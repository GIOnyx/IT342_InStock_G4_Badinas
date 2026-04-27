package edu.cit.badinas.instock.repository;

import edu.cit.badinas.instock.entity.PantryItem;
import edu.cit.badinas.instock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {
    List<PantryItem> findByUserOrderByCreatedAtDesc(User user);
    Optional<PantryItem> findByIdAndUser(Long id, User user);
    boolean existsByUserAndNameIgnoreCase(User user, String name);
    long deleteByUser(User user);
}
