package gdsc.comunity.repository.user;


import gdsc.comunity.entity.user.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserChannelJpaRepository extends JpaRepository<UserChannel, Long> {
    @Query(value = "SELECT user_id FROM user_channel WHERE nickname = :senderNickname AND channel_id = :channelId", nativeQuery = true)
    Optional<Long> findUserIdByNicknameAndChannel(@Param("senderNickname") String senderNickname,
                                                  @Param("channelId") Long channelId);
}
