package gdsc.comunity.controller;

import gdsc.comunity.dto.channel.ChannelInfoDto;
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

    // TODO : 사용자 id는 @UserId 어노테이션으로 받아올 예정이다.
    @PostMapping
    ResponseEntity<String> createChannel(@RequestBody String channelName, Long id){
        channelServiceImpl.createChannel(id, channelName);
        return new ResponseEntity<>("Channel created.", HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<ChannelInfoDto> searchChannel(@RequestParam Long channelId, Long id){
        ChannelInfoDto channelInfoDto = channelServiceImpl.searchChannel(channelId);
        return new ResponseEntity<>(channelInfoDto, HttpStatus.OK);
    }
}
