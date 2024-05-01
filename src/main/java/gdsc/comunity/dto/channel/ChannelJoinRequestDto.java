package gdsc.comunity.dto.channel;

import lombok.Getter;

@Getter
public class ChannelJoinRequestDto {
    Long id;
    Long userId;
    Long channelId;
    String nickname;

    public ChannelJoinRequestDto(Long id, Long userId, Long channelId, String nickname) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.nickname = nickname;
    }
}
