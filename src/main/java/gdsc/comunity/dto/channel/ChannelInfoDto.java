package gdsc.comunity.dto.channel;

import gdsc.comunity.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ChannelInfoDto {
    /**
     * {
     * 	"channelName": "채널명",
     * 	"openDate": "2024-04-06",
     *   "managerNickname": "관리자 닉네임",
     *   "channelUsers": [
     *     {
     *       "id": 1,
     *       "nickname": "nick1"
     *     },
     *     {
     *       "id": 2,
     *       "nickname": "nick2"
     *     },
     *     {
     *       "id": 3,
     *       "nickname": "nick3"
     *     }
     *   ]
     * }
     */
    private String channelName;
    private String openDate;
    private String managerNickname;
    private List<User> channelUsers;
}
