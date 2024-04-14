package gdsc.comunity.service.channel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.user.UserChannel;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import gdsc.comunity.repository.user.UserRepository;
import gdsc.comunity.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//TODO : Test code 작성
@ExtendWith(SpringExtension.class)
@SpringBootTest
//@ActiveProfiles("test") // Ensure you use the 'test' profile to avoid using the production database
@Transactional // Ensure that each test is independently rolled back after execution
public class ChannelServiceImplTest {
    @Autowired
    private ChannelServiceImpl channelService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserChannelRepository userChannelRepository;

    @Test
    void createChannel_Success() {
        // Arrange
        User user = User.builder()
                .email("email")
                .profileImageUrl("image")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user = userRepository.save(user);

        // Act
        Channel channel = channelService.createChannel(user.getId(), "New Channel");

        // Assert
        assertNotNull(channel.getId());
        assertEquals("New Channel", channel.getChannelName());
        assertEquals(user.getId(), channel.getManager().getId());

        Optional<UserChannel> userChannel = userChannelRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(userChannel.isPresent());
    }

    @Test
    void leaveChannel_Success() {
        // Arrange
        User user = User.builder()
                .email("email")
                .profileImageUrl("image")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user = userRepository.save(user);

        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);

        Channel channel = channelService.createChannel(manager.getId(), "New Channel");
        // TODO : user join to channel

        // Act
        channelService.leaveChannel(manager.getId(), channel.getId());

        // Assert
        Optional<UserChannel> userChannel = userChannelRepository.findByUserIdAndChannelId(manager.getId(), channel.getId());
        assertTrue(userChannel.isEmpty());

        Optional<Channel> channelOptional = channelRepository.findById(channel.getId());
        assertTrue(channelOptional.isPresent());
        assertEquals(user.getId(), channelOptional.get().getManager().getId());
    }

    @Test
    void deleteChannel_Failure_NotManager() {
        // Arrange
        User user = User.builder()
                .email("email")
                .profileImageUrl("image")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user = userRepository.save(user);

        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);

        Channel channel = channelService.createChannel(manager.getId(), "New Channel");

        Long userId = user.getId();
        Long channelId = channel.getId();

        // Act && Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            channelService.deleteChannel(userId, channelId);
        });
        assertEquals("Only manager can delete channel.", exception.getMessage());
    }

    @Test
    void searchChannel_Success() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);
        Channel channel = channelService.createChannel(manager.getId(), "New Channel");

        // Act
        ChannelInfoDto result = channelService.searchChannel(channel.getId());

        // Assert
        assertThat(result.getChannelName()).isEqualTo("New Channel");
    }
}