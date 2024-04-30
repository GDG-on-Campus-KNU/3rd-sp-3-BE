package gdsc.comunity.repository.userchannel;


import gdsc.comunity.entity.userchannel.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChannelJpaRepository extends JpaRepository<UserChannel, Long> {
    @Query(value = "SELECT user_id FROM user_channel WHERE nickname = :senderNickname AND channel_id = :channelId", nativeQuery = true)
    Optional<Long> findUserIdByNicknameAndChannel(@Param("senderNickname") String senderNickname,
                                                  @Param("channelId") Long channelId);

    List<UserChannel> findTop2ByChannelIdOrderByCreatedDateAsc(Long channelId);

    Optional<UserChannel> findByUserIdAndChannelId(Long userId, Long channelId);

    void deleteAllByChannelId(Long channelId);

    List<UserChannel> findAllByChannelId(Long channelId);

    Optional<UserChannel> findByUserId(Long userId);
}
