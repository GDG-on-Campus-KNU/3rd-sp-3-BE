package gdsc.comunity.dto.chat;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.chat.ChatLog;
import gdsc.comunity.entity.chat.ChatType;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Chatting {
    public final String message;
    public final String senderNickname;
    public final ChatType type;
    public final LocalDateTime time;

    public ChatLog toEntity(Channel channel, Long senderId, ChatType type) {
        return ChatLog.builder()
                .senderId(senderId)
                .type(type)
                .content(message)
                .channel(channel)
                .sendTime(time)
                .build();
    }
}
