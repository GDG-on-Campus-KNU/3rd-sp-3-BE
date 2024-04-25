package gdsc.comunity.entity.channel;

import gdsc.comunity.entity.common.BaseTimeEntity;
import gdsc.comunity.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChannelJoinRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(nullable = false)
    String nickname;

    @Builder
    private ChannelJoinRequest(User user, Channel channel, String nickname){
        this.user = user;
        this.channel = channel;
        this.nickname = nickname;
    }

}
