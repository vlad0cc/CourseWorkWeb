package ru.rutmiit.web;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.AddBookDto;
import ru.rutmiit.dto.BorrowBookDto;
import ru.rutmiit.dto.LoanDetailsDto;
import ru.rutmiit.services.BookService;
import ru.rutmiit.services.LibraryService;
import ru.rutmiit.services.LoanService;

@Slf4j
@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final LibraryService libraryService;
    private final LoanService loanService;

    public BookController(BookService bookService, LibraryService libraryService, LoanService loanService) {
        this.bookService = bookService;
        this.libraryService = libraryService;
        this.loanService = loanService;
        log.info("BookController готов");
    }

    @GetMapping("/add")
    public String addBook(Model model) {
        log.debug("GET форма добавления книги");
        model.addAttribute("availableLibraries", libraryService.allLibraries());
        return "book-add";
    }

    @ModelAttribute("bookModel")
    public AddBookDto initBook() {
        return new AddBookDto();
    }

    @ModelAttribute("borrowModel")
    public BorrowBookDto initBorrow() {
        return new BorrowBookDto();
    }

    @PostMapping("/add")
    public String addBook(@Valid AddBookDto bookModel, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("POST добавление книги: {} / {}", bookModel.getTitle(), bookModel.getAuthor());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки при сохранении книги: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("bookModel", bookModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookModel",
                    bindingResult);
            return "redirect:/books/add";
        }
        bookService.addBook(bookModel);
        log.info("Книга сохранена: {} / {}", bookModel.getTitle(), bookModel.getAuthor());

        return "redirect:/books/all";
    }

    @GetMapping("/all")
    public String showAllBooks(Model model) {
        log.debug("Список всех книг");
        model.addAttribute("allBooks", bookService.allBooks());
        return "book-all";
    }

    @GetMapping("/book-details/{book-id}")
    public String showBookDetails(@PathVariable("book-id") String bookId,
                                  Model model) {
        log.debug("Детали книги: {}", bookId);
        model.addAttribute("bookDetails", bookService.bookInfo(bookId));
        model.addAttribute("borrowModel", new BorrowBookDto());
        return "book-details";
    }

    @PostMapping("/borrow")
    public String borrowBook(@Valid BorrowBookDto borrowBookDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Principal principal) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Авторизуйтесь, чтобы взять книгу.");
            return "redirect:/users/login";
        }

        if (!principalHasUserRole()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Только читатели могут брать книги.");
            return "redirect:/books/book-details/" + borrowBookDto.getBookId();
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.borrowModel", bindingResult);
            redirectAttributes.addFlashAttribute("borrowModel", borrowBookDto);
            return "redirect:/books/book-details/" + borrowBookDto.getBookId();
        }

        try {
            loanService.borrowBook(borrowBookDto.getBookId(), principal.getName(), borrowBookDto.getDays());
            redirectAttributes.addFlashAttribute("successMessage", "Книга выдана на " + borrowBookDto.getDays() + " дней.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/books/book-details/" + borrowBookDto.getBookId();
    }

    private boolean principalHasUserRole() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @GetMapping("/my-loans")
    public String myLoans(Model model, Principal principal) {
        if (principal != null) {
            List<LoanDetailsDto> loans = loanService.activeLoansForUser(principal.getName());
            model.addAttribute("loans", loans);
        }
        return "loans-my";
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/active-loans")
    public String activeLoans(Model model) {
        List<LoanDetailsDto> loans = loanService.activeLoansForStaff();
        model.addAttribute("loans", loans);
        return "loans-active";
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @PostMapping("/return/{loan-id}")
    public String returnBook(@PathVariable("loan-id") String loanId,
                             @RequestParam(value = "backTo", required = false, defaultValue = "/books/active-loans") String backTo,
                             RedirectAttributes redirectAttributes) {
        try {
            loanService.returnBook(loanId);
            redirectAttributes.addFlashAttribute("successMessage", "Книга отмечена как возвращенная.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:" + backTo;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @DeleteMapping("/book-delete/{book-id}")
    public String removeBook(@PathVariable("book-id") String bookId) {
        log.debug("Удаление книги: {}", bookId);
        bookService.removeBook(bookId);
        log.info("Книга удалена: {}", bookId);

        return "redirect:/books/all";
    }

    @GetMapping("/title-desc")
    public String findTopByTitle(Model model) {
        model.addAttribute("allBooks", bookService.findTopByTitle(1));
        return "title-desc";
    }
    
}
