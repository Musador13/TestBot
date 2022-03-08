package entity;

import parcer.CsvParcer;

import java.util.ArrayList;
import java.util.List;

public class Test {

    /**
     * Функция проверяет, что переданный текст это название города.
     * @param fromCity название города.
     * @return истину или ложь в зависимости от результата проверки.
     */
    public static boolean isCity(String fromCity) {
        for (String city : Test.cities()) {
            if (city.equalsIgnoreCase(fromCity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция выводит список городов.
     * @return {@link List terminals} список городов-терминалов.
     */
    public static List<String> cities() {
        List<String> terminals = new ArrayList<>();

        for (Terminal terminal : Terminal.values()) {
            terminals.add(terminal.getPath());
        }

        return terminals;
    }

    public static List<List<String>> listsTo(String fromCity) {

        List<List<String>> listList = new ArrayList<>();
        List<String> terminalsTo = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        terminalsTo.addAll(CsvParcer.capacityTerminal(fromCity));

        for (int i = 0; i < terminalsTo.size(); i++) {
            strings.add(terminalsTo.get(i));
            if (i % 3 == 2) {
                listList.add(strings);
                strings = new ArrayList<>();
            }
        }

        if (!strings.isEmpty()) {
            listList.add(strings);
        }

        return listList;
    }

    public static List<List<String>> listsFrom() {

        List<List<String>> listList = new ArrayList<>();
        List<String> terminals = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        for (Terminal terminal : Terminal.values()) {
            terminals.add(terminal.getPath());
        }

        for (int i = 0; i < terminals.size(); i++) {
            strings.add(terminals.get(i));
            if (i % 3 == 2) {
                listList.add(strings);
                strings = new ArrayList<>();
            }
        }

        if (!strings.isEmpty()) {
            listList.add(strings);
        }

        return listList;

    }
}
