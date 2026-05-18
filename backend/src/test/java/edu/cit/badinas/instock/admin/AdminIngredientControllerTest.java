package edu.cit.badinas.instock.admin;

import edu.cit.badinas.instock.core.config.SecurityConfig;
import edu.cit.badinas.instock.core.config.OAuth2RequestCustomizer;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.OAuth2LoginSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import edu.cit.badinas.instock.users.UserRepository;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@WebMvcTest(AdminIngredientController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminIngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OAuth2RequestCustomizer oauth2RequestCustomizer;

    @MockitoBean
    private MasterIngredientRepository repository;

    @Test
    void listShouldReturnForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/ingredients").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createShouldAllowAdmin() throws Exception {
        MasterIngredient ing = new MasterIngredient();
        ing.setId(1L);
        ing.setName("Tomato");

        when(repository.findByNameIgnoreCase("Tomato")).thenReturn(java.util.Optional.empty());
        when(repository.save(ing)).thenReturn(ing);

        String payload = "{\"name\":\"Tomato\",\"category\":\"Vegetable\"}";

        mockMvc.perform(post("/api/v1/admin/ingredients")
                .with(user("admin").roles("ADMIN"))
                .contentType(APPLICATION_JSON_VALUE)
                .content(payload))
            .andExpect(status().isOk());
    }
}
