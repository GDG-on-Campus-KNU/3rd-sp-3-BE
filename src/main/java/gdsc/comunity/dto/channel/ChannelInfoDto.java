package gdsc.comunity.dto.channel;

import gdsc.comunity.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ChannelInfoDto {
    private String channelName;
    private String openDate;
    private String managerNickname;
    private List<User> channelUsers;
}
