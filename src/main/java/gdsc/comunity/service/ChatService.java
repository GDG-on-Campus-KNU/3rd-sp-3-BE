package gdsc.comunity.service;

import gdsc.comunity.dto.chat.PagingChatting;
import gdsc.comunity.dto.chat.Chatting;
import gdsc.comunity.entity.chat.ChatLog;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.chat.ChatRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;

    @Async
    public void saveChat(Chatting chatting, Long channelId) {
        var channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다."));

        Long userId = userChannelRepository.getUserIdByNicknameAndChannel(chatting.senderNickname, channel);

        chatRepository.save(chatting.toEntity(channel, userId));
    }

    public PagingChatting getChatLog(Long channelId, int page) {
        Page<ChatLog> chatLogPage = chatRepository.findChatLogByPage(channelId, page);

        Map<Long, String> nicknameMap = userChannelRepository.getNicknameMapByChannelId(channelId);

        return PagingChatting.of(chatLogPage, nicknameMap);
    }
}
