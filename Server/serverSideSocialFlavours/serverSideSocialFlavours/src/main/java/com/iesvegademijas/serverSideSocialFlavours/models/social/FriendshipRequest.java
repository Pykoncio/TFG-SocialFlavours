package com.iesvegademijas.serverSideSocialFlavours.models.social;

import jakarta.persistence.*;

@Entity
public class FriendshipRequest {

    public enum status {
        PENDING,
        APPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Friendship;
    @ManyToOne
    @JoinColumn(name = "id_userSender")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "id_userReceiver")
    private User receiver;
    private String status;

    public FriendshipRequest(){}

    public FriendshipRequest(Long idFriendship, User userSender, User userReceiver, String status) {
        this.id_Friendship = idFriendship;
        this.sender = userSender;
        this.receiver = userReceiver;
        this.status = status;
    }

    public Long getId_Friendship() {
        return id_Friendship;
    }

    public void setId_Friendship(Long id_Friendship) {
        this.id_Friendship = id_Friendship;
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
