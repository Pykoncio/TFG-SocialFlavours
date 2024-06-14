package com.iesvegademijas.serverSideSocialFlavours.models.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class FriendshipRequest {

    public enum Status {
        PENDING,
        APPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_friendship;
    @ManyToOne
    @JoinColumn(name = "id_userSender")
    @JsonIgnore
    private User sender;

    private String usernameSender;

    @ManyToOne
    @JoinColumn(name = "id_userReceiver")
    @JsonIgnore
    private User receiver;
    private String status;

    public FriendshipRequest(){
        this.status = Status.PENDING.toString();
    }

    public FriendshipRequest(User userSender, User userReceiver, String usernameSender) {
        this.sender = userSender;
        this.receiver = userReceiver;
        this.usernameSender = usernameSender;
        this.status = Status.PENDING.toString();
    }

    public Long getId_friendship() {
        return id_friendship;
    }

    public String getUsernameSender() {
        return usernameSender;
    }

    public void setUsernameSender(String usernameSender) {
        this.usernameSender = usernameSender;
    }

    public void setId_friendship(Long id_Friendship) {
        this.id_friendship = id_Friendship;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
