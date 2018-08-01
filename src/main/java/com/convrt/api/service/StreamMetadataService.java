package com.convrt.api.service;

import com.convrt.api.entity.Video;
import com.convrt.api.entity.User;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class StreamMetadataService {

    @Autowired
    private VideoService videoService;
    @Autowired
    private PlayCountService playCountService;
    @Autowired
    private UserService userService;

    static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=%s";

    @Transactional
    public Video mapStreamData(Video video, String userUuid) {
        String videoId = video.getId();
        log.info("Attempting to fetch existing valid stream url for video={}", videoId);
        Video videoPersistent = videoService.readVideoMetadata(videoId);
        if (videoPersistent == null) {
            videoPersistent = video;
            log.info("No existing stream url available for video={}", videoId);
            String streamUrl = getStreamUrl(videoId);
            if (StringUtils.isNotBlank(streamUrl)){
                MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(streamUrl).build().getQueryParams();
                List<String> param1 = parameters.get("expire");
                videoPersistent.setStreamUrlExpireDate(Instant.ofEpochSecond(Long.valueOf(param1.get(0))));
                videoPersistent.setStreamUrl(streamUrl);
                videoPersistent.setStreamUrlDate(Instant.now());
            }
            videoService.createVideo(videoPersistent);
        }
//        if (userUuid != null) {
//            User user = userService.readUser(userUuid);
//            user.iteratePlayCount(video);
//            userService.updateUser(user);
//        }
        return videoPersistent;
    }

    private String getStreamUrl(String videoId) {
        try {
            final AtomicBoolean stop = new AtomicBoolean(false);
            URL web = new URL(String.format(YOUTUBE_URL, videoId));
            VGetParser user = VGet.parser(web);
            VideoInfo videoinfo = user.info(web);
            new VGet(videoinfo).extract(user, stop, () -> {});
            List<VideoFileInfo> list = videoinfo.getInfo();
            return findAudioStreamUrl(list);
        } catch (NullPointerException e) {
            throw new RuntimeException("Sorry bruh, looks like we couldn't find this video!", e);
        } catch (Exception e) {
            throw new RuntimeException("Oops, looks like something went wrong :(", e);
        }
    }

    private String findAudioStreamUrl(List<VideoFileInfo> list) {
        VideoFileInfo videoFileInfo = null;
        if (list != null) {
            for (VideoFileInfo d : list) {
                log.info("Found content-type: " + d.getContentType());
                if (d.getContentType().contains("audio")) {
                    log.info("Dedicated audio url found");
                    return d.getSource().toString();
                }
                videoFileInfo = d;
            }
            log.info("No dedicated audio url found. Returning full video url.");
            return videoFileInfo.getSource().toString();
        }
        throw new RuntimeException("Could not extract media stream url.");
    }

}
