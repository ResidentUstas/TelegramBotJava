package ru.gleb.soft.tgbot.telegram_java_bot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories")
@EntityScan(basePackages = "ru.gleb.soft.tgbot.telegram_java_bot.domain.entities")
@EnableJpaAuditing
public class TelegramJavaBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TelegramJavaBotApplication.class, args);
	}
}
