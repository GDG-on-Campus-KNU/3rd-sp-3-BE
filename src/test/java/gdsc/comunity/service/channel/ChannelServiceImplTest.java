package gdsc.comunity.service.channel;

import gdsc.comunity.entity.channel.Channel;
import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.repository.channel.ChannelRepository;
import gdsc.comunity.repository.user.UserChannelRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ChannelServiceImplTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserChannelRepository userChannelRepository;

    @InjectMocks
    private ChannelServiceImpl channelServiceImpl;

    public User user;
    public Long randomNum;
    public String randomStr;

    @BeforeEach
    void setVar() {
        user = User.builder()
                .providerId("1")
                .email("email")
                .profileImageUrl("image url")
                .provider(Provider.GOOGLE)
                .build();

        randomNum = (long) (Math.random() * 100);
        randomStr = randomNum.toString();
    }

    @Test
    void createChannel() {
        //Arrange
        //already set in @BefroeEach

        //Act
        Channel newChannel = channelServiceImpl.createChannel(user, "Channel" + randomStr);

        //Assert
        assertThat(newChannel).isNotNull();
        assertThat(newChannel.getChannelName()).isEqualTo("Channel" + randomStr);
    }
}