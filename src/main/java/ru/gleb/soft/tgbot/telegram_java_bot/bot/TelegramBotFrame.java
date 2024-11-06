package ru.gleb.soft.tgbot.telegram_java_bot.bot;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gleb.soft.tgbot.telegram_java_bot.config.BotConfig;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Service
public class TelegramBotFrame extends TelegramLongPollingBot {

    final BotConfig botConfig;

    public TelegramBotFrame(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var t = 0;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }
}
