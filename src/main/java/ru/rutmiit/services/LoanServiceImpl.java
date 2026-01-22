package ru.rutmiit.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.LoanDetailsDto;
import ru.rutmiit.models.entities.Book;
import ru.rutmiit.models.entities.BookLoan;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.exceptions.BookNotFoundException;
import ru.rutmiit.repositories.BookLoanRepository;
import ru.rutmiit.repositories.BookRepository;
import ru.rutmiit.repositories.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private static final int MAX_DAYS = 14;

    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public LoanServiceImpl(BookLoanRepository bookLoanRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository,
                           ModelMapper mapper) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"books"}, allEntries = true)
    public LoanDetailsDto borrowBook(String bookId, String username, int days) {
        if (days < 1 || days > MAX_DAYS) {
            throw new IllegalArgumentException("Срок выдачи может быть от 1 до 14 дней.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга не найдена"));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Нет доступных экземпляров книги.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        BookLoan loan = new BookLoan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStartDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(days));
        loan.setReturnedAt(null);

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        bookLoanRepository.save(loan);
        bookRepository.save(book);

        return toDto(loan);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"books"}, allEntries = true)
    public void returnBook(String loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Выдача не найдена"));

        if (loan.isReturned()) {
            return;
        }

        loan.setReturnedAt(LocalDate.now());
        Book book = loan.getBook();
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + 1));

        bookLoanRepository.save(loan);
        bookRepository.save(book);
    }

    @Override
    public List<LoanDetailsDto> activeLoansForUser(String username) {
        return bookLoanRepository.findActiveByUsername(username).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanDetailsDto> activeLoansForStaff() {
        return bookLoanRepository.findAllActive().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserLoansResult> findLoansByUserQuery(String query) {
        Optional<User> userOpt = userRepository.findByReaderCardNumber(query)
                .or(() -> userRepository.findFirstByFullNameIgnoreCase(query));

        return userOpt.map(user -> {
            List<LoanDetailsDto> all = bookLoanRepository.findByUserIdOrderByStartDateDesc(user.getId()).stream()
                    .map(this::toDto)
                    .toList();
            List<LoanDetailsDto> active = all.stream().filter(LoanDetailsDto::isActive).toList();
            List<LoanDetailsDto> past = all.stream().filter(l -> !l.isActive()).toList();
            return new UserLoansResult(user.getId(), user.getFullName(), user.getReaderCardNumber(), active, past);
        });
    }

    private LoanDetailsDto toDto(BookLoan loan) {
        LoanDetailsDto dto = mapper.map(loan, LoanDetailsDto.class);
        dto.setLoanId(loan.getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setBookAuthor(loan.getBook().getAuthor());
        dto.setIsbn(loan.getBook().getIsbn());
        dto.setUserFullName(loan.getUser().getFullName());
        dto.setUsername(loan.getUser().getUsername());
        dto.setReaderCardNumber(loan.getUser().getReaderCardNumber());
        return dto;
    }
}
