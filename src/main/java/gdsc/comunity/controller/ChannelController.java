package gdsc.comunity.controller;

import gdsc.comunity.annotation.UserId;
import gdsc.comunity.dto.channel.*;
import gdsc.comunity.service.channel.ChannelServiceImpl;
import gdsc.comunity.util.ListWrapper;
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

    @PostMapping
    ResponseEntity<String> createChannel(@RequestBody ChannelCreateDto channelCreateDto, @UserId Long id) {
        String channelName = channelCreateDto.getChannelName();
        String nickname = channelCreateDto.getNickname();
        channelServiceImpl.createChannel(id, channelName, nickname);
        return new ResponseEntity<>("Channel created.", HttpStatus.CREATED);
    }

    @GetMapping("/{channelId}")
    ResponseEntity<ChannelInfoDto> searchChannel(@PathVariable Long channelId, @UserId Long id) {
        ChannelInfoDto channelInfoDto = channelServiceImpl.searchChannel(channelId);
        return new ResponseEntity<>(channelInfoDto, HttpStatus.OK);
    }

    @GetMapping("/join/{channelId}")
    ResponseEntity<ListWrapper<List<ChannelJoinRequestDto>>> searchJoinRequest(@PathVariable Long channelId, @UserId Long id) {
        List<ChannelJoinRequestDto> userList = channelServiceImpl.searchJoinRequest(id, channelId);
        return new ResponseEntity<>(new ListWrapper<>(userList), HttpStatus.OK);
    }

    @PostMapping("/join/{channelId}")
    ResponseEntity<String> sendJoinRequest(@RequestBody String nickname, @PathVariable Long channelId, @UserId Long id) {
        channelServiceImpl.sendJoinRequest(nickname, id, channelId);
        return new ResponseEntity<>("Channel joined.", HttpStatus.OK);
    }

    @PutMapping("/join/{channelId}")
    ResponseEntity<String> leaveChannel(@PathVariable Long channelId, @UserId Long id) {
        channelServiceImpl.leaveChannel(id, channelId);
        return new ResponseEntity<>("Channel left.", HttpStatus.OK);
    }

    @DeleteMapping("/join/{channelId}")
    ResponseEntity<String> deleteChannel(@PathVariable Long channelId, @UserId Long id) {
        channelServiceImpl.deleteChannel(id, channelId);
        return new ResponseEntity<>("Channel deleted.", HttpStatus.OK);
    }

    @PutMapping("/approve")
    ResponseEntity<String> approveJoinChannel(@RequestBody ApproveJoinChannelDto approveJoinChannelDto, @UserId Long userId) {
        Long targetUserId = approveJoinChannelDto.getUserId();
        Long channelId = approveJoinChannelDto.getChannelId();
        channelServiceImpl.approveJoinChannel(userId, targetUserId, channelId);
        return new ResponseEntity<>("Channel joined.", HttpStatus.OK);
    }

    @PutMapping("/nickname")
    ResponseEntity<String> changeNickname(@RequestBody ChannelNicknameDto channelNicknameDto, @UserId Long id) {
        String nickname = channelNicknameDto.getNickname();
        Long channelId = channelNicknameDto.getChannelId();
        channelServiceImpl.changeNickname(id, channelId, nickname);
        return new ResponseEntity<>("Nickname changed.", HttpStatus.OK);
    }
}
