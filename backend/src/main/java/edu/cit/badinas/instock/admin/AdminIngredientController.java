package edu.cit.badinas.instock.admin;

import edu.cit.badinas.instock.core.dto.ApiResponse;
import edu.cit.badinas.instock.admin.dto.CreateIngredientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/ingredients")
@RequiredArgsConstructor
public class AdminIngredientController {

    private final MasterIngredientRepository repository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MasterIngredient>>> listAll() {
        List<MasterIngredient> all = repository.findAll();
        return ResponseEntity.ok(ApiResponse.success("OK", all));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MasterIngredient>> create(@Valid @RequestBody CreateIngredientRequest req) {
        repository.findByNameIgnoreCase(req.getName()).ifPresent(existing -> {
            throw new RuntimeException("Ingredient already exists");
        });

        MasterIngredient ing = new MasterIngredient();
        ing.setName(req.getName().trim());
        ing.setCategory(req.getCategory());
        MasterIngredient saved = repository.save(ing);

        return ResponseEntity.ok(ApiResponse.success("Ingredient created", saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Ingredient deleted", null));
    }
}
