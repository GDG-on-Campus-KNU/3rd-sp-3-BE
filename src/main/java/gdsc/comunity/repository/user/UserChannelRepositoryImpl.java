package gdsc.comunity.repository.user;

import gdsc.comunity.entity.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserChannelRepositoryImpl implements UserChannelRepository{
    private final UserChannelJpaRepository userChannelJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long getUserIdByNicknameAndChannel(String senderNickname, Channel channel) {
        return userChannelJpaRepository.findUserIdByNicknameAndChannel(senderNickname, channel.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    @Override
    public Map<Long, String> getNicknameMapByChannelId(Long channelId) {
        String sql = "SELECT uc.user_id, uc.nickname FROM user_channel uc WHERE uc.channel_id = ?";
        return jdbcTemplate.query(sql, new Object[]{channelId}, rs -> {
            Map<Long, String> resultMap = new HashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getLong("user_id"), rs.getString("nickname"));
            }
            return resultMap;
        });
    }
}
