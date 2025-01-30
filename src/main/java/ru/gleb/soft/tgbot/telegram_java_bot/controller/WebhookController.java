package ru.gleb.soft.tgbot.telegram_java_bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gleb.soft.tgbot.telegram_java_bot.bot.WebhookBot;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bot")
public class WebhookController {
    private final WebhookBot telegramBot;

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }

}
