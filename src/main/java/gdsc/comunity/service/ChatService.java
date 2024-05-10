package gdsc.comunity.service;

import gdsc.comunity.dto.chat.PagingChatting;
import gdsc.comunity.dto.chat.Chatting;
import gdsc.comunity.entity.chat.ChatLog;
import gdsc.comunity.entity.chat.ChatType;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.chat.ChatRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChannelRepository channelRepository;
    private final UserChannelRepository userChannelRepository;
    private final S3FileService s3FileService;

    @Async
    public void saveChat(Chatting chatting, Long channelId) {
        var channel = channelRepository.getReferenceById(channelId);

        Long userId = userChannelRepository.getUserIdByNicknameAndChannel(chatting.senderNickname, channelId);

        chatRepository.save(chatting.toEntity(channel, userId, chatting.type));
    }

    public PagingChatting getChatLog(Long channelId, int page) {
        Page<ChatLog> chatLogPage = chatRepository.findChatLogByPage(channelId, page);

        Map<Long, String> nicknameMap = userChannelRepository.getNicknameMapByChannelId(channelId);

        return PagingChatting.of(chatLogPage, nicknameMap);
    }

    public Chatting saveChatFile(Long channelId, String userNickname, MultipartFile file) {
        String s3filename = s3FileService.uploadFile(file);

        String message = s3filename + "/" + file.getSize();

        Chatting chatting = new Chatting(message, userNickname, ChatType.FILE, LocalDateTime.now());

        saveChat(chatting, channelId);

        return chatting;
    }

    public Chatting saveChatImage(Long channelId, String userNickname, MultipartFile[] images) {
        StringBuilder message = new StringBuilder();
        for (MultipartFile image : images) {
            String url = s3FileService.uploadImage(image);
            message.append(url).append("/")
                .append(image.getOriginalFilename()).append("/")
                .append(image.getSize()).append(";");
        }

        Chatting chatting = new Chatting(message.toString(), userNickname, ChatType.IMAGE, LocalDateTime.now());

        saveChat(chatting, channelId);

        return chatting;
    }

    public File getChatFile(String fileCode) {
        return s3FileService.downloadFile(fileCode);
    }
}
