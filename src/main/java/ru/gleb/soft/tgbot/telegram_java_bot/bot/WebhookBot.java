package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gleb.soft.tgbot.telegram_java_bot.enums.modes;

import java.io.*;
import java.util.*;

@Slf4j
@Getter
@Setter
public class WebhookBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;
    DefaultBotOptions defaultBotOptions;
    private List<String> phrases;
    private int phrases_count;
    private Random rand;
    private Queue<Integer> recently_phrases;
    private modes mode;

    public WebhookBot(DefaultBotOptions options) {
        super(options);
        phrases = getPhrasesList();
        phrases_count = phrases.size();
        rand = new Random();
        mode = modes.dialog;
        recently_phrases = new LinkedList<>();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Запрос получен");
        if (checkCommands(update)) return null;

        switch (mode) {
            case dialog -> getBotAnswer(update);
            case adding -> setBotPhrase(update);
        }
        return null;
    }

    private void getBotAnswer(Update update) {
        if (update.getMessage().getReplyToMessage().getFrom().getId() == 8013072863L) {
            var chatId = update.getMessage().getChatId();
            var replyMess = update.getMessage();
            var phraseID = getPhraseID();
            String messageText = phrases.get(phraseID);
            sendMessage(chatId, messageText, replyMess.getMessageId());
        }
    }

    private int getPhraseID() {
        var phraseID = rand.nextInt(phrases_count);
        if (recently_phrases.isEmpty()) {
            recently_phrases.add(phraseID);
            return phraseID;
        }
        while (recently_phrases.contains(phraseID)) {
            phraseID = rand.nextInt(phrases_count);
        }
        recently_phrases.add(phraseID);
        if (recently_phrases.size() > 30) {
            recently_phrases.poll();
        }
        return phraseID;
    }

    private void setBotPhrase(Update update) {
        var newPhrase = update.getMessage();
        try {
            addPhrase(newPhrase.getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long chatId, String textToSend, int replyId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (replyId > 0) {
            sendMessage.setReplyToMessageId(replyId);
        }
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private boolean checkCommands(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var command = update.getMessage().getText();
            switch (command) {
                case "/start":
                    sendMessage(update.getMessage().getChatId(), phrases.get(rand.nextInt(phrases_count)), 0);
                    return true;
                case "/add":
                    mode = modes.adding;
                    sendMessage(update.getMessage().getChatId(), "Пришлите фразы для добавление", 0);
                    return true;
                case "/stopadd":
                    mode = modes.dialog;
                    phrases.clear();
                    phrases = getPhrasesList();
                    sendMessage(update.getMessage().getChatId(), "Фразы добавлены", 0);
                    return true;
                case "/phrases":
                    sendPhrasesList(update);
                    return true;
                case "/phrasesCount":
                    sendMessage(update.getMessage().getChatId(), "Фраз в списке: " + phrases.size(), 0);
                    return true;
                case "/docker":
                    sendMessage(update.getMessage().getChatId(), "привет поцы", 0);
                    return true;
            }
        }
        return false;
    }

    private ArrayList<String> getPhrasesList() {
        try {
            var reader = new BufferedReader(new FileReader("phrases.txt"));
            String line = reader.readLine();
            var result = new ArrayList<String>();
            result.add(line);
            while (line != null) {
                line = reader.readLine();
                result.add(line);
            }

            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPhrase(String phrase) throws IOException {
        FileWriter writer = new FileWriter("phrases.txt", true);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        bufferWriter.write(phrase + "\r\n");
        bufferWriter.close();
    }

    private void sendPhrasesList(Update update) {
        var chatId = update.getMessage().getChatId();
        for (var phrase : phrases) {
            sendMessage(chatId, phrase, 0);
        }
        sendMessage(chatId, "Список закончен", 0);
    }

    @Override
    public String getBotUsername() {
        return "Paha_Svin";
    }
}
