package com.iesvegademijas.serverSideSocialFlavours.models.social;

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
    private User sender;
    @ManyToOne
    @JoinColumn(name = "id_userReceiver")
    private User receiver;
    private String status;

    public FriendshipRequest(){
        this.status = Status.PENDING.toString();
    }

    public FriendshipRequest(User userSender, User userReceiver) {
        this.sender = userSender;
        this.receiver = userReceiver;
        this.status = Status.PENDING.toString();
    }

    public Long getId_friendship() {
        return id_friendship;
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
