package gdsc.comunity.dto.channel;

import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.user.UserChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChannelInfoDto {

    @Getter
    public static class UserDto {
        private Long id;
        private String email;
        private String nickname;
        private String profileImageUrl;

        public UserDto(String nickname, User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = nickname;
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }

    private String channelName;
    private String openDate;
    private String managerNickname;
    private List<UserDto> channelUsers;

    public ChannelInfoDto(String channelName, String openDate, String managerNickname, List<UserChannel> userChannelList, List<User> userList) {
        this.channelName = channelName;
        this.openDate = openDate;
        this.managerNickname = managerNickname;
        this.channelUsers = new ArrayList<>();

        for (UserChannel userChannel : userChannelList) {
            for (User user : userList) {
                channelUsers.add(new UserDto(userChannel.getNickname(), user));
            }
        }
    }
}
