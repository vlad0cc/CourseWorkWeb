package ru.rutmiit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.rutmiit.models.entities.Book;
import ru.rutmiit.models.entities.Library;
import ru.rutmiit.models.entities.Role;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.enums.Genre;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.repositories.BookRepository;
import ru.rutmiit.repositories.LibraryRepository;
import ru.rutmiit.repositories.UserRepository;
import ru.rutmiit.repositories.UserRoleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class Init implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public Init(UserRepository userRepository,
                UserRoleRepository userRoleRepository,
                LibraryRepository libraryRepository,
                BookRepository bookRepository,
                PasswordEncoder passwordEncoder,
                @Value("${app.default.password}") String defaultPassword) {

        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;

        log.info("Init компонент инициализирован");
    }

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации");
        initRoles();
        initUsers();
        initSampleLibrariesAndBooks();
        log.info("Инициализация завершена");
    }

    private void initRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("Создание ролей");

            userRoleRepository.saveAll(List.of(
                    new Role(UserRoles.ADMIN),
                    new Role(UserRoles.MODERATOR),
                    new Role(UserRoles.USER)
            ));

            log.info("Роли созданы: ADMIN, MODERATOR, USER");
        } else {
            log.debug("Роли уже существуют - пропуск");
        }
    }

    private void initUsers() {
        long existing = userRepository.count();
        AtomicLong nextCardNumber = new AtomicLong(
                userRepository.findMaxReaderCardNumber()
        );

        if (existing == 0) {
            log.info("Создание пользователей");

            initAdmin();
            initModerator();
            initNormalUser(nextCardNumber);

            log.info("Пользователи созданы");
        } else {
            log.debug("Пользователи уже существуют - проверяем readerCard");
        }

        userRepository.findAll().forEach(user -> {
            boolean isUser = user.getRoles().stream()
                    .anyMatch(role -> role.getName() == UserRoles.USER);

            if (isUser) {
                if (user.getReaderCardNumber() == null || user.getReaderCardNumber().isBlank()) {
                    user.setReaderCardNumber(String.valueOf(nextCardNumber.incrementAndGet()));
                }
            } else {
                user.setReaderCardNumber(null);
            }

            userRepository.save(user);
        });
    }

    private void initAdmin() {
        var adminRole = userRoleRepository.findRoleByName(UserRoles.ADMIN).orElseThrow();

        User admin = new User(
                "admin",
                passwordEncoder.encode(defaultPassword),
                "admin@example.com",
                "Администратор Системы",
                30
        );
        admin.setRoles(List.of(adminRole));

        userRepository.save(admin);
        log.info("Создан администратор: admin");
    }

    private void initModerator() {
        var moderatorRole = userRoleRepository.findRoleByName(UserRoles.MODERATOR).orElseThrow();

        User moderator = new User(
                "moderator",
                passwordEncoder.encode(defaultPassword),
                "moderator@example.com",
                "Модератор Каталога",
                24
        );
        moderator.setRoles(List.of(moderatorRole));

        userRepository.save(moderator);
        log.info("Создан модератор: moderator");
    }

    private void initNormalUser(AtomicLong nextCardNumber) {
        var userRole = userRoleRepository.findRoleByName(UserRoles.USER).orElseThrow();

        User user = new User(
                "user",
                passwordEncoder.encode(defaultPassword),
                "user@example.com",
                "Иван Петров",
                22
        );
        user.setRoles(List.of(userRole));
        user.setReaderCardNumber(String.valueOf(nextCardNumber.incrementAndGet()));

        userRepository.save(user);
        log.info("Создан обычный пользователь: user");
    }

    private void initSampleLibrariesAndBooks() {
        if (libraryRepository.count() > 0 || bookRepository.count() > 0) {
            log.debug("Библиотеки/книги уже существуют - пропуск");
            return;
        }

        log.info("Создание библиотек и книг");

        Library central = new Library();
        central.setName("Центральная библиотека");
        central.setAddress("г. Москва, ул. Пушкина, 10");
        central.setWorkingHours("Пн–Пт 09:00–20:00");
        central.setDescription("Главная городская библиотека с широким фондом художественной и научной литературы.");

        Library kids = new Library();
        kids.setName("Детская библиотека");
        kids.setAddress("г. Москва, ул. Лермонтова, 5");
        kids.setWorkingHours("Вт–Вс 10:00–19:00");
        kids.setDescription("Библиотека для детей: сказки, энциклопедии, занимательные книги.");

        libraryRepository.saveAll(List.of(central, kids));

        Book book1 = new Book();
        book1.setTitle("Хоббит, или Туда и обратно");
        book1.setAuthor("Дж. Р. Р. Толкин");
        book1.setGenre(Genre.FICTION);
        book1.setPublishedOn(LocalDate.of(1937, 1, 1));
        book1.setIsbn("978-5-389-07416-5");
        book1.setPublisher("АСТ");
        book1.setPrintRun(5000);
        book1.setAvailableCopies(3);
        book1.setLibrary(central);

        Book book2 = new Book();
        book2.setTitle("Краткая история времени");
        book2.setAuthor("Стивен Хокинг");
        book2.setGenre(Genre.SCIENCE);
        book2.setPublishedOn(LocalDate.of(1988, 1, 1));
        book2.setIsbn("978-5-91759-268-5");
        book2.setPublisher("КОЛИБРИ");
        book2.setPrintRun(7000);
        book2.setAvailableCopies(2);
        book2.setLibrary(central);

        Book book3 = new Book();
        book3.setTitle("Приключения Тома Сойера");
        book3.setAuthor("Марк Твен");
        book3.setGenre(Genre.CHILDREN);
        book3.setPublishedOn(LocalDate.of(1876, 1, 1));
        book3.setIsbn("978-5-389-20704-4");
        book3.setPublisher("АСТ");
        book3.setPrintRun(4000);
        book3.setAvailableCopies(4);
        book3.setLibrary(kids);

        bookRepository.saveAll(List.of(book1, book2, book3));

        log.info("Созданы примерные библиотеки и книги");
    }
}
