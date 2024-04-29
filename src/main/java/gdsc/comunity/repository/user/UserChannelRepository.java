package gdsc.comunity.repository.user;

import java.util.Map;

public interface UserChannelRepository {

    Long getUserIdByNicknameAndChannel(String senderNickname, Long channel);

    Map<Long, String> getNicknameMapByChannelId(Long channelId);
}
