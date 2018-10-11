package com.convrt.api.service;

import com.convrt.api.entity.*;
import com.convrt.api.repository.SubscriptionRepository;
import com.convrt.api.view.Status;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private ContextService contextService;
    @Autowired
    private ChannelService channelService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d, uuuu").withZone(ZoneOffset.UTC);

    @Transactional
    public Subscription addSubscription(Channel channel, String token) {
        if (channel == null) {
            throw new RuntimeException("Cannot add new subscription for user. Subscription body is null.");
        }
        User user = contextService.validateAndGetUser(token);
        channel = channelService.createChannel(channel);
        Subscription sub = new Subscription();
        sub.setUuid(UUID.randomUUID().toString());
        sub.setUser(user);
        sub.setChannel(channel);
        sub.setSubscribedDate(Instant.now());
        if (subscriptionRepository.existsByChannelAndUser(channel, user)) {
            throw new RuntimeException(String.format("You have already subscribed to %s", channel.getName()));
        }
        subscriptionRepository.save(sub);
        return sub;
    }

    @Transactional(readOnly = true)
    public List<Channel> readAllDistinctChannels() {
        return subscriptionRepository.findDistinctChannel();
    }

    @Transactional
    public void deleteSubscription(String token, String uuid) {
        if (uuid == null) {
            throw new RuntimeException("Cannot delete subscription for user. Subscription uuid is null.");
        }
        User user = contextService.validateAndGetUser(token);
        subscriptionRepository.deleteByUuidAndUser(uuid, user);
    }

    @Transactional(readOnly = true)
    public List<Subscription> readSubscriptions(String token) {
        User user = contextService.validateAndGetUser(token);
        return subscriptionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Video>> getSubscriptionVideos(String token, String groupBy) {
        StopWatch sw = new StopWatch();
        sw.start();
        User user = contextService.validateAndGetUser(token);
        Map<String, List<Video>> subscribedVideos;
        switch (groupBy) {
            case "date":
                subscribedVideos = groupByDate(user);
                break;
            case "channel":
                subscribedVideos = groupByChannel(user);
                break;
            default:
                throw new RuntimeException(String.format("Unknown group by type: %s", groupBy));
        }
        sw.stop();
        log.info("Total time to scan for new subscribed videos {}ms", sw.getTotalTimeMillis());
        return subscribedVideos;
    }

    private Map<String, List<Video>> groupByDate(User user) {
        Map<String, List<Video>> subscribedVideos = Maps.newLinkedHashMap();
        user.getSubscriptions().stream().forEach((subscription) -> {
            subscription.getChannel().getVideos().stream().forEach((video) -> {
                if (!isVideoWatched(video, user.getVideos())) {
                    String date = LocalDateTime.ofInstant(video.getSubscriptionScannedDate(), ZoneOffset.UTC).format(DATE_FORMATTER);
                    if (!subscribedVideos.containsKey(date)) {
                        subscribedVideos.put(date, Lists.newLinkedList());
                    }
                    List<Video> videos = subscribedVideos.get(date);
                    if (videos.size() < 3) {
                        video.setStreamUrl(null);
                        video.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
                        videos.add(video);
                    }
                }
            });
        });
        return subscribedVideos;
    }

    private Map<String, List<Video>> groupByChannel(User user) {
        Map<String, List<Video>> subscribedVideos = Maps.newLinkedHashMap();
        user.getSubscriptions().stream().forEach((subscription) -> {
            Channel channel = subscription.getChannel();
            String channelName = channel.getName();
            channel.getVideos().stream().forEach((video) -> {
                if (!isVideoWatched(video, user.getVideos())) {
                    if (!subscribedVideos.containsKey(channelName)) {
                        subscribedVideos.put(channelName, Lists.newLinkedList());
                    }
                    List<Video> videos = subscribedVideos.get(channelName);
                    if (videos.size() < 3) {
                        String date = LocalDateTime.ofInstant(video.getSubscriptionScannedDate(), ZoneOffset.UTC).format(DATE_FORMATTER);
                        video.setStreamUrl(null);
                        video.setDateScanned(date);
                        video.setThumbnailUrl(String.format("http://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
                        videos.add(video);
                    }
                }
            });
        });
        return subscribedVideos;
    }

    private boolean isVideoWatched(Video video, List<Video> watchedVideos) {
        for (Video watchedVideo : watchedVideos) {
            if (video.getId().equals(watchedVideo.getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Long pollSubscriptionVideos(String token) {
        Map<String, List<Video>> videos = getSubscriptionVideos(token, "date");
        Long count = 0L;
        for (Map.Entry<String, List<Video>> entrySet : videos.entrySet()) {
            count += entrySet.getValue().size();
        }
        return count;
    }

}
