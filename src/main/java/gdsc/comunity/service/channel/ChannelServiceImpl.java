package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.dto.channel.ChannelJoinRequestDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.channel.ChannelJoinRequest;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.userchannel.UserChannel;
import gdsc.comunity.repository.channel.ChannelJoinRequestRepository;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserRepository;
import gdsc.comunity.repository.userchannel.UserChannelJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserChannelJpaRepository userChannelJpaRepository;
    private final UserRepository userRepository;
    private final ChannelJoinRequestRepository channelJoinRequestRepository;

    @Override
    @Transactional
    public Channel createChannel(Long userId, String channelName, String nickname) {
        User user = findUserByIdThrowException(userId);

        Channel newChannel = Channel.builder()
                .channelName(channelName)
                .manager(user)
                .build();

        UserChannel userChannel = UserChannel.builder()
                .channel(newChannel)
                .user(user)
                .nickname(nickname)
                .build();

        newChannel.addUserChannel(userChannel);
        channelRepository.save(newChannel);

        return newChannel;
    }

    @Override
    @Transactional
    public void leaveChannel(Long userId, Long channelId) {
        User user = findUserByIdThrowException(userId);
        Channel channel = findChannelByIdThrowException(channelId);

        // 대상이 매니저가 아닌 경우, UserChannel 삭제
        if (!(Objects.equals(channel.getManager().getId(), user.getId()))) {
            UserChannel userChannel = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channelId).orElseThrow(
                    () -> new IllegalStateException("사용자 본인이 해당 채널에 속해있지 않습니다.")
            );
            channel.removeUserChannel(userChannel);
            channelRepository.save(channel);
            userChannelJpaRepository.delete(userChannel);
            return;
        }

        // 대상이 매니저인 경우, 채널 매니저 변경
        List<UserChannel> userChannelList = userChannelJpaRepository.findTop2ByChannelIdOrderByCreatedDateAsc(channelId);
        // list의 size가 2이상인 경우 진행 아니면 exception
        if (userChannelList.size() < 2) {
            throw new IllegalStateException("채널에 다른 사용자가 없어 채널을 탈퇴할 수 없습니다. 채널 삭제를 진행해주세요.");
        }

        UserChannel deleteUserChannel = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channelId).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 채널에 속해있지 않습니다.")
        );
        userChannelJpaRepository.delete(deleteUserChannel);

        channel.updateManager(userChannelList.get(1).getUser());
        channel.removeUserChannel(deleteUserChannel);
        channelRepository.save(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(Long userId, Long channelId) {
        User user = findUserByIdThrowException(userId);
        Channel channel = findChannelByIdThrowException(channelId);

        // 채널 매니저만 채널 삭제 가능
        if (!(Objects.equals(channel.getManager().getId(), user.getId()))) {
            throw new IllegalStateException("채널 매니저만 채널을 삭제할 수 있습니다.");
        }

        channelRepository.delete(channel);
    }

    @Override
    public ChannelInfoDto searchChannel(Long channelId) {
        Channel channel = findChannelByIdThrowException(channelId);
        List<UserChannel> userChannels = userChannelJpaRepository.findAllByChannelId(channelId);
        List<User> channelUsers = userChannels.stream().map(UserChannel::getUser).toList();

        UserChannel manager = userChannelJpaRepository.findByUserIdAndChannelId(channel.getManager().getId(), channelId).orElseThrow(
                () -> new IllegalArgumentException("해당 채널의 매니저가 존재하지 않습니다.")
        );

        // 요청한 채널의 정보(채널 이름, 생성일, 매니저 닉네임, 채널 유저 리스트) 반환
        return new ChannelInfoDto(
                channel.getChannelName(),
                channel.getCreatedDate().toString(),
                manager.getNickname(),
                userChannels,
                channelUsers
        );
    }

    @Override
    public void sendJoinRequest(String nickname, Long userId, Long channelId) {
        doubleCheckNicknameThrowException(channelId, nickname);

        User user = findUserByIdThrowException(userId);
        Channel channel = findChannelByIdThrowException(channelId);

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
        User user = findUserByIdThrowException(userId);
        Channel channel = findChannelByIdThrowException(channelId);

        checkManagerThrowException(userId, channel.getManager().getId());

        // 채널 가입 요청을 승인하고 UserChannel에 저장. 이후 ChannelJoinRequest 삭제
        ChannelJoinRequest channelJoinRequest = channelJoinRequestRepository.findByUserIdAndChannelId(targetUserId, channelId).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자의 채널 가입 요청이 존재하지 않습니다.")
        );

        userChannelJpaRepository.save(UserChannel.builder()
                .channel(channel)
                .user(channelJoinRequest.getUser())
                .nickname(channelJoinRequest.getNickname())
                .build());
        channelJoinRequestRepository.delete(channelJoinRequest);
    }

    @Override
    public List<ChannelJoinRequestDto> searchJoinRequest(Long userId, Long channelId) {
        Channel channel = findChannelByIdThrowException(channelId);
        checkManagerThrowException(userId, channel.getManager().getId());

        return channelJoinRequestRepository.findAllByChannelId(channelId).stream().map(
                channelJoinRequest -> new ChannelJoinRequestDto(
                        channelJoinRequest.getId(),
                        channelJoinRequest.getUser().getId(),
                        channelJoinRequest.getChannel().getId(),
                        channelJoinRequest.getNickname()
                )
        ).toList();
    }

    @Override
    public void changeNickname(Long userId, Long channelId, String nickname) {
        // 닉네임 중복 확인 후 변경.
        doubleCheckNicknameThrowException(channelId, nickname);

        UserChannel userChannel = userChannelJpaRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 채널에 속해있지 않습니다.")
        );
        userChannel.updateNickname(nickname);
        userChannelJpaRepository.save(userChannel);
    }

    @Override
    public void doubleCheckNicknameThrowException(Long channelId, String nickname) {
        // 닉네임 중복 시 throw Exception
        userChannelJpaRepository.findAllByChannelId(channelId).stream()
                .filter(userChannel -> userChannel.getNickname().equals(nickname))
                .findAny()
                .ifPresent(userChannel -> {
                    throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
                });
    }

    @Override
    public void checkManagerThrowException(Long userId, Long managerId) {
        if (!Objects.equals(userId, managerId)) {
            throw new IllegalArgumentException("해당 채널의 매니저가 아닙니다.");
        }
    }

    @Override
    public User findUserByIdThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public Channel findChannelByIdThrowException(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }
}
