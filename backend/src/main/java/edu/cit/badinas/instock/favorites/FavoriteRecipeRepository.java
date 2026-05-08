package edu.cit.badinas.instock.favorites;

import edu.cit.badinas.instock.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {
    List<FavoriteRecipe> findByUserOrderBySavedAtDesc(User user);
    Optional<FavoriteRecipe> findByIdAndUser(Long id, User user);
    boolean existsByUserAndExternalRecipeId(User user, Long externalRecipeId);
}