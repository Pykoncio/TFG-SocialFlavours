package com.iesvegademijas.serverSideSocialFlavours.repository.recipeRelated;

import com.iesvegademijas.serverSideSocialFlavours.models.recipeRelated.ShoppingList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {
    @Query("SELECT sl FROM ShoppingList sl WHERE sl.user.id_user = :userId")
    List<ShoppingList> findShoppingListByUserId(@Param("userId") Long userId);
}
