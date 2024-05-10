package gdsc.comunity.dto.chat;

import gdsc.comunity.entity.chat.ChatLog;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public class PagingChatting {
    public final boolean hasNext;
    public final List<Chatting> chattingList;

    private PagingChatting(boolean hasNext, List<Chatting> chattingList) {
        this.hasNext = hasNext;
        this.chattingList = chattingList;
    }

    public static PagingChatting of(Page<ChatLog> chatLogPage, Map<Long, String> nicknameMap) {
        List<ChatLog> chatLogList = chatLogPage.getContent();
        List<Chatting> chattingList = chatLogList.stream()
                .map(chatLog -> new Chatting(chatLog.getContent(), nicknameMap.get(chatLog.getSenderId()),
                        chatLog.getType(), chatLog.getSendTime()))
                .toList();

        return new PagingChatting(chatLogPage.hasNext(), chattingList);
    }
}
