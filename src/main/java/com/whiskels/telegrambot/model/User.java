package com.whiskels.telegrambot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "chat_id", name = "users_unique_chatid_idx")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractBaseEntity {
    @Column(name = "chat_id", unique = true, nullable = false)
    private int chatId;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank
    private String name;

    @Column(name = "manager", columnDefinition = "boolean default false")
    private boolean isManager;

    @Column(name = "admin", columnDefinition = "boolean default false")
    private boolean isAdmin;

    @Column(name = "head", columnDefinition = "boolean default false")
    private boolean isHead;

    @OneToMany(fetch = LAZY, mappedBy = "user")
    @OrderBy("hour DESC")
    private List<Schedule> scheduleList;

    public User(int chatId) {
        this.chatId = chatId;
        this.name = String.valueOf(chatId);
    }
}
