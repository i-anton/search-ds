package ru.mail.polis;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ru.mail.polis.Student.Gender;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */
public class SimpleStudentGenerator {

    private static SimpleStudentGenerator instance = new SimpleStudentGenerator();

    private final static String URL_PREFIX = "https://polis.mail.ru/photo/id=";
    private final static LocalDate MIN_YEAR_OF_BIRTHDAY = LocalDate.of(1990, 1, 1);
    private final static int MAX_YEARS_AFTER_BIRTHDAY = 10;
    private final static LocalDate MIN_YEAR_OF_ADMISSION = LocalDate.of(2010, 1, 1);
    private final static int MAX_YEARS_AFTER_ADMISSION = 7;
    private final static int MIN_GROUP_ID = 1000;
    private final static int MIN_LETTER_CODE = 'a';
    private final static int MAX_LETTER_CODE = 'z';
    private final static String EMAIL_SUFFIX = "@polis.mail.ru";
    private final static int MIN_PHONE_NUMBER = 1000000000;
    private final static Random random = new Random();

    private static final String[] maleFirstNames = {
            "Адонис", "Баграт", "Вальтер", "Гелеон", "Дамир", "Жерар", "Измаил", "Камиль", "Лазарь",
            "Марк", "Назар", "Оганес", "Пабло", "Радим", "Севастьян", "Тенгиз", "Фарид", "Христоф",
            "Чарлз", "Шамиль", "Эвальд", "Ювеналий", "Януарий"
    };
    private static final String[] femaleFirstNames = {
            "Августа", "Бажена", "Валентина", "Габриэлла", "Далида", "Ева", "Жаклин", "Забава", "Ильзира",
            "Камила", "Лада", "Мавиле", "Надежда", "Одетта", "Павлина", "Рада", "Сабина", "Таисия", "Ульяна",
            "Фёкла", "Хадия", "Цагана", "Челси", "Шакира", "Эвелина", "Юлианна", "Ядвига"
    };

    private static final String[] lastNames = {
            "АБАИМОВ", "БАБАДЖАНОВ", "ВАВИЛИН", "ГАВЕНДЯЕВ", "ДАЙНЕКО", "ЕВГЕЕВ", "ЖАБЕНКОВ", "ЗАБАВА",
            "ИБРАГИМОВ", "КАБАКОВ", "ЛАБЗИН", "МАВРИН", "НАБАТОВ", "ОБАБКОВ", "ПАВЕЛЕВ", "РАБИН",
            "САБАНЕЕВ", "ТАБАКОВ", "УБАЙДУЛЛАЕВ", "ФАБИШ", "ХАБАЛОВ", "ЦАГАРАЕВ", "ЧААДАЕВ",
            "ШАБАЛДИН", "ЩАВЕЛЕВ", "ЭВАРНИЦКИЙ", "ЮБЕРОВ", "ЯВЛАШКИН"
    };

    private SimpleStudentGenerator() {
        /* empty */
    }

    public static SimpleStudentGenerator getInstance() {
        return instance;
    }

    public Student generate() {
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        String[] firstNames = gender == Gender.MALE ? maleFirstNames : femaleFirstNames;
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        LocalDate birthday = MIN_YEAR_OF_BIRTHDAY
                .plusYears(random.nextInt(MAX_YEARS_AFTER_BIRTHDAY + 1))
                .plusMonths(ChronoField.MONTH_OF_YEAR.range().getMinimum() + random.nextInt((int) ChronoField.MONTH_OF_YEAR.range().getMaximum()))
                .plusDays(ChronoField.DAY_OF_MONTH.range().getMinimum() + random.nextInt((int) ChronoField.DAY_OF_MONTH.range().getSmallestMaximum()));
        int groupId = MIN_GROUP_ID + random.nextInt(100);
        int yearOfAdmission = MIN_YEAR_OF_ADMISSION.plusYears(random.nextInt(MAX_YEARS_AFTER_ADMISSION + 1)).getYear();

        Student student = new Student(firstName, lastName, gender, birthday, groupId, yearOfAdmission);
        if (random.nextBoolean()) {
            student.setPhotoReference(URL_PREFIX + 1000000 + random.nextInt(1000000));
        }
        if (random.nextBoolean()) {
            StringBuilder username = new StringBuilder();
            random.ints(10, MIN_LETTER_CODE, MAX_LETTER_CODE + 1).forEach(((value -> username.append((char) value))));
            student.setEmail(username + EMAIL_SUFFIX);
        }
        if (random.nextBoolean()) {
            student.setMobile("" + (MIN_PHONE_NUMBER + random.nextInt(MIN_PHONE_NUMBER)));
        }
        return student;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(instance.generate());
            System.out.println();
        }
    }
}
