package edu.cit.badinas.instock.admin;

import edu.cit.badinas.instock.core.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WebMvcTest(AdminIngredientController.class)
@Import(SecurityConfig.class)
class AdminIngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
