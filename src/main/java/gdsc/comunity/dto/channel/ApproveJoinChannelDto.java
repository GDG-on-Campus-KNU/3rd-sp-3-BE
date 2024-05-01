package gdsc.comunity.dto.channel;

import lombok.Getter;

@Getter
public class ApproveJoinChannelDto {
    private Long userId;
    private Long channelId;

    public ApproveJoinChannelDto(Long userId, Long channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }
}
