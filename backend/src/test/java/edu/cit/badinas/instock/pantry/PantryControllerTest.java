package edu.cit.badinas.instock.pantry;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.badinas.instock.core.config.SecurityConfig;
import edu.cit.badinas.instock.users.Role;
import edu.cit.badinas.instock.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.OAuth2LoginSuccessHandler;
import edu.cit.badinas.instock.users.UserRepository;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PantryController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class PantryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PantryItemRepository pantryItemRepository;

    @MockitoBean
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void createPantryItemReturnsCreated() throws Exception {
        User user = testUser(Role.USER);
        PantryItemRequest request = new PantryItemRequest();
        request.setName("tomato");

        PantryItem saved = new PantryItem();
        saved.setId(1L);
        saved.setName("tomato");
        saved.setCreatedAt(LocalDateTime.of(2026, 5, 1, 10, 0));
        saved.setUser(user);

        when(pantryItemRepository.existsByUserAndNameIgnoreCase(any(User.class), eq("tomato")))
                .thenReturn(false);
        when(pantryItemRepository.save(any(PantryItem.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/stock")
                .with(authenticatedUser(user, Role.USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("tomato"));
    }

    @Test
    void getPantryReturnsList() throws Exception {
        User user = testUser(Role.USER);

        PantryItem first = new PantryItem();
        first.setId(1L);
        first.setName("garlic");
        first.setCreatedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        first.setUser(user);

        PantryItem second = new PantryItem();
        second.setId(2L);
        second.setName("egg");
        second.setCreatedAt(LocalDateTime.of(2026, 5, 1, 8, 0));
        second.setUser(user);

        when(pantryItemRepository.findByUserOrderByCreatedAtDesc(any(User.class)))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/stock")
                .with(authenticatedUser(user, Role.USER)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].name").value("garlic"));
    }

    @Test
    void updatePantryItemReturnsUpdatedItem() throws Exception {
        User user = testUser(Role.USER);
        PantryItemRequest request = new PantryItemRequest();
        request.setName("onion");

        PantryItem existing = new PantryItem();
        existing.setId(1L);
        existing.setName("garlic");
        existing.setCreatedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        existing.setUser(user);

        PantryItem updated = new PantryItem();
        updated.setId(1L);
        updated.setName("onion");
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUser(user);

        when(pantryItemRepository.findByIdAndUser(eq(1L), any(User.class)))
                .thenReturn(Optional.of(existing));
        when(pantryItemRepository.existsByUserAndNameIgnoreCase(any(User.class), eq("onion")))
                .thenReturn(false);
        when(pantryItemRepository.save(any(PantryItem.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/stock/1")
                .with(authenticatedUser(user, Role.USER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("onion"));
    }

    @Test
    void deletePantryItemReturnsOk() throws Exception {
        User user = testUser(Role.USER);

        PantryItem existing = new PantryItem();
        existing.setId(1L);
        existing.setName("garlic");
        existing.setCreatedAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        existing.setUser(user);

        when(pantryItemRepository.findByIdAndUser(eq(1L), any(User.class)))
                .thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/v1/stock/1")
                .with(authenticatedUser(user, Role.USER)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        verify(pantryItemRepository).delete(existing);
    }

    @Test
    void deleteAllPantryItemsReturnsOk() throws Exception {
        User user = testUser(Role.ADMIN);

        when(pantryItemRepository.deleteByUser(any(User.class))).thenReturn(2L);

        mockMvc.perform(delete("/api/v1/stock")
                .with(authenticatedUser(user, Role.ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Removed 2 pantry item(s)"));

        verify(pantryItemRepository).deleteByUser(any(User.class));
    }

    private static RequestPostProcessor authenticatedUser(User user, Role role) {
        user.setRole(role);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        Authentication auth = new UsernamePasswordAuthenticationToken(user, "n/a", authorities);
        return authentication(auth);
    }

    private static User testUser(Role role) {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFullName("Test User");
        user.setRole(role);
        return user;
    }
}
