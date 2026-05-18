package edu.cit.badinas.instock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;

import edu.cit.badinas.instock.core.config.OAuth2RequestCustomizer;
import edu.cit.badinas.instock.admin.MasterIngredientRepository;
import edu.cit.badinas.instock.favorites.FavoriteRecipeRepository;
import edu.cit.badinas.instock.pantry.PantryItemRepository;
import edu.cit.badinas.instock.users.UserRepository;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=local-test-secret-local-test-secret-local-test-secret",
    "jwt.expiration=3600000",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "ADMIN_EMAIL=admin@instock.local",
    "ADMIN_PASSWORD=admin"
})
class BackendApplicationTests {
    @Test
    void contextLoads() {
    }
}
