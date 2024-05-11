package gdsc.comunity.dto.chat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatStateDto {
    public final String userNickname;
    public final ConnectionState connectionState;
}
