package gdsc.comunity.repository.chat;

import gdsc.comunity.entity.chat.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogJpaRepository extends JpaRepository<ChatLog, Long> {
    Page<ChatLog> findAllByOrderBySendTimeDesc(PageRequest pageRequest);
}
