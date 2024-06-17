package com.iesvegademijas.serverSideSocialFlavours.controllers.social;

import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.FriendshipRequestRepository;
import com.iesvegademijas.serverSideSocialFlavours.repository.social.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendshipapi")
public class FriendshipRequestController {

    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendshipRequestController(FriendshipRequestRepository friendshipRequestRepository, UserRepository userRepository) {
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/pending{id}")
    public ResponseEntity<List<FriendshipRequest>> getPendingFriendRequestsFromUser(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<FriendshipRequest> friendshipRequests = friendshipRequestRepository.findAllFriendshipRequestByReceiverAndStatus(optionalUser.get(), FriendshipRequest.Status.PENDING.name());
        return ResponseEntity.ok(friendshipRequests);


    }

    @GetMapping(path = "/approved{id}")
    public ResponseEntity<List<FriendshipRequest>> getApprovedFriendRequestsFromUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<FriendshipRequest> friendshipRequests = friendshipRequestRepository.findAllFriendshipRequestByReceiverAndStatus(optionalUser.get(), FriendshipRequest.Status.APPROVED.name());

        if (friendshipRequests.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(friendshipRequests);
        }
    }

    @GetMapping(path = "/incomingFriendshipRequests/{idUser}")
    public ResponseEntity<List<FriendshipRequest>> getIncomingFriendRequestsFromUser(@PathVariable Long idUser) {
        Optional<User> optionalUser = userRepository.findById(idUser);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else
        {
            List<FriendshipRequest> friendshipRequests;

            friendshipRequests = friendshipRequestRepository.findAllFriendshipRequestByReceiverAndStatus(optionalUser.get(), FriendshipRequest.Status.PENDING.name());

            return ResponseEntity.ok(friendshipRequests);
        }
    }


    @PostMapping(path = "/newRequest{sender}To{receiver}")
    public ResponseEntity<Long> createFriendshipRequest(@PathVariable("sender") Long idSender, @PathVariable("receiver") String usernameReceiver) {

        Optional<User> sender = userRepository.findById(idSender);
        Optional<User> receiver = Optional.ofNullable(userRepository.findByUsername(usernameReceiver));
        Optional<FriendshipRequest> existingRequest = friendshipRequestRepository.findExistingRequest(sender.orElse(null), receiver.orElse(null));

        if (sender.isPresent() && receiver.isPresent() && existingRequest.isEmpty()) {
            FriendshipRequest newFriendshipRequest = new FriendshipRequest(sender.get(), receiver.get(), sender.get().getUsername());
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
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Long id) {
        Optional<FriendshipRequest> friendshipRequest = friendshipRequestRepository.findById(id);

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
