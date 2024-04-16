package gdsc.comunity.repository.chat;

import gdsc.comunity.entity.chat.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatLogJpaRepository extends JpaRepository<ChatLog, Long> {
    @Query("SELECT cl FROM ChatLog cl WHERE cl.channel.id = :channelId ORDER BY cl.sendTime DESC")
    Page<ChatLog> findAllByChannelIdOrderBySendTimeDesc(Long channelId, PageRequest pageRequest);
}
