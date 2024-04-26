package gdsc.comunity.repository.user;


import gdsc.comunity.entity.user.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {
    List<UserChannel> findTop2ByChannelIdOrderByCreatedDateAsc(Long channelId);

    Optional<UserChannel> findByUserIdAndChannelId(Long userId, Long channelId);

    void deleteAllByChannelId(Long channelId);

    List<UserChannel> findAllByChannelId(Long channelId);

    Optional<UserChannel> findByUserId(Long userId);
}
