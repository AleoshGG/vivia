package aleosh.online.vivia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@EnableAsync
// Escaneo de Beans (Controllers, Services, Configs)
@ComponentScan(basePackages = {
    "aleosh.online.vivia.core",
    "aleosh.online.vivia.features.auth",
    "aleosh.online.vivia.features.users"
})
// Escaneo de Entidades JPA (Cualquier subpaquete 'entities' dentro de auth y users)
@EntityScan(basePackages = {
    "aleosh.online.vivia.features.auth",
    "aleosh.online.vivia.features.users"
})
// Escaneo de Repositorios (Cualquier subpaquete 'repositories' dentro de auth y users)
@EnableJpaRepositories(basePackages = {
    "aleosh.online.vivia.features.auth",
    "aleosh.online.vivia.features.users"
})
public class ViviaApplication {


    public static void main(String[] args) {
        SpringApplication.run(ViviaApplication.class, args);
    }

}
