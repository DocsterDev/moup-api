package com.convrt.entity;

import com.convrt.utils.UUIDUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "user",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")},
        indexes = {@Index(name = "user_email_idx0", columnList = "email"), @Index(name = "user_pin_idx1", columnList = "pin")})
public class User extends BaseEntity {

    public User (String email, String pin) {
        this.uuid = UUID.randomUUID().toString();
        this.pin = pin;
        this.email = email;
    }

    @NonNull
    @Column(name = "email", length = 50)
    private String email;

    @NonNull
    @JsonIgnore
    @Column(name = "pin", length = 4) // TODO: encrypt this
    private String pin;

    @NonNull
    @Column(name = "first_name", length = 36)
    private String firstName;

    @OrderColumn
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Playlist> playlists;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<PlayCount> playCounts = Lists.newArrayList();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Context> contexts;

    @JsonIgnore
    public void iteratePlayCount(Video video) {
        for (PlayCount playCount: playCounts) {
            if (video.getId().equals(playCount.getVideo().getId())) {
                playCount.iterateNumPlays();
                return;
            }
        }
        PlayCount playCount = new PlayCount(video, this);
        playCounts.add(playCount);
    }

}
