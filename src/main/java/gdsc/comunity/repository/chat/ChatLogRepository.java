package gdsc.comunity.repository.chat;

import gdsc.comunity.entity.chat.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
}
