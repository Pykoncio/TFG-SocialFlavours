package com.iesvegademijas.serverSideSocialFlavours.controllers;

import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.FriendshipRequestRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendship-requests")
public class FriendshipRequestController {

    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendshipRequestController(FriendshipRequestRepository friendshipRequestRepository, UserRepository userRepository) {
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/pending")
    public List<FriendshipRequest> getPendingFriendRequestsFromUser(@RequestParam Long idUser) {
        return friendshipRequestRepository.findAllByReceiverId_userReceiverAndStatus(idUser, FriendshipRequest.Status.PENDING.name());
    }

    @GetMapping(path = "/approved")
    public List<FriendshipRequest> getApprovedFriendRequestsFromUser(@RequestParam Long idUser) {
        return friendshipRequestRepository.findAllByReceiverId_userReceiverAndStatus(idUser, FriendshipRequest.Status.APPROVED.name());
    }


    @PostMapping(path = "/newRequest{sender}To{receiver}")
    public ResponseEntity<Long> createFriendshipRequest(@PathVariable("sender") Long idSender, @PathVariable("receiver") Long idReceiver) {

        Optional<User> sender = userRepository.findById(idSender);
        Optional<User> receiver = userRepository.findById(idReceiver);
        Optional<FriendshipRequest> existingRequest = friendshipRequestRepository.findExistingRequest(sender.orElse(null), receiver.orElse(null));

        if (sender.isPresent() && receiver.isPresent() && existingRequest.isEmpty()) {
            FriendshipRequest newFriendshipRequest = new FriendshipRequest(sender.get(), receiver.get());
            FriendshipRequest friendshipRequestCreated = friendshipRequestRepository.save(newFriendshipRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(friendshipRequestCreated.getId_friendship());
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Long> acceptFriendRequest(@PathVariable("id") Long requestId) {

        Optional<FriendshipRequest> friendshipRequest = friendshipRequestRepository.findById(requestId);

        if (friendshipRequest.isPresent()) {
            FriendshipRequest updateFriendship = friendshipRequest.get();
            updateFriendship.setStatus(FriendshipRequest.Status.APPROVED.name());
            FriendshipRequest updatedFriendship = friendshipRequestRepository.save(updateFriendship);
            return ResponseEntity.ok(updatedFriendship.getId_friendship());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }


    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable("id") Long requestId) {
        Optional<FriendshipRequest> friendshipRequest = friendshipRequestRepository.findById(requestId);

        if (friendshipRequest.isPresent()) {
            friendshipRequestRepository.delete(friendshipRequest.get());
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
