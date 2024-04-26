package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.channel.ChannelJoinRequest;
import gdsc.comunity.entity.user.User;

import java.util.List;

public interface ChannelService {
    Channel createChannel(Long userId, String channelName);

    void leaveChannel(Long userId, Long channelId);

    void deleteChannel(Long userId, Long channelId);

    ChannelInfoDto searchChannel(Long channelId);

    void approveJoinChannel(Long userId, Long targetUserId, Long channelId);

    void sendJoinRequest(String nickname, Long userId, Long channelId);

    List<ChannelJoinRequest> searchJoinRequest(Long userId, Long channelId);

    void changeNickname(Long userId, Long channelId, String nickname);

    void doubleCheckNicknameThrowException(Long channelId, String nickname);
}
