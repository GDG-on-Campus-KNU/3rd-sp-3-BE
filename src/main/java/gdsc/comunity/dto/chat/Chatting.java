package gdsc.comunity.dto.chat;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.chat.ChatLog;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Chatting {
    public final String message;
    public final String senderNickname;
    public final LocalDateTime time;

    public ChatLog toEntity(Channel channel, Long senderId) {
        return ChatLog.builder()
                .senderId(senderId)
                .content(message)
                .channel(channel)
                .sendTime(time)
                .build();
    }
}
