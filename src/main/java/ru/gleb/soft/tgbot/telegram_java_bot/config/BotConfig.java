package ru.gleb.soft.tgbot.telegram_java_bot.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
@Data
public class BotConfig {
    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public DefaultBotOptions getDefaultBotOptions(){
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost("proxy.krista.ru");
        botOptions.setProxyPort(8080);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        return botOptions;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }
}