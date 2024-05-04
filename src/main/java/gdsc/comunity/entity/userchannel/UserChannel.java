package gdsc.comunity.entity.userchannel;

import gdsc.comunity.entity.channel.Channel;
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
public class UserChannel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder
    private UserChannel(String nickname, User user, Channel channel) {
        this.nickname = nickname;
        this.user = user;
        this.channel = channel;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}