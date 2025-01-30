package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
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
    private String fileName;
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
        fileName = "phrases.txt";
        recently_phrases = new LinkedList<>();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Запрос получен " + getMode());
        log.info(update.toString());
        try {
            if (checkCommands(update)) return null;
            log.info("Команды проверены " + getMode());
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
        log.info("SetBotPhrase " + getMode());
        var newPhrase = update.getMessage();
        try {
            addPhrase(newPhrase.getText());
            log.info("SetBotPhrase OK " + getMode());
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
                    sendPhrasesList(506238949L);
                    sendMessage(update.getMessage().getChatId(), "Фразы добавлены", 0);
                    return true;
                case "/phrases":
                    sendPhrasesList(update.getMessage().getChatId());
                    return true;
                case "/count":
                    sendMessage(update.getMessage().getChatId(), "Фраз в списке: " + phrases.size(), 0);
                    return true;
                case "/docker":
                    sendMessage(update.getMessage().getChatId(), "привет поцы", 0);
                    return true;
            }
        }
        if (update.hasMessage() && update.getMessage().hasDocument() && update.getMessage().getFrom().getId() != 8013072863L) {
            log.info("Получил документ");
            setPhrasesFromFile(update);
            return true;
        }
        return false;
    }

    private ArrayList<String> getPhrasesList() {
        try {
            var reader = new BufferedReader(new FileReader(fileName));
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
        FileWriter writer = new FileWriter(fileName, true);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        log.info("пишу в файл: " + phrase);
        bufferWriter.write(phrase + "\r\n");
        bufferWriter.close();
        log.info("закрыл файл");
    }

    private void sendPhrasesList(long chatId) throws TelegramApiException {
        File file = new File(fileName);
        InputFile inputFile = new InputFile(file, fileName);

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setDocument(inputFile);
        execute(sendDocumentRequest);
    }

    private void setPhrasesFromFile(Update update) {
        String doc_id = update.getMessage().getDocument().getFileId();
        String doc_name = update.getMessage().getDocument().getFileName();
        String doc_mine = update.getMessage().getDocument().getMimeType();
        int doc_size = Math.toIntExact(update.getMessage().getDocument().getFileSize());
        String getID = String.valueOf(update.getMessage().getFrom().getId());

        Document document = new Document();
        document.setMimeType(doc_mine);
        document.setFileName(doc_name);
        document.setFileSize((long) doc_size);
        document.setFileId(doc_id);

        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File("newfrases.txt"));
            fileName = "newfrases.txt";
            phrases.clear();
            phrases = getPhrasesList();
            phrases_count = phrases.size();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Paha_Svin";
    }
}
