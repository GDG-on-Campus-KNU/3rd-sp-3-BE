package gdsc.comunity.dto.channel;

import lombok.Getter;

@Getter
public class ChannelNicknameDto {
    private String nickname;
    private Long channelId;

    public ChannelNicknameDto(String nickname, Long channelId) {
        this.nickname = nickname;
        this.channelId = channelId;
    }
}
