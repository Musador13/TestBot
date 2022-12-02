package bot;

import entity.Terminal;
import entity.Test;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parcer.CsvParcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Главный класс приложения бота, использует {@link TelegramLongPollingBot} обновления.
 */
public class Bot extends TelegramLongPollingBot {

    //Хешмапа хешмап для отслеживания состояния бота для каждого пользователя.
    HashMap<Long, HashMap<String, Object>> hashMapHashMap = new HashMap<>();

    /**
     * Главная функция-обработчик обновлений. В зависимости от пришедшего объекта {@link Update} передает управление
     * на актуальный обработчик.
     * @param update объект обновления, содержит всю инфмормацию о полученном сообщении.
     */
    @SneakyThrows
    public void onUpdateReceived(Update update) {

        User sender = update.getMessage().getFrom();

        String text = update.getMessage().getText();

        if (text.equals("/start")) {

            hashMapHashMap.put(sender.getId(), new HashMap<>());
            handleStart(update);

        } else if (text.equals("/help")) {

            handleHelp(update);

        } else if (text.equals("/info")) {

            handleInfo(update);

        } else if (text.equals("/set_way") || Test.isCity(text)) {

            handleSetWay(update);
        }
        else if (!text.equals("/stop") && text.matches("\\d+\\.?\\d*")) {
            handleDigits(update);
        }
        else {
            System.out.println("is another text " + update.getMessage().getText());
        }
    }


    /**
     * Обработчик сообщений, содержащих числовые значения, для дальнейшего вычисления стоимости.
     * После расчета цены собирает ссылку на сайт компании и передает полученные данные.
     * @param update объект обновления.
     */
    private void handleDigits(Update update) throws TelegramApiException {

        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String fromCity = (String) hashMapHashMap.get(userId).get("from");
        String toCity = (String) hashMapHashMap.get(userId).get("to");
        double weight;
        double size;

        if (hashMapHashMap.get(userId).get("choice").equals("Город назначения выбран")) {

            weight = Double.parseDouble(text);
            hashMapHashMap.get(userId).put("choice", "Вес введен");

            hashMapHashMap.get(userId).put("weight", weight);
            System.out.println(hashMapHashMap);

            execute(SendMessage.builder()
                            .chatId(chatId)
                            .text("Введите объем")
                            .build());

        } else if (hashMapHashMap.get(userId).get("choice").equals("Вес введен")) {

            size = Double.parseDouble(text);

            hashMapHashMap.get(userId).put("choice", "Объем введен");
            hashMapHashMap.get(userId).put("size", size);
            System.out.println(hashMapHashMap);

            weight = (double) hashMapHashMap.get(userId).get("weight");

            int fromCityId = Terminal.valueOf(((String) hashMapHashMap.get(userId).get("from"))
                                                      .replaceAll("[ |-]", "_")).getId();

            int toCityId = Terminal.valueOf(((String) hashMapHashMap.get(userId).get("to"))
                                                    .replaceAll("[ |-]", "_")).getId();

            String web = "https://megatrans-tk.ru/calc?from_city_id=" +
                       fromCityId +
                       "&to_city_id=" +
                       toCityId +
                       "&weight=" +
                         hashMapHashMap.get(userId).get("weight") +
                         "&size=" +
                         size;

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> Buttons = new ArrayList<>();
            InlineKeyboardButton webSite = new InlineKeyboardButton("Оформить заказ по заданным параметрам");
            webSite.setUrl(web);
            Buttons.add(webSite);
            keyboard.add(Buttons);
            inlineKeyboardMarkup.setKeyboard(keyboard);

            execute(SendMessage.builder()
                            .chatId(chatId)
                            .replyMarkup(inlineKeyboardMarkup)
                            .text(CsvParcer.calculatePrice(fromCity, toCity, weight, size))
                            .build());
        }

    }

    /**
     * Обработчик команды "/set_way". Отправляет списки городов, мин. стоимость и передает управление дальше.
     * @param update объект обновления.
     */
    private void handleSetWay(Update update) throws TelegramApiException {

        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        if (!hashMapHashMap.containsKey(userId)) {
            hashMapHashMap.put(userId, new HashMap<>());
        }

        if (text.equals("/set_way")) {

            hashMapHashMap.get(userId).put("choice", "set_way");
            sendCustomKeyboardFromCity(chatId);
            System.out.println(hashMapHashMap);
        }

        else if (hashMapHashMap.get(userId).get("choice").equals("set_way")) {
            hashMapHashMap.get(userId).put("choice", "Город отправления выбран");

            System.out.println(hashMapHashMap);

            hashMapHashMap.get(userId).put("from", text);

            System.out.println(hashMapHashMap);

            sendCustomKeyboardToCity(text, chatId);

            System.out.println(hashMapHashMap);

        } else {
            hashMapHashMap.get(userId).put("choice", "Город назначения выбран");
            hashMapHashMap.get(userId).put("to", text);


            String fromCity = (String) hashMapHashMap.get(userId).get("from");
            String toCity = (String) hashMapHashMap.get(userId).get("to");

            execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(CsvParcer.minPrice(fromCity, toCity))
                            .build());

            execute(SendMessage.builder()
                            .chatId(chatId)
                            .text("Введите вес в кг")
                            .build());

            System.out.println(hashMapHashMap);
        }

    }


    /**
     * Обработчик команды "/info", которая выводит информацию о компании, а также отправляет кнопки-ссылки.
     * @param update объект обновления.
     */
    private void handleInfo(Update update) throws TelegramApiException {
        if (!hashMapHashMap.containsKey(update.getMessage().getFrom().getId())) {
            hashMapHashMap.put(update.getMessage().getFrom().getId(), new HashMap<>());
        }
        hashMapHashMap.get(update.getMessage().getFrom().getId()).put("choice", "info");


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> Buttons = new ArrayList<>();

        InlineKeyboardButton webSite = new InlineKeyboardButton("Наш сайт");
        webSite.setUrl("https://megatrans-tk.ru/");
        Buttons.add(webSite);

        InlineKeyboardButton instagram = new InlineKeyboardButton("Наш инстаграм");
        instagram.setUrl("https://www.instagram.com/mega__trans/");
        Buttons.add(instagram);

        keyboard.add(Buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        execute(SendMessage.builder()
                        .chatId(update.getMessage().getChatId().toString())
                        .replyMarkup(inlineKeyboardMarkup)
                        .text("Транспортная компания Мега Транс.\n\n" +
                              "\uD83C\uDDF7\uD83C\uDDFA Мы осуществляем грузоперевозки по всей " +
                              "Росии \uD83C\uDDF7\uD83C\uDDFA\n\n" +
                              "Рассчитать стоимость /set_way\n\n" +
                              "Контакты для связи \uD83D\uDCE9\n\n" +
                              "Наш сайт \uD83C\uDF0E \n" + "https://megatrans-tk.ru/\n\n" +
                              "Наш инстаграм \uD83D\uDCF8\n" +
                              "https://www.instagram.com/mega__trans/\n\n" +
                              "Наш телефон ☎\n" + "\uD83D\uDCDE  88007007455\n")
                        .build());

        System.out.println(hashMapHashMap);

    }

    /**
     * Обработчик команды "/help", которая выводит краткую информацию о возможностях бота.
     * @param update объект обновления.
     */
    private void handleHelp(Update update) throws TelegramApiException {
        if (!hashMapHashMap.containsKey(update.getMessage().getFrom().getId())) {
            hashMapHashMap.put(update.getMessage().getFrom().getId(), new HashMap<>());
        }

        hashMapHashMap.get(update.getMessage().getFrom().getId()).put("choice", "help");

        execute(SendMessage.builder()
                        .chatId(update.getMessage().getChatId().toString())
                        .text(
                                "Данный бот может подсказать вам минимальную стоимость отправления в " +
                                "зависимости от выбора города отправления и города " + "назначения.\n\n" +
                                "Также вы сможете узнать начальную стоимость отправления в " +
                                "зависимости от веса и габаритов груза.\n\n" +
                                "Чтобы посмотреть доступные команды нажмите на кнопку вызова меню, " +
                                "находящуюся рядом с полем ввода сообщения.\n\n" +
                                "В данный момент бот находится в активной разработке, по всем " + "вопросам и " +
                                "пожеланиям можете смело обращаться к разработчику\n" +
                                "https://t.me/Musador")
                        .build());

        System.out.println(hashMapHashMap);
    }


    /**
     * Обработчик команды "/start", которая запускает работу бота.
     * @param update объект обновления.
     */
    private void handleStart(Update update) throws TelegramApiException {
        hashMapHashMap.get(update.getMessage().getFrom().getId()).put("choice", "start");

        execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(
                        """
                                ✋ Здравствуйте, вы начали работу с ботом ✋

                                Нажмите /set_way, чтобы посчитать стоимость отправления \uD83D\uDCB0

                                Нажмите /info, чтобы узнать как с нами связаться ☎

                                Нажмите /help, чтобы узнать возможности бота \uD83D\uDEE0

                                """)
                .build());

        System.out.println(hashMapHashMap);
    }

    /**
     * Функция отправляет клавиатуру с городами, в которые можно отправить груз, в зависимости от города отправления.
     * @param fromCity город отправления.
     * @param chatId чат, в который будет отправлена клавиатура.
     */
    private void sendCustomKeyboardToCity(String fromCity, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите город назначения");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        System.out.println(Test.isCity(fromCity));
        System.out.println(Test.listsTo(fromCity));

        for (int i = 0; i < Test.listsTo(fromCity).size(); i++) {
            for (String s : Test.listsTo(fromCity).get(i)) {
                row.add(s);
            }
            keyboard.add(row);
            row = new KeyboardRow();
        }


        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция получает список городов из {@link Test} в виде листа листов.
     * Создает кастомную клавиатуру с кнопками-терминалами.
     * @param chatId идентификатор чата, в который нужно отправить клавиатуру.
     */
    private void sendCustomKeyboardFromCity(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите город отправления");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < Test.listsFrom().size(); i++) {
            for (String s : Test.listsFrom().get(i)) {
                row.add(s);
            }
            keyboard.add(row);
            row = new KeyboardRow();
        }


        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Здесь мы добавляем имя нашего бота.
     *
     * @return имя бота, переданное в application.properties
     */
    @Override
    public String getBotUsername() {
        return "@MegaTransTariffBot";
    }

    /**
     * Здесь мы добавляем токен бота.
     *
     * @return токен, переданный в application.properties
     */
    @Override
    public String getBotToken() {
        return "5080581722:AAEqda6rUfIeyJaYA-NwhRVQyr-BUsQA1es";
    }

}