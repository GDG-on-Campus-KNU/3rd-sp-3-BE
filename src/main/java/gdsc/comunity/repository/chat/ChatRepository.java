package gdsc.comunity.repository.chat;

import gdsc.comunity.entity.chat.ChatLog;
import org.springframework.data.domain.Page;

public interface ChatRepository {
    void save(ChatLog entity);

    Page<ChatLog> findChatLogByPage(Long channelId, int page);
}
