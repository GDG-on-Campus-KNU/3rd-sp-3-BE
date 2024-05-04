package gdsc.comunity.dto.chat;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.chat.ChatLog;

import java.time.LocalDateTime;

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

    public Chatting(String message, String senderNickname, LocalDateTime time) {
        this.message = message;
        this.senderNickname = senderNickname;
        this.time = time;
    }
}
