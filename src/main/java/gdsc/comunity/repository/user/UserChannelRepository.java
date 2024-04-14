package gdsc.comunity.repository.user;


import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.user.UserChannel;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {
    Optional<UserChannel> findSecondByChannelIdOrderByCreatedDateDesc(Long channelId);

    Optional<UserChannel> findByUserIdAndChannelId(Long id, Long channelId);

    void deleteAllByChannelId(Long channelId);

    List<UserChannel> findAllByChannelId(Long channelId);
}
