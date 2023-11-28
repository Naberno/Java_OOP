package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


/**
 * Интерфейс для Телеграмм-бота.
 */
interface TelegramBotInterface {


    /**
     * Создание клавиатуры в боте.
     *
     * @return Объект ReplyKeyboardMarkup с настроенной клавиатурой.
     */
    ReplyKeyboardMarkup createKeyboard();
}


/**
 * Класс для реализации Телеграмм-бота
 */
public class TelegramBot extends TelegramLongPollingBot implements TelegramBotInterface {

    /**
     * Токен для Telegram-бота.
     * Получено из переменной среды "tgbotToken".
     */
    final private String BOT_TOKEN = System.getenv("tgbotToken");

    /**
     * Имя Telegram-бота.
     * Эта переменная используется для хранения имени бота.
     */
    final private String BOT_NAME = "Nabo";

    /**
     * Экземпляр класса MessageHandling.
     * Эта переменная используется для хранения экземпляра обработки сообщения.
     */
    private MessageHandling messageHandling;
    /**
     * Конструктор класса TelegramBot, который инициализирует объекты Storage и MessageHandling.
     * Storage используется для управления базой данных с прочитанными книгами,
     * а MessageHandling - для обработки входящих сообщений от пользователя.
     */
    public TelegramBot() {
        messageHandling = new MessageHandling();
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    /**
     * Получение и Отправка сообщения в чат пользователю
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                // Извлекаем из объекта сообщение пользователя
                Message message = update.getMessage();
                String userMessage = message.getText();
                // Достаем из inMess id чата пользователя
                long chatId = message.getChatId();

                // Выводим сообщение пользователя в консоль
                System.out.println("User Message: " + userMessage);

                // Получаем текст сообщения пользователя, отправляем в написанный нами обработчик
                String response = messageHandling.parseMessage(userMessage, chatId);

                // Выводим ответ бота в консоль
                System.out.println("Bot Response: " + response);

                // Создаем объект класса SendMessage - наш будущий ответ пользователю
                SendMessage outMess = new SendMessage();
                // Добавляем в наше сообщение id чата, а также наш ответ
                outMess.setChatId(String.valueOf(chatId));
                outMess.setText(response);
                outMess.setReplyMarkup(createKeyboard());
                // Отправка в чат
                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    /**
     * Метод для создания клавиатуры в боте
     */
    public ReplyKeyboardMarkup createKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Создание ряда клавиш
        KeyboardRow row1 = new KeyboardRow();
        //row1.add("1");
        //row1.add("2");
        KeyboardRow row2 = new KeyboardRow();
        //row2.add("3");
        //row2.add("4");
        KeyboardRow row3 = new KeyboardRow();
        //row3.add("5");
        //row3.add("!?");
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        // Установка клавиатуры
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
