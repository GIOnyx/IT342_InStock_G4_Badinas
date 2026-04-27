package edu.cit.badinas.instock.repository;

import edu.cit.badinas.instock.entity.FavoriteRecipe;
import edu.cit.badinas.instock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {
    List<FavoriteRecipe> findByUserOrderBySavedAtDesc(User user);
    Optional<FavoriteRecipe> findByIdAndUser(Long id, User user);
    boolean existsByUserAndExternalRecipeId(User user, Long externalRecipeId);
}
