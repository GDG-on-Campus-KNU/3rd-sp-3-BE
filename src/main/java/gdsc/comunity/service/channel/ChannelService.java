package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.User;

public interface ChannelService {
    Channel createChannel(Long userId, String channelName);

    void leaveChannel(Long userId, Long channelId);

    void deleteChannel(Long userId, Long channelId);

    ChannelInfoDto searchChannel(Long channelId);
}
