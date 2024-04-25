package gdsc.comunity.repository.channel;

import gdsc.comunity.entity.channel.ChannelJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelJoinRequestRepository extends JpaRepository<ChannelJoinRequest, Long> {
    Optional<ChannelJoinRequest> findByUserIdAndChannelId(Long targetUserId, Long userId);
}
