package gdsc.comunity.repository.chat;

import gdsc.comunity.entity.chat.ChatLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository{
    private final ChatLogJpaRepository chatLogJpaRepository;

    @Override
    public void save(ChatLog entity) {
        chatLogJpaRepository.save(entity);
    }

    @Override
    public Page<ChatLog> findChatLogByPage(Long channelId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 50);
        return chatLogJpaRepository.findAllByChannelIdOrderBySendTimeDesc(channelId, pageRequest);
    }
}
