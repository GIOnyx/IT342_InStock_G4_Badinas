package edu.cit.badinas.instock.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.badinas.instock.auth.security.JwtService;
import edu.cit.badinas.instock.auth.security.OAuth2LoginSuccessHandler;
import edu.cit.badinas.instock.core.config.SecurityConfig;
import edu.cit.badinas.instock.recipes.dto.RecipeDTO;
import edu.cit.badinas.instock.recipes.dto.RecipeDetailDTO;
import edu.cit.badinas.instock.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RecipeApiFacade recipeApiFacade;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    @Test
    @WithMockUser
    void searchByIngredientsReturnsRecipes() throws Exception {
        RecipeDTO first = new RecipeDTO();
        first.setRecipeId(101L);
        first.setTitle("Chicken Rice Bowl");
        first.setImageUrl("https://img.example/chicken.jpg");

        RecipeDTO second = new RecipeDTO();
        second.setRecipeId(102L);
        second.setTitle("Tomato Pasta");
        second.setImageUrl("https://img.example/pasta.jpg");

        when(recipeApiFacade.searchByIngredients(eq("chicken,rice"), eq(2)))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/recipes/search")
                .param("ingredients", "chicken,rice")
                .param("number", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].recipeId").value(101))
            .andExpect(jsonPath("$.data[0].title").value("Chicken Rice Bowl"));
    }

    @Test
    @WithMockUser
    void searchByNameReturnsRecipes() throws Exception {
        RecipeDTO recipe = new RecipeDTO();
        recipe.setRecipeId(201L);
        recipe.setTitle("Spicy Noodles");
        recipe.setImageUrl("https://img.example/noodles.jpg");

        when(recipeApiFacade.searchByRecipeTitle(eq("noodles"), eq(1)))
                .thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/v1/recipes/search-by-name")
                .param("query", "noodles")
                .param("number", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].title").value("Spicy Noodles"));
    }

    @Test
    @WithMockUser
    void getRecipeDetailsReturnsData() throws Exception {
        RecipeDetailDTO detail = new RecipeDetailDTO();
        detail.setRecipeId(301L);
        detail.setTitle("Herb Omelette");
        detail.setImageUrl("https://img.example/omelette.jpg");
        detail.setSummary("A quick omelette.");
        detail.setReadyInMinutes(10);
        detail.setServings(2);
        detail.setSourceUrl("https://example.com/omelette");
        detail.setIngredients(List.of("egg", "herbs"));
        detail.setInstructions(List.of("Beat eggs", "Cook"));

        when(recipeApiFacade.getRecipeDetails(eq(301L))).thenReturn(detail);

        mockMvc.perform(get("/api/v1/recipes/301"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.recipeId").value(301))
            .andExpect(jsonPath("$.data.title").value("Herb Omelette"))
            .andExpect(jsonPath("$.data.ingredients", hasSize(2)));
    }
}
