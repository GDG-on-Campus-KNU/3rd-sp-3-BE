package gdsc.comunity.service.channel;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import gdsc.comunity.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;

    @Override
    public Channel createChannel(User user, String channelName) {
        Channel newChannel = Channel.builder()
                .channelName(channelName)
                .manager(user)
                .build();
        channelRepository.save(newChannel);
        return newChannel;
    }

    @Override
    public void leaveChannel(User user) {

    }

    @Override
    public void deleteChannel(User user) {

    }

    @Override
    public Channel searchChannel(Long channelId) {
        return null;
    }
}
