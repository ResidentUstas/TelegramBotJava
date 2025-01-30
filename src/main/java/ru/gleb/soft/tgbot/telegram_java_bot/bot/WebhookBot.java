package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
        log.info(update.toString());
        try {
            if (checkCommands(update)) return null;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        switch (mode) {
            case dialog -> getBotAnswer(update);
            case adding -> setBotPhrase(update);
        }
        return null;
    }

    private void getBotAnswer(Update update) {
        if (update.getMessage().getReplyToMessage() != null) {
            if (update.getMessage().getReplyToMessage().getFrom().getId() == 8013072863L) {
                var chatId = update.getMessage().getChatId();
                var phraseID = getPhraseID();
                var replyMess = update.getMessage();
                String messageText = phrases.get(phraseID);
                sendMessage(chatId, messageText, replyMess.getMessageId());
            }
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
        if (recently_phrases.size() > 40) {
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

    private boolean checkCommands(Update update) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var command = update.getMessage().getText().replaceAll("@.*", "");
            switch (command) {
                case "/start":
                    sendMessage(update.getMessage().getChatId(), phrases.get(rand.nextInt(phrases_count)), 0);
                    return true;
                case "/add":
                    mode = modes.adding;
                    sendMessage(update.getMessage().getChatId(), "Пришлите фразы для добавление", 0);
                    return true;
                case "/stop":
                    mode = modes.dialog;
                    phrases.clear();
                    phrases = getPhrasesList();
                    phrases_count = phrases.size();
                    sendMessage(update.getMessage().getChatId(), "Фразы добавлены", 0);
                    return true;
                case "/phrases":
                    sendPhrasesList(update);
                    return true;
                case "/count":
                    sendMessage(update.getMessage().getChatId(), "Фраз в списке: " + phrases.size(), 0);
                    return true;
                case "/docker":
                    sendMessage(update.getMessage().getChatId(), "привет поцы", 0);
                    return true;
            }
            return false;
        }
        return true;
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
        log.info("открываю в файл");
        FileWriter writer = new FileWriter("phrases.txt", true);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        log.info("пишу в файл: " + phrase);
        bufferWriter.write(phrase + "\r\n");
        bufferWriter.close();
        log.info("закрыл файл");
    }

    private void sendPhrasesList(Update update) throws TelegramApiException {
        var chatId = update.getMessage().getChatId();
        File file = new File("phrases.txt");
        InputFile inputFile = new InputFile(file, "phrases.txt");

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setDocument(inputFile);
        execute(sendDocumentRequest);
    }

    @Override
    public String getBotUsername() {
        return "Paha_Svin";
    }
}
