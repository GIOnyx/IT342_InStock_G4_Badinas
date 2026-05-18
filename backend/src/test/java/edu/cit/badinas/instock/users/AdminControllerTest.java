package edu.cit.badinas.instock.users;

import edu.cit.badinas.instock.core.config.SecurityConfig;
import edu.cit.badinas.instock.core.config.OAuth2RequestCustomizer;
import edu.cit.badinas.instock.favorites.FavoriteRecipeRepository;
import edu.cit.badinas.instock.pantry.PantryItemRepository;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.OAuth2LoginSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Required Security Mocks ---
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @MockitoBean
    private OAuth2RequestCustomizer oauth2RequestCustomizer;

    // --- Controller Dependencies ---
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PantryItemRepository pantryItemRepository;

    @MockitoBean
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @Test
    void adminUsersShouldReturnListForAdmin() throws Exception {
        User userOne = new User();
        userOne.setId(1L);
        userOne.setFullName("Admin One");
        userOne.setEmail("admin1@test.com");
        userOne.setRole(Role.ADMIN);
        userOne.setPasswordHash("hash1");

        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setFullName("User Two");
        userTwo.setEmail("user2@test.com");
        userTwo.setRole(Role.USER);
        userTwo.setPasswordHash("hash2");

        when(userRepository.findAll()).thenReturn(List.of(userOne, userTwo));

        mockMvc.perform(get("/api/v1/admin/users")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].fullName").value("Admin One"))
            .andExpect(jsonPath("$.data[0].email").value("admin1@test.com"))
            .andExpect(jsonPath("$.data[0].role").value("ADMIN"))
            .andExpect(jsonPath("$.data[0].passwordHash").doesNotExist())
            .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }

    @Test
    void adminUsersShouldReturnForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                .with(user("user").roles("USER")))
            .andExpect(status().isForbidden());
    }
}
