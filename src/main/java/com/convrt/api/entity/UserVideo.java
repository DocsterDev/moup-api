/*package com.convrt.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
//@NoArgsConstructor
@Entity
@Table(name = "user_video", indexes = {@Index(name = "user_video_user_uuid_idx0", columnList = "user_uuid"), @Index(name = "user_video_video_id_idx0", columnList = "video_id"), @Index(name = "user_video_videos_order_idx0", columnList = "videos_order")}) //
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserVideo extends BaseEntity implements Serializable {

    public UserVideo() {
        this.uuid = UUID.randomUUID().toString();
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", foreignKey = @ForeignKey(name = "fk_user_video_user_uuid"))
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id", foreignKey = @ForeignKey(name = "fk_user_video_video_id"))
    private Video video;

    @Column(name = "videos_order")
    private int videosOrder;

//    @Column(name = "viewed_date", updatable = false)
//    public Instant viewedDate = Instant.now();
//
//    @PrePersist
//    protected void prePersist() {
//        this.viewedDate = Instant.now();
//    }
}

*/