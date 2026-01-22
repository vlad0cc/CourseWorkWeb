package ru.rutmiit.web;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rutmiit.services.LoanService;

@Slf4j
@Controller
@RequestMapping("/staff/loans")
@PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
public class StaffLoanController {

    private final LoanService loanService;

    public StaffLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public String searchLoans(@RequestParam(value = "q", required = false) String query, Model model) {
        if (query != null && !query.isBlank()) {
            Optional<LoanService.UserLoansResult> result = loanService.findLoansByUserQuery(query.trim());
            if (result.isPresent()) {
                model.addAttribute("userLoans", result.get());
            } else {
                model.addAttribute("errorMessage", "Пользователь не найден.");
            }
            model.addAttribute("q", query);
        }
        return "staff-loans";
    }
}
