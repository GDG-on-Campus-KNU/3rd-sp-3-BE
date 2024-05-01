package gdsc.comunity.service.channel;

import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.channel.ChannelJoinRequest;
import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.entity.userchannel.UserChannel;
import gdsc.comunity.repository.channel.ChannelJoinRequestRepository;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserRepository;
import gdsc.comunity.repository.userchannel.UserChannelJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChannelServiceImplTest {
    private final ChannelServiceImpl channelService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final UserChannelJpaRepository userChannelJpaRepository;
    private final ChannelJoinRequestRepository channelJoinRequestRepository;

    @Autowired
    public ChannelServiceImplTest(ChannelServiceImpl channelService, UserRepository userRepository, ChannelRepository channelRepository, UserChannelJpaRepository userChannelJpaRepository, ChannelJoinRequestRepository channelJoinRequestRepository) {
        this.channelService = channelService;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.userChannelJpaRepository = userChannelJpaRepository;
        this.channelJoinRequestRepository = channelJoinRequestRepository;
    }

    @Test
    @DisplayName("채널 생성 성공")
    void createChannel_Success() {
        // set env
        User user = User.builder()
                .email("email")
                .profileImageUrl("image")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user = userRepository.save(user);

        // do
        Channel channel = channelService.createChannel(user.getId(), "New Channel", "nickname");

        // assert result
        assertNotNull(channel.getId());
        assertEquals("New Channel", channel.getChannelName());
        assertEquals(user.getId(), channel.getManager().getId());

        Optional<UserChannel> userChannel = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(userChannel.isPresent());
    }

    @Test
    @DisplayName("채널 가입 요청 성공")
    void sendJoinRequest_Success() {
        // set env
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

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        // do
        channelService.sendJoinRequest("nickname2", user.getId(), channel.getId());

        // assert result
        Optional<UserChannel> userChannel = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(userChannel.isEmpty());

        Optional<ChannelJoinRequest> channelJoinRequest = channelJoinRequestRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(channelJoinRequest.isPresent());
    }

    @Test
    @DisplayName("채널 가입 요청 승인 성공")
    void approveJoinChannel_Success() {
        // set env
        User user = User.builder()
                .email("email2")
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

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        channelService.sendJoinRequest("nickname2", user.getId(), channel.getId());

        // do
        channelService.approveJoinChannel(manager.getId(), user.getId(), channel.getId());

        // assert result
        Optional<UserChannel> userChannel = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(userChannel.isPresent());

        Optional<ChannelJoinRequest> channelJoinRequest = channelJoinRequestRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(channelJoinRequest.isEmpty());
    }

    @Test
    @DisplayName("채널 나가기 성공 - 매니저가 아닌 경우")
    void leaveChannel_Success_WithNormalUser() {
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

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");
        channelService.sendJoinRequest("nickname2", user.getId(), channel.getId());
        channelService.approveJoinChannel(manager.getId(), user.getId(), channel.getId());

        // Act
        channelService.leaveChannel(user.getId(), channel.getId());

        // Assert
        Optional<UserChannel> userChannelOptional = userChannelJpaRepository.findByUserIdAndChannelId(user.getId(), channel.getId());
        assertTrue(userChannelOptional.isEmpty());

        Optional<Channel> channelOptional = channelRepository.findById(channel.getId());
        assertTrue(channelOptional.isPresent());
        assertEquals(manager.getId(), channelOptional.get().getManager().getId());
    }

    @Test
    @DisplayName("채널 나가기 실패 - 매니저인 경우, 매니저를 넘겨줄 사람이 없는 경우")
    void leaveChannel_Failure_WithManager_NoMoreUser() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        // Act & Assert
        Long managerId = manager.getId();
        Long channelId = channel.getId();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            channelService.leaveChannel(managerId, channelId);
        });
    }

    @Test
    @DisplayName("UserChannel respository 메소드 테스트 - findTop2ByChannelIdOrderByCreatedDateDesc")
    void userRepositoryFindTop2ByChannelIdOrderByCreatedDateDesc() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);

        User user1 = User.builder()
                .email("email1")
                .profileImageUrl("image1")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user1 = userRepository.save(user1);

        User user2 = User.builder()
                .email("email2")
                .profileImageUrl("image2")
                .provider(Provider.GOOGLE)
                .providerId("3")
                .build();
        user2 = userRepository.save(user2);

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");
        channelService.sendJoinRequest("nickname1", user1.getId(), channel.getId());
        channelService.sendJoinRequest("nickname2", user2.getId(), channel.getId());
        channelService.approveJoinChannel(manager.getId(), user1.getId(), channel.getId());
        channelService.approveJoinChannel(manager.getId(), user2.getId(), channel.getId());

        // Act
        List<UserChannel> listUserChannel = userChannelJpaRepository.findTop2ByChannelIdOrderByCreatedDateAsc(channel.getId());

        // Assert
        assertEquals(2, listUserChannel.size());
    }

    @Test
    @DisplayName("채널 나가기 성공 - 매니저인 경우, 매니저를 넘겨줄 사람이 있는 경우")
    void leaveChannel_Success_WithManager_HasMoreUser() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);

        User user = User.builder()
                .email("email")
                .profileImageUrl("image")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();
        user = userRepository.save(user);

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");
        channelService.sendJoinRequest("nickname2", user.getId(), channel.getId());
        channelService.approveJoinChannel(manager.getId(), user.getId(), channel.getId());

        // Act
        channelService.leaveChannel(manager.getId(), channel.getId());

        // Assert
        Optional<UserChannel> userChannel = userChannelJpaRepository.findByUserIdAndChannelId(manager.getId(), channel.getId());
        assertTrue(userChannel.isEmpty());

        Optional<Channel> channelOptional = channelRepository.findById(channel.getId());
        assertTrue(channelOptional.isPresent());
        assertEquals(user.getId(), channelOptional.get().getManager().getId());
    }

    @Test
    @DisplayName("채널 삭제 실패 - 매니저가 아닌 경우")
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

        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        Long userId = user.getId();
        Long channelId = channel.getId();

        // Act && Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            channelService.deleteChannel(userId, channelId);
        });
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_Success() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);
        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        // Act
        channelService.deleteChannel(manager.getId(), channel.getId());

        // Assert
        Optional<Channel> channelOptional = channelRepository.findById(channel.getId());
        assertTrue(channelOptional.isEmpty());

        Optional<UserChannel> userChannelOptional = userChannelJpaRepository.findByUserIdAndChannelId(manager.getId(), channel.getId());
        assertTrue(userChannelOptional.isEmpty());
    }

    @Test
    @DisplayName("채널 검색 성공")
    void searchChannel_Success() {
        // Arrange
        User manager = User.builder()
                .email("manager")
                .profileImageUrl("manager")
                .provider(Provider.GOOGLE)
                .providerId("2")
                .build();
        manager = userRepository.save(manager);
        Channel channel = channelService.createChannel(manager.getId(), "New Channel", "nickname");

        // Act
        ChannelInfoDto result = channelService.searchChannel(channel.getId());

        // Assert
        assertThat(result.getChannelName()).isEqualTo("New Channel");
    }
}