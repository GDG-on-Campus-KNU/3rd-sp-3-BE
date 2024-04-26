package gdsc.comunity.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChannelJoinRequestDto {
    Long id;
    Long userId;
    Long channelId;
    String nickname;
}
