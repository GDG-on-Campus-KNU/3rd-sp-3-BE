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
import java.util.Optional;

@Service
@RequiredArgsConstructor
// TODO : Exception handling
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;
    private final UserRepository userRepository;
    private final ChannelJoinRequestRepository channelJoinRequestRepository;

    @Override
    public Channel createChannel(Long userId, String channelName) {
        User user = userRepository.findById(userId).orElseThrow();
        Channel newChannel = Channel.builder()
                .channelName(channelName)
                .manager(user)
                .build();
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    @Transactional
    public void leaveChannel(Long userId, Long channelId) {
        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        if (!(Objects.equals(channel.getManager().getId(), user.getId()))) {
            userChannelRepository.findByUserIdAndChannelId(user.getId(), channelId).ifPresentOrElse(
                    userChannelRepository::delete,
                    () -> {
                        throw new IllegalArgumentException("You are not in this channel.");
                    }
            );
            return;
        }

        UserChannel newManagerUserChannel = userChannelRepository.findSecondByChannelIdOrderByCreatedDateDesc(channelId)
                .orElseThrow(() -> new IllegalArgumentException("There is no manager in this channel."));
        channel.updateManager(newManagerUserChannel.getUser());
        channelRepository.save(channel);

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

        return new ChannelInfoDto(
                channel.getChannelName(),
                channel.getCreatedDate().toString(),
                manager.getNickname(),
                channelUsers
        );
    }

    @Override
    public void sendJoinRequest(String nickname, Long userId, Long channelId) {
        userChannelRepository.findAllByChannelId(channelId).stream()
                .filter(userChannel -> userChannel.getNickname().equals(nickname))
                .findAny()
                .ifPresent(userChannel -> {
                    throw new IllegalArgumentException("Nickname is already exist in this channel.");
                });

        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        channelJoinRequestRepository.save(ChannelJoinRequest
                .builder()
                .channel(channel)
                .user(user)
                .nickname(nickname)
                .build());
    }

    @Override
    public void approveJoinChannel(Long userId, Long targetUserId, Long channelId){
    }

    @Override
    public List<Object> searchJoinRequest(Long userId, Long channelId) {
        return null;
    }

    @Override
    public void changeNickname(Long userId, String nickname){

    }
}
