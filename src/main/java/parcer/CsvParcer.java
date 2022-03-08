package parcer;

import au.com.bytecode.opencsv.CSVReader;
import lombok.Getter;
import lombok.Setter;
import parcer.tariffCalc.Terminals;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CsvParcer {

    public static void printerMinPrice(String fromCity, String toCity) {

        System.out.println(minPrice(fromCity, toCity) + "");
    }

    private static void printerPrice(String fromCity, String toCity, double weight, double size) {

        System.out.println(minPrice(fromCity, toCity) + "");
        System.out.println(calculatePrice(fromCity, toCity, weight, size));
    }

    public static String minPrice(String fromCity, String toCity) {
        String minPrice = "Мин. стоимость из города " + fromCity + " в город " + toCity + ": " +
                          printMinAmount(fromCity, toCity) + " руб.";
        return minPrice;
    }

    public static String calculatePrice(String fromCity, String toCity, double weight, double size) {
        String price =
                "\uD83D\uDE9B\uD83D\uDCB0 Межтерминальная перевозка \uD83D\uDE9B\uD83D\uDCB0\n" +
                "Стоимость отправления " + "из города " + fromCity + " в город " + toCity + " груза " +
                "весом " + weight +
                " кг и объемом " + size + " кубометра " + "начинается от " +
                calcAmount(fromCity, toCity, weight, size) + " руб.\n\n" +
                "Данная стоимость является ориентировочной. Окончательная стоимость перевозки будет известна после " +
                "поступления груза на склад отправления\n\n" +
                "Узнать стоимость для других городов /set_way\n\n" +
                "Получить подробную информацию, связавшись с нашими специалистами " +
                "/help\n\n" +
                "Чтобы оформить перевозку нажмите соответствующую кнопку снизу.\n" +
                "Не переживайте, введенные данные уже будут на сайте, вам лишь нужно уточнить детали";
        return price;
    }


    /**
     * Функция получает название города, открывает csv таблицу для данного города и возвращает лист объектов
     * {@link Terminals}.
     *
     * @param fromCity город отправления.
     * @return лист терминалов {@link Terminals}.
     */
    private static ArrayList<Terminals> loadFromTable(String fromCity) {
        ArrayList<Terminals> terminals = new ArrayList<>();
        String city = fromCity.trim();

        try {
            CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/tables/" + city + ".csv"));
            List<String[]> allRows = csvReader.readAll();
            for (String[] nextLine : allRows) {

                if (nextLine[0].trim().equalsIgnoreCase("город")) {
                    continue;
                }

                terminals.add(new Terminals(nextLine[0], Integer.parseInt(nextLine[1]), Integer.parseInt(nextLine[2]),
                                            Double.parseDouble(nextLine[3]), Double.parseDouble(nextLine[4]),
                                            Double.parseDouble(nextLine[5]), Double.parseDouble(nextLine[6]),
                                            Double.parseDouble(nextLine[7]), Double.parseDouble(nextLine[8]),
                                            Integer.parseInt(nextLine[9]), Integer.parseInt(nextLine[10]),
                                            Integer.parseInt(nextLine[11]), Integer.parseInt(nextLine[12]),
                                            Integer.parseInt(nextLine[13]), Integer.parseInt(nextLine[14])));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return terminals;
    }

    public static List<String> capacityTerminal(String fromCity) {
        ArrayList<Terminals> lists = loadFromTable(fromCity);
        List<String> toCities = new ArrayList<>();

        for (Terminals terminals : lists) {
            toCities.add(terminals.getTo());
        }

        return toCities;
    }

    private static Terminals currentTerminal(String fromCity, String toCity) {
        ArrayList<Terminals> lists = loadFromTable(fromCity);
        String city = toCity.trim();
        Terminals currentT = new Terminals();

        for (Terminals terminals : lists) {
            String str = (terminals.getTo());
            if (str.equalsIgnoreCase(city)) {
                currentT = terminals;
            }
        }
        return currentT;
    }

    private static double calcWeightTariff(double weight, String fromCity, String toCity) {

        Terminals terminals = currentTerminal(fromCity, toCity);

        double weightS = weight / 250;

        double weightTariff;
        if (weightS <= 1) {
            weightTariff = terminals.getWeightIndex1();
        }
        else if (weightS > 1 && weightS <= 3) {
            weightTariff = terminals.getWeightIndex3();
        }
        else if (weightS > 3 && weightS <= 5) {
            weightTariff = terminals.getWeightIndex5();
        }
        else if (weightS > 5 && weightS <= 10) {
            weightTariff = terminals.getWeightIndex10();
        }
        else if (weightS > 10 && weightS <= 20) {
            weightTariff = terminals.getWeightIndex20();
        }
        else {
            weightTariff = terminals.getWeightIndex40();
        }
        return weightTariff;
    }

    public static double Size3d(double length, double width, double height) {
        double size = length * width * height;

        return size;
    }

    private static int calcSizeTariff(double size, String fromCity, String toCity) {

        Terminals terminals = currentTerminal(fromCity, toCity);

        int sizeTariff;
        if (size <= 1) {
            sizeTariff = terminals.getSizeIndex1();
        }
        else if (size > 1 && size <= 3) {
            sizeTariff = terminals.getSizeIndex3();
        }
        else if (size > 3 && size <= 5) {
            sizeTariff = terminals.getSizeIndex5();
        }
        else if (size > 5 && size <= 10) {
            sizeTariff = terminals.getSizeIndex10();
        }
        else if (size > 10 && size <= 20) {
            sizeTariff = terminals.getSizeIndex20();
        }
        else {
            sizeTariff = terminals.getSizeIndex40();
        }
        return sizeTariff;
    }

    public static long printMinAmount(String fromCity, String toCity) {

        ArrayList<Terminals> lists = loadFromTable(fromCity);


        long price = 0;
        for (Terminals terminals : lists) {
            String str = (terminals.getTo());
            if (str.equalsIgnoreCase(toCity)) {
                price += terminals.getMinAmount();
            }
        }
        return price;
    }

    public static double calcAmount(String fromCity, String toCity, double weight, double size) {
        return Math.max(Math.max(calcSizeAmount(size, fromCity, toCity), calcWeightAmount(weight, fromCity, toCity)),
                        printMinAmount(fromCity, toCity));

    }

    private static double calcSizeAmount(double size, String fromCity, String toCity) {
        return (size * calcSizeTariff(size, fromCity, toCity));
    }

    private static double calcWeightAmount(double weight, String fromCity, String toCity) {
        return (weight * calcWeightTariff(weight, fromCity, toCity));
    }

}
