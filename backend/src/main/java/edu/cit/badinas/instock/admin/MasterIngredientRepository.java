package edu.cit.badinas.instock.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MasterIngredientRepository extends JpaRepository<MasterIngredient, Long> {
    Optional<MasterIngredient> findByNameIgnoreCase(String name);
}
