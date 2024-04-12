package gdsc.comunity.repository.user;


import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChannelJpaRepository extends JpaRepository<UserChannel, Long> {
    Optional<Long> findUserIdByNicknameAndChannel(String senderNickname, Channel channel);
}
