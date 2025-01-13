package ru.vozov.taskmanagamentsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenAPIConfig {
    @Value("${api.server.url}")
    String url;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server()
                .url(url)
                .description("Server URL в окружении разработки");

        Contact contact = new Contact()
                .email("dmitrii.vozov0105@gmail.com")
                .name("Дмитрий Возов");

        Info info = new Info()
                .title("Task Management System API")
                .version("1.0")
                .description("API для управления задачами")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
