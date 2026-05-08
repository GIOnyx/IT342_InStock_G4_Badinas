package edu.cit.badinas.instock.favorites;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.OAuth2LoginSuccessHandler;
import edu.cit.badinas.instock.core.config.SecurityConfig;
import edu.cit.badinas.instock.users.Role;
import edu.cit.badinas.instock.users.User;
import edu.cit.badinas.instock.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Test
    void addFavoriteReturnsCreated() throws Exception {
        User user = testUser();
        FavoriteRecipeRequest request = new FavoriteRecipeRequest();
        request.setExternalRecipeId(456L);
        request.setTitle("Pesto Pasta");
        request.setImageUrl("https://img.example/pesto.jpg");
        request.setSummary("Simple pasta recipe");
        request.setLikes(42);

        FavoriteRecipe saved = new FavoriteRecipe();
        saved.setId(1L);
        saved.setExternalRecipeId(456L);
        saved.setTitle("Pesto Pasta");
        saved.setImageUrl("https://img.example/pesto.jpg");
        saved.setSummary("Simple pasta recipe");
        saved.setLikes(42);
        saved.setSavedAt(LocalDateTime.of(2026, 5, 1, 12, 0));
        saved.setUser(user);

        when(favoriteRecipeRepository.existsByUserAndExternalRecipeId(any(User.class), eq(456L)))
                .thenReturn(false);
        when(favoriteRecipeRepository.save(any(FavoriteRecipe.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/favorites")
                .with(authenticatedUser(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Pesto Pasta"));
    }

    @Test
    void listFavoritesReturnsData() throws Exception {
        User user = testUser();

        FavoriteRecipe first = new FavoriteRecipe();
        first.setId(1L);
        first.setExternalRecipeId(101L);
        first.setTitle("Pancakes");
        first.setSavedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        first.setUser(user);

        FavoriteRecipe second = new FavoriteRecipe();
        second.setId(2L);
        second.setExternalRecipeId(102L);
        second.setTitle("Omelette");
        second.setSavedAt(LocalDateTime.of(2026, 5, 1, 8, 0));
        second.setUser(user);

        when(favoriteRecipeRepository.findByUserOrderBySavedAtDesc(any(User.class)))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/favorites")
                .with(authenticatedUser(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].title").value("Pancakes"));
    }

    @Test
    void deleteFavoriteReturnsOk() throws Exception {
        User user = testUser();

        FavoriteRecipe existing = new FavoriteRecipe();
        existing.setId(1L);
        existing.setExternalRecipeId(101L);
        existing.setTitle("Pancakes");
        existing.setSavedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        existing.setUser(user);

        when(favoriteRecipeRepository.findByIdAndUser(eq(1L), any(User.class)))
                .thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/v1/favorites/1")
                .with(authenticatedUser(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        verify(favoriteRecipeRepository).delete(existing);
    }

    private static RequestPostProcessor authenticatedUser(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.name()));
        Authentication auth = new UsernamePasswordAuthenticationToken(user, "n/a", authorities);
        return authentication(auth);
    }

    private static User testUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFullName("Test User");
        user.setRole(Role.USER);
        return user;
    }
}
