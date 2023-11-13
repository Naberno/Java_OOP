package org.example;
import java.util.ArrayList;

/**
 * Интерфейс для обработки сообщений пользователя
 */
 interface MessageProcessor {

    /**
     * Обрабатывает текстовое сообщение пользователя и возвращает ответ.
     *
     * @param textMsg Текстовое сообщение пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на сообщение пользователя.
     */
    String parseMessage (String textMsg, long chatId);
}


/**
 * Класс для обработки сообщений пользователя
 */
public class MessageHandling implements MessageProcessor {

    private Storage storage;

    private PuzzleGame puzzleGame;

    private boolean puzzleMode;


    /**
     * Конструктор класса MessageHandling. Инициализирует объекты Storage и PuzzleGame,
     * а также устанавливает начальное значение режима головоломки как false.
     */
    public MessageHandling() {
        storage = new Storage();
        puzzleGame = new PuzzleGame();
        puzzleMode = false;
    }


    /**
     * Метод для обработки входящего текстового сообщения от пользователя.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в виде строки.
     */

    public String parseMessage(String textMsg, long chatId) {
        String response;

        if (puzzleMode) {
            response = handlePuzzleMode(textMsg, chatId);
        }else{
            response = handleDefaultMode(textMsg, chatId);
        }

        return response;
    }


    /**
     * Обработчик сообщений в режиме головоломки.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме головоломки.
     */
    private String handlePuzzleMode(String textMsg, long chatId) {
        String response;
        if ((textMsg.equalsIgnoreCase("дай подсказку"))||(textMsg.equals("/gethint"))) {
            response = puzzleGame.getHint();
        } else if ((textMsg.equalsIgnoreCase("следующая загадка"))||(textMsg.equals("/anotheriddle"))) {
            response = puzzleGame.getNextPuzzle(chatId);
        } else if (textMsg.equals("/restart")) {
            response = puzzleGame.restart(chatId);
        } else if ((textMsg.equalsIgnoreCase("какой ответ"))||(textMsg.equals("/getanswer"))) {
            response = puzzleGame.getAnswerAndNextPuzzle(chatId);
        } else if (textMsg.equals("/stoppuzzle")) {
            response = "Режим головоломки завершен.\n" + puzzleGame.getStatistics(chatId);

            puzzleMode = false; // Выход из режима головоломки
        }else if (textMsg.startsWith("/checkanswer")) {
            // Извлеките ответ пользователя из сообщения
            String userAnswer = textMsg.substring("/checkanswer".length()).trim();
            // Передайте ответ пользователя в PuzzleGame для проверки
            response = puzzleGame.checkAnswer(chatId, userAnswer);
        } else {
            response = puzzleGame.checkAnswer(chatId, textMsg);
        }
        return response;
    }

    /**
     * Метод для установки объекта PuzzleGame.
     *
     * @param puzzleGame Объект PuzzleGame.
     */
    public void setPuzzleGame(PuzzleGame puzzleGame) {
        this.puzzleGame = puzzleGame;
    }

    /**
     * Обработчик сообщений в режиме по умолчанию.
     *
     * @param textMsg Входящий текстовый запрос от пользователя.
     * @param chatId  Идентификатор чата пользователя.
     * @return Ответ на запрос пользователя в режиме по умолчанию.
     */
    private String handleDefaultMode(String textMsg, long chatId) {
        String response;
        // Сравниваем текст пользователя с командами, на основе этого формируем ответ
        if (textMsg.equals("/start") || textMsg.equals("/help")) {
            response = "Приветствую, это литературный бот. Жми /get, чтобы получить случайную цитату. Жми /genre, чтобы перейти в раздел жанров книг.";
        } else if (textMsg.equals("/get") || textMsg.equals("Просвети")) {
            response = storage.getRandQuote();
        } else if (textMsg.equals("/genre")) {
            response = "Здравствуйте, добро пожаловать в бот рекомендации книг! Нажмите /chat и выберите жанр";
        } else if (textMsg.equals("Научная фантастика")) {
            response = "Прочитайте 'Автостопом по галактике', 'Время жить и время умирать' или 'Война миров'";
        } else if (textMsg.equals("Фэнтези")) {
            response = "Прочитайте 'Хоббит', 'Игра престолов' или 'Гарри Поттер'";
        } else if (textMsg.equals("Романтика")) {
            response = "Прочитайте 'Великий Гетсби', 'Триумфальная арка' или 'Поющие в терновнике'";
        } else if (textMsg.equals("Детектив")) {
            response = "Прочитайте 'Убийство в восточном экспрессе', 'Снеговик' или 'Собака Баскервилей'";


        } else if (textMsg.startsWith("/addbook")) {
            // Получаем название книги, автора и год прочтения, введенные пользователем
            String[] parts = textMsg.substring(9).split("\n");
            if (parts.length == 3) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                int year;
                try {
                    year = Integer.parseInt(parts[2].trim());

                    // Проверяем существование книги в базе данных
                    if (!storage.bookExists(title, author, year, chatId)) {
                        // Если книги с такими данными нет, добавляем книгу в базу данных
                        storage.addReadBook(title, author, year, chatId);
                        response = "Книга '" + title + "' от автора " + author + " (год: " + year + ") успешно добавлена в список прочитанных!";
                    } else {
                        response = "Книга с указанным названием, автором и годом прочтения уже существует в базе данных.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /addbook Название книги\nАвтор\nГод прочтения";
            }


        } else if (textMsg.startsWith("/editbook")) {
            // Получаем уникальный номер книги и новые данные книги, введенные пользователем
            String[] parts = textMsg.substring(10).split("\n");
            if (parts.length == 4) {
                int bookNumber;
                String newTitle;
                String newAuthor;
                int newYear;
                try {
                    // Получаем уникальный номер книги
                    bookNumber = Integer.parseInt(parts[0].trim());
                    // Получаем новые данные книги
                    newTitle = parts[1].trim();
                    newAuthor = parts[2].trim();
                    newYear = Integer.parseInt(parts[3].trim());

                    // Проверяем существование книги с указанным уникальным номером в списке прочитанных книг
                    ArrayList<String> readBooks = storage.getAllValues(chatId);
                    if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                        // Получаем старые данные книги
                        String[] oldBookParts = readBooks.get(bookNumber - 1).split("\n");
                        String oldTitle = oldBookParts[0];
                        String oldAuthor = oldBookParts[1];
                        int oldYear = Integer.parseInt(oldBookParts[2]);

                        // Заменяем книгу в базе данных
                        storage.editReadBook(oldTitle, oldAuthor, oldYear, newTitle, newAuthor, newYear, chatId);
                        response = "Книга '" + oldTitle + "' успешно заменена на книгу '" + newTitle + "' от автора " + newAuthor + " (год: " + newYear + ") в списке прочитанных!";
                    } else {
                        response = "Указанный уникальный номер книги не существует в списке прочитанных книг.";
                    }
                } catch (NumberFormatException e) {
                    response = "Некорректный формат уникального номера книги или года прочтения.";
                }
            } else {
                response = "Некорректный формат ввода. Используйте /editbook Уникальный_номер\n Новое_название\nНовый_автор\nНовый_год";
            }


    } else if (textMsg.equals("/clearread")) {
            // Очищаем список прочитанных книг
            storage.clearReadBooks(chatId);
            response = "Список прочитанных книг очищен!";


        } else if (textMsg.equals("/getread")) {
            // Получаем список прочитанных книг с уникальными номерами
            ArrayList<String> readBooks = storage.getReadBooks(chatId);
            if (readBooks.isEmpty()) {
                response = "Список прочитанных книг пуст.";
            } else {
                StringBuilder responseBuilder = new StringBuilder("Прочитанные книги:\n");
                for (int i = 0; i < readBooks.size(); i++) {
                    responseBuilder.append(i + 1).append(". ").append(readBooks.get(i)).append("\n");
                }
                response = responseBuilder.toString();
            }


    } else if (textMsg.startsWith("/getbyauthor")) {
            String author = textMsg.substring(13); // Получаем имя автора из сообщения
            ArrayList<String> booksByAuthor = storage.getBooksByAuthor(author, chatId);
            if (!booksByAuthor.isEmpty()) {
                response = "Книги автора " + author + ":\n" + String.join("\n", booksByAuthor);
            } else {
                response = "Нет прочитанных книг этого автора.";
            }


        } else if (textMsg.startsWith("/getbyyear")) {
            int year = Integer.parseInt(textMsg.substring(11)); // Получаем год из сообщения
            ArrayList<String> getBooksByYear = storage.getBooksByYear(year, chatId);
            if (!getBooksByYear.isEmpty()) {
                response = "Книги" + " " + year + " " + "года" + ":\n" + String.join("\n", getBooksByYear);
            } else {
                response = "Нет прочитанных книг в этом году.";
            }


        } else if (textMsg.startsWith("/removebook")) {
                   String message = textMsg.substring(12);
                   try {
                       int bookNumber = Integer.parseInt(message);
                       ArrayList<String> readBooks = storage.getReadBooks(chatId);
                       if (bookNumber >= 1 && bookNumber <= readBooks.size()) {
                           String removedBook = readBooks.remove(bookNumber - 1); // Удаляем книгу и получаем ее данные
                           // Здесь можно использовать removedBook для получения информации об удаленной книге
                           storage.updateReadBooks(chatId, readBooks); // Обновляем список без удаленной книги
                           response = "Книга " + removedBook + " успешно удалена из списка прочитанных!";
                       } else {
                           response = "Указанный номер книги не существует.";
                       }
                   } catch(NumberFormatException e) {
                         response = "Некорректный формат номера книги";
                   }


    } else if (textMsg.equals("/playpuzzle")) {
            // Вход в режим головоломки
            puzzleMode = true;
            response = puzzleGame.startPuzzle(chatId);
        }

    else {
        response = textMsg;
    }
        return response;
    }


}


