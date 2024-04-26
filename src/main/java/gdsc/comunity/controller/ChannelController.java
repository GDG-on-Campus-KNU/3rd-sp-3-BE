package gdsc.comunity.controller;

import gdsc.comunity.dto.channel.ApproveJoinChannelDto;
import gdsc.comunity.dto.channel.ChannelInfoDto;
import gdsc.comunity.dto.channel.ChannelNicknameDto;
import gdsc.comunity.entity.channel.ChannelJoinRequest;
import gdsc.comunity.entity.user.Provider;
import gdsc.comunity.entity.user.User;
import gdsc.comunity.service.channel.ChannelServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{channelId}")
    ResponseEntity<ChannelInfoDto> searchChannel(@PathVariable Long channelId, Long id){
        ChannelInfoDto channelInfoDto = channelServiceImpl.searchChannel(channelId);
        return new ResponseEntity<>(channelInfoDto, HttpStatus.OK);
    }

    @GetMapping("/join/{channelId}")
    ResponseEntity<List<ChannelJoinRequest>> searchJoinRequest(@PathVariable Long channelId, Long id){
        // TODO : 반환 형식이 아직 정해지지 않았다. 회의를 통해 문서화하고 코드에 반영 필요.
        List<ChannelJoinRequest> userList = channelServiceImpl.searchJoinRequest(id, channelId);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @PostMapping("/join/{channelId}")
    ResponseEntity<String> sendJoinRequest(@RequestBody String nickname, @PathVariable Long channelId, Long id){
        channelServiceImpl.sendJoinRequest(nickname, id, channelId);
        return new ResponseEntity<>("Channel joined.", HttpStatus.OK);
    }

    @PutMapping("/join/{channelId}")
    ResponseEntity<String> leaveChannel(@PathVariable Long channelId, Long id){
        channelServiceImpl.leaveChannel(id, channelId);
        return new ResponseEntity<>("Channel left.", HttpStatus.OK);
    }

    @DeleteMapping("/join/{channelId}")
    ResponseEntity<String> deleteChannel(@PathVariable Long channelId, Long id){
        channelServiceImpl.deleteChannel(id, channelId);
        return new ResponseEntity<>("Channel deleted.", HttpStatus.OK);
    }

    @PutMapping("/approve")
    ResponseEntity<String> approveJoinChannel(@RequestBody ApproveJoinChannelDto approveJoinChannelDto, Long userId){
        Long targetUserId = approveJoinChannelDto.getUserId();
        Long channelId = approveJoinChannelDto.getChannelId();
        channelServiceImpl.approveJoinChannel(userId, targetUserId, channelId);
        return new ResponseEntity<>("Channel joined.", HttpStatus.OK);
    }

    @PutMapping("/nickname")
    ResponseEntity<String> changeNickname(@RequestBody ChannelNicknameDto channelNicknameDto, Long id){
        String nickname = channelNicknameDto.getNickname();
        Long channelId = channelNicknameDto.getChannelId();
        channelServiceImpl.changeNickname(id, channelId, nickname);
        return new ResponseEntity<>("Nickname changed.", HttpStatus.OK);
    }
}
