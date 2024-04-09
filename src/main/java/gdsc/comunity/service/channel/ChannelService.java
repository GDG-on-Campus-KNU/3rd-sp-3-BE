package gdsc.comunity.service.channel;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.User;

public interface ChannelService {
    Channel createChannel(User user, String channelName);

    void leaveChannel(User user);

    void deleteChannel(User user);

    Channel searchChannel(Long channelId);
}
