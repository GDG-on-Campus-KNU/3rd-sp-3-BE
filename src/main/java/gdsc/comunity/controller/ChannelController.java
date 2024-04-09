package gdsc.comunity.controller;

import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.service.channel.ChannelServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelServiceImpl channelServiceImpl;

    @GetMapping("/hello")
    public String test() {
        return "test";
    }

    @PostMapping
    ResponseEntity<String> createChannel(@RequestBody String channelName){
        // TODO : 사용자 로그인 정보를 바탕으로 User Entity를 보유하고 있음을 가정함.
        User user = User.builder()
                .email("test")
                .profileImageUrl("test")
                .provider(Provider.GOOGLE)
                .providerId("1")
                .build();

        channelServiceImpl.createChannel(user, channelName);
        return new ResponseEntity<>("Channel created.", HttpStatus.CREATED);
    }
}
