/*
package com.convrt.api.controller;

import com.convrt.api.service.PlaylistService;
import com.convrt.api.entity.Context;
import com.convrt.api.entity.Playlist;
import com.convrt.api.entity.Video;
import com.convrt.api.service.ContextService;
import com.convrt.api.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private ContextService contextService;

    @GetMapping("/{uuid}")
    @JsonView(View.PlaylistWithVideo.class)
    public Playlist getPlaylist(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid) {
        Context context = contextService.validateContext(token);
        return playlistService.readPlaylist(context.getUser(), uuid);
    }

    @GetMapping("/{uuid}/videos")
    @JsonView(View.VideoWithPlaylist.class)
    public List<Video> getPlaylistVideos(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid) {
        Context context = contextService.validateContext(token);
        return playlistService.readPlaylistVideos(context.getUser(), uuid);
    }

    @GetMapping
    @JsonView(View.Playlist.class)
    public List<Playlist> getPlaylists(@RequestHeader(value = "token") String token) {
        Context context = contextService.validateContext(token);
        return context.getUser().getPlaylists();
    }

    @PutMapping("/{uuid}/videos")
    public void updateVideos(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid, @RequestBody @Valid List<Video> videos) {
        Context context = contextService.validateContext(token);
        playlistService.updateVideos(uuid, context.getUser(), videos).getVideos();
    }

    @PutMapping("/{uuid}")
    public Playlist updatePlaylist(@RequestHeader(value = "token") String token, @PathVariable(value = "uuid") String uuid, @RequestBody @Valid Playlist playlist) {
        Context context = contextService.validateContext(token);
        playlist.setUuid(uuid);
        return playlistService.updatePlaylist(context.getUser(), playlist);
    }

    @DeleteMapping("/{uuid}")
    public void deletePlaylist(@RequestHeader(value = "token") String token, @PathVariable("uuid") String uuid) {
        Context context = contextService.validateContext(token);
        playlistService.deletePlaylist(context.getUser(), uuid);
    }

    @DeleteMapping("/{uuid}/videos/{videoId}")
    public void deleteVideoInPlaylist(@RequestHeader(value = "token") String token, @PathVariable("uuid") String uuid, @PathVariable("videoId") String videoId) {
        log.info("In delete");
        Context context = contextService.validateContext(token);
        playlistService.deleteVideo(context.getUser(), uuid, videoId);
    }

}
*/
