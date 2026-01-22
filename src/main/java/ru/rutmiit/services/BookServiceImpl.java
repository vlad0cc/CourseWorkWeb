package ru.rutmiit.services;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddBookDto;
import ru.rutmiit.dto.ShowBookInfoDto;
import ru.rutmiit.dto.ShowDetailedBookInfoDto;
import ru.rutmiit.models.entities.Book;
import ru.rutmiit.models.exceptions.BookNotFoundException;
import ru.rutmiit.repositories.BookRepository;
import ru.rutmiit.repositories.BookLoanRepository;
import ru.rutmiit.repositories.LibraryRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;
    private final BookLoanRepository bookLoanRepository;
    private final ModelMapper mapper;

    public BookServiceImpl(BookRepository bookRepository, LibraryRepository libraryRepository, BookLoanRepository bookLoanRepository, ModelMapper mapper) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
        this.bookLoanRepository = bookLoanRepository;
        this.mapper = mapper;
        log.info("BookService готов");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "books", allEntries = true)
    public void addBook(AddBookDto bookDto) {
        log.debug("Создание книги: {} / {}", bookDto.getTitle(), bookDto.getAuthor());

        Book book = mapper.map(bookDto, Book.class);
        var library = libraryRepository.findByName(bookDto.getLibraryName())
                .orElseThrow(() -> new IllegalArgumentException("Библиотека не найдена"));
        book.setLibrary(library);
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(0);
        }
        if (book.getPrintRun() != null && book.getAvailableCopies() > book.getPrintRun()) {
            book.setAvailableCopies(book.getPrintRun());
        }

        bookRepository.saveAndFlush(book);
        log.info("Книга сохранена: {} / {}", bookDto.getTitle(), bookDto.getAuthor());
    }

    @Override
    @Cacheable(value = "books", key = "'all'")
    public List<ShowBookInfoDto> allBooks() {
        log.debug("Загрузка списка книг");
        List<ShowBookInfoDto> books = bookRepository.findAll().stream()
                .map(book -> mapper.map(book, ShowBookInfoDto.class))
                .collect(Collectors.toList());
        log.debug("Найдено книг: {}", books.size());
        return books;
    }

    @Override
    public ShowDetailedBookInfoDto bookInfo(String bookId) {
        log.debug("Детали книги: {}", bookId);
        Book book = bookRepository.findDetailedById(bookId)
                .orElseThrow(() -> {
                    log.warn("Книга не найдена: {}", bookId);
                    return new BookNotFoundException("Книга не найдена");
                });

        ShowDetailedBookInfoDto dto = mapper.map(book, ShowDetailedBookInfoDto.class);
        dto.setId(book.getId());
        if (book.getLibrary() != null) {
            dto.setLibraryName(book.getLibrary().getName());
        }
        if (book.getGenre() != null) {
            dto.setGenreLabel(book.getGenre().getDisplayName());
        }
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "books", allEntries = true)
    public void removeBook(String bookId) {
        log.debug("Удаление книги: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить несуществующую книгу: {}", bookId);
                    return new BookNotFoundException("Книга не найдена");
                });
        bookLoanRepository.findByBookId(bookId).forEach(bookLoanRepository::delete);
        bookRepository.delete(book);
        log.info("Книга удалена: {} / {}", book.getTitle(), book.getAuthor());
    }

    @Override
    public List<ShowBookInfoDto> findTopByTitle(int limit) {
        return bookRepository.findAllByOrderByTitleDesc(PageRequest.of(0, limit))
                .stream()
                .map(b -> mapper.map(b, ShowBookInfoDto.class))
                .collect(Collectors.toList());
    }
}
