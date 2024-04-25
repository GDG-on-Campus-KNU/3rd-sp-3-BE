package gdsc.comunity.dto.channel;

import gdsc.comunity.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ApproveJoinChannelDto {
    private Long userId;
    private Long channelId;
}
