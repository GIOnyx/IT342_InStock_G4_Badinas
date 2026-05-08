package edu.cit.badinas.instock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import edu.cit.badinas.instock.favorites.FavoriteRecipeRepository;
import edu.cit.badinas.instock.pantry.PantryItemRepository;
import edu.cit.badinas.instock.users.UserRepository;

@SpringBootTest(properties = {
		"jwt.secret=local-test-secret-local-test-secret-local-test-secret",
		"jwt.expiration=3600000"
})
@ImportAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
class BackendApplicationTests {

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private PantryItemRepository pantryItemRepository;

	@MockitoBean
	private FavoriteRecipeRepository favoriteRecipeRepository;

	@Test
	void contextLoads() {
	}

}
