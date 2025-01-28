package ru.gleb.soft.tgbot.telegram_java_bot.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ru.gleb.soft.tgbot.telegram_java_bot.bot.WebhookBot;

@Configuration
@Data
public class BotConfig {
    @Getter
    @Value("${bot.name}")
    private String botUsername;

    @Getter
    @Value("${bot.token}")
    private String botToken;

    @Value("${telegram.webHookPath}")
    private String webHookPath;

    @Bean
    public DefaultBotOptions getDefaultBotOptions(){
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost("proxy.krista.ru");
        botOptions.setProxyPort(8080);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        return botOptions;
    }
    @Bean
    public WebhookBot getWebHookBot() {
        DefaultBotOptions options = getDefaultBotOptions();

        WebhookBot webhookBot = new WebhookBot(options);
        webhookBot.setBotUsername(botUsername);
        webhookBot.setBotToken(botToken);
        webhookBot.setBotPath(webHookPath);

        return webhookBot;
    }
}