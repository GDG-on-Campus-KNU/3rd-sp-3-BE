package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.User;

import java.util.List;

public interface ChannelService {
    Channel createChannel(Long userId, String channelName);

    void leaveChannel(Long userId, Long channelId);

    void deleteChannel(Long userId, Long channelId);

    ChannelInfoDto searchChannel(Long channelId);

    void approveJoinChannel(Long id, Long userId, Long channelId);

    void sendJoinRequest(String nickname, Long id, Long channelId);

    List<Object> searchJoinRequest(Long id, Long channelId);

    void changeNickname(Long id, String nickname);
}
