package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.channel.ChannelJoinRequest;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.user.UserChannel;
import gdsc.comunity.repository.channel.ChannelJoinRequestRepository;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import gdsc.comunity.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
// TODO : Exception handling
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;
    private final UserRepository userRepository;
    private final ChannelJoinRequestRepository channelJoinRequestRepository;

    @Override
    public Channel createChannel(Long userId, String channelName, String nickname) {
        User user = userRepository.findById(userId).orElseThrow();
        Channel newChannel = Channel.builder()
                .channelName(channelName)
                .manager(user)
                .build();
        channelRepository.save(newChannel);

        UserChannel userChannel = UserChannel.builder()
                .channel(newChannel)
                .user(user)
                .nickname(nickname)
                .build();
        userChannelRepository.save(userChannel);

        return newChannel;
    }

    @Override
    @Transactional
    public void leaveChannel(Long userId, Long channelId) {
        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        // 대상이 매니저가 아닌 경우, UserChannel 삭제
        if (!(Objects.equals(channel.getManager().getId(), user.getId()))) {
            userChannelRepository.findByUserIdAndChannelId(user.getId(), channelId).ifPresentOrElse(
                    userChannelRepository::delete,
                    () -> {
                        throw new IllegalArgumentException("You are not in this channel.");
                    }
            );
            return;
        }

        // 대상이 매니저인 경우, 채널 매니저 변경
        List<UserChannel> userChannelList = userChannelRepository.findTop2ByChannelIdOrderByCreatedDateAsc(channelId);
        // list의 size가 2이상인 경우 진행 아니면 exception
        if (userChannelList.size() < 2) {
            throw new IllegalArgumentException("There is no user to be a manager.");
        }
        UserChannel newManagerUserChannel = userChannelList.get(1);
        channel.updateManager(newManagerUserChannel.getUser());
        channelRepository.save(channel);

        // 이후 대상의 UserChannel 삭제
        UserChannel deleteUserChannel = userChannelRepository.findByUserIdAndChannelId(user.getId(), channelId).orElseThrow();
        userChannelRepository.delete(deleteUserChannel);
    }

    @Override
    @Transactional
    public void deleteChannel(Long userId, Long channelId) {
        User user = userRepository.findById(userId).orElseThrow();

        // 채널 매니저만 채널 삭제 가능
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        if (!(Objects.equals(channel.getManager().getId(), user.getId()))) {
            throw new IllegalArgumentException("Only manager can delete channel.");
        }

        // 연관된 UserChannel 및 Channel 삭제
        userChannelRepository.deleteAllByChannelId(channelId);
        channelRepository.delete(channel);
    }

    @Override
    public ChannelInfoDto searchChannel(Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        List<User> channelUsers = userChannelRepository.findAllByChannelId(channelId).stream().map(UserChannel::getUser).toList();
        UserChannel manager = userChannelRepository.findByUserIdAndChannelId(channel.getManager().getId(), channelId).orElseThrow();

        // 요청한 채널의 정보(채널 이름, 생성일, 매니저 닉네임, 채널 유저 리스트) 반환
        return new ChannelInfoDto(
                channel.getChannelName(),
                channel.getCreatedDate().toString(),
                manager.getNickname(),
                channelUsers
        );
    }

    @Override
    public void sendJoinRequest(String nickname, Long userId, Long channelId) {
        doubleCheckNicknameThrowException(channelId, nickname);

        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        // 사용자에 대한 채널 가입 요청 저장
        channelJoinRequestRepository.save(ChannelJoinRequest
                .builder()
                .channel(channel)
                .user(user)
                .nickname(nickname)
                .build());
    }

    @Override
    @Transactional
    public void approveJoinChannel(Long userId, Long targetUserId, Long channelId) {
        // 채널 가입 요청을 승인하고 UserChannel에 저장. 이후 ChannelJoinRequest 삭제
        ChannelJoinRequest channelJoinRequest = channelJoinRequestRepository.findByUserIdAndChannelId(targetUserId, channelId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        userChannelRepository.save(UserChannel.builder()
                .channel(channel)
                .user(channelJoinRequest.getUser())
                .nickname(channelJoinRequest.getNickname())
                .build());
        channelJoinRequestRepository.delete(channelJoinRequest);
    }

    @Override
    public List<ChannelJoinRequest> searchJoinRequest(Long userId, Long channelId) {
        // 채널의 매니저가 이나라면 예외 발생
        Channel channel = channelRepository.findById(channelId).
                orElseThrow(() -> new IllegalArgumentException("Channel does not exist."));
        if (!Objects.equals(channel.getManager().getId(), userId)) {
            throw new IllegalArgumentException("Only manager can search join request.");
        }
        return channelJoinRequestRepository.findAllByChannelId(channelId);
    }

    @Override
    public void changeNickname(Long userId, Long channelId, String nickname) {
        // 닉네임 중복 확인 후 변경.
        doubleCheckNicknameThrowException(channelId, nickname);

        UserChannel userChannel = userChannelRepository.findByUserId(userId).orElseThrow();
        userChannel.updateNickname(nickname);
        userChannelRepository.save(userChannel);
    }

    @Override
    public void doubleCheckNicknameThrowException(Long channelId, String nickname) {
        // 닉네임 중복 시 throw Exception
        userChannelRepository.findAllByChannelId(channelId).stream()
                .filter(userChannel -> userChannel.getNickname().equals(nickname))
                .findAny()
                .ifPresent(userChannel -> {
                    throw new IllegalArgumentException("Nickname is already exist in this channel.");
                });
    }
}
