package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Buttons {
    private static final InlineKeyboardButton MAKEORDER_BUTTON = new InlineKeyboardButton("Добавить в заказ");

    public static InlineKeyboardMarkup inlineMarkup() {
        MAKEORDER_BUTTON.setCallbackData("/makeOrder");

        List<InlineKeyboardButton> rowInline = List.of(MAKEORDER_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}