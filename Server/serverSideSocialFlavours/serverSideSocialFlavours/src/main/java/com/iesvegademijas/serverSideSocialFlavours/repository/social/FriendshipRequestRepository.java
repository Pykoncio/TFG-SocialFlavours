package com.iesvegademijas.serverSideSocialFlavours.repository.social;

import com.iesvegademijas.serverSideSocialFlavours.models.social.FriendshipRequest;
import com.iesvegademijas.serverSideSocialFlavours.models.social.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRequestRepository extends CrudRepository<FriendshipRequest, Long> {

    @Query("SELECT fr FROM FriendshipRequest fr " +
            "WHERE (fr.sender = :sender AND fr.receiver = :receiver) " +
            "OR (fr.sender = :receiver AND fr.receiver = :sender) " +
            "AND fr.status IN ('PENDING', 'APPROVED')")
    Optional<FriendshipRequest> findExistingRequest(@Param("sender") User sender, @Param("receiver") User receiver);

    @Query("SELECT fr FROM FriendshipRequest fr " +
            "WHERE fr.receiver = :receiver " +
            "AND fr.status = :status")
    List<FriendshipRequest> findAllFriendshipRequestByReceiverAndStatus(Long receiver, String status);

    @Query("SELECT fr FROM FriendshipRequest fr " +
            "WHERE (fr.sender = :user OR fr.receiver = :user)" +
            "AND fr.status = :status")
    List<FriendshipRequest> findAllApprovedRequestsFromUser(Long user, String status);
}
