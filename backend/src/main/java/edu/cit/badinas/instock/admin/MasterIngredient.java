package edu.cit.badinas.instock.admin;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "master_ingredients")
@Data
public class MasterIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String category;

    private String imageUrl;

    private boolean isVerified = false;
}
