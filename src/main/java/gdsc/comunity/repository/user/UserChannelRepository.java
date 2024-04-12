package gdsc.comunity.repository.user;

import gdsc.comunity.entity.channel.Channel;

import java.util.Map;

public interface UserChannelRepository {

    Long getUserIdByNicknameAndChannel(String senderNickname, Channel channel);

    Map<Long, String> getNicknameMapByChannelId(Long channelId);
}
