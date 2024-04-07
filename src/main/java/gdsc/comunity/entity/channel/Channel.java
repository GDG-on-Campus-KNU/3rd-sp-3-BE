package gdsc.comunity.entity.channel;


import gdsc.comunity.entity.common.BaseTimeEntity;
import gdsc.comunity.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Channel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

/*
    fixme:
    현재 채널마다 음성채팅방1개, 채팅방1개를 가지고 있습니다. 그냥 Channel id로 식별이 가능해서 필요없을 거 같아요.
    필요없다고 생각되시면 suggest로 해당 사항 삭제해주시면 됩니다.

    @Column(unique = true)
    private Long chatRoomId;

    @Column(unique = true)
    private Long voiceRoomId;
*/

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Builder
    private Channel(User manager) {
        this.manager = manager;
    }

}
