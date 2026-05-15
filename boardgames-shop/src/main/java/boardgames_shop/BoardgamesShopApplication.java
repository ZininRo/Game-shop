package boardgames_shop;

import boardgames_shop.config.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
public class BoardgamesShopApplication {

	public static void main(String[] args) {


		runDatabaseInitializer();


		SpringApplication.run(BoardgamesShopApplication.class, args);
	}

	private static void runDatabaseInitializer() {

		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {

			// Подключаем application.properties
			org.springframework.core.env.ConfigurableEnvironment env =
					new org.springframework.core.env.StandardEnvironment();
			org.springframework.core.io.support.ResourcePropertySource props =
					new org.springframework.core.io.support.ResourcePropertySource(
							"classpath:application.properties");
			env.getPropertySources().addFirst(props);
			ctx.setEnvironment(env);


			ctx.register(DatabaseInitializer.class);
			ctx.refresh();


			DatabaseInitializer initializer = ctx.getBean(DatabaseInitializer.class);
			initializer.initialize();
		} catch (Exception e) {
			System.err.println("КРИТИЧЕСКАЯ ОШИБКА при инициализации БД: " + e.getMessage());
			System.err.println("Проверьте параметр init.datasource.password в application.properties");
			throw new RuntimeException(e);
		}
	}
}