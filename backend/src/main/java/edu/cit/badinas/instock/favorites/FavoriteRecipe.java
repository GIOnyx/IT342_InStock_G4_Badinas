package edu.cit.badinas.instock.favorites;

import edu.cit.badinas.instock.users.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_recipes")
@Data
@NoArgsConstructor
public class FavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "external_recipe_id", nullable = false)
    private Long externalRecipeId;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @PrePersist
    void onCreate() {
        savedAt = LocalDateTime.now();
    }
}