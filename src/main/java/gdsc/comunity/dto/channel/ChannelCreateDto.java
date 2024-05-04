package gdsc.comunity.dto.channel;

import lombok.Getter;

@Getter
public class ChannelCreateDto {
    private String channelName;
    private String nickname;

    public ChannelCreateDto(String channelName, String nickname) {
        this.channelName = channelName;
        this.nickname = nickname;
    }
}
