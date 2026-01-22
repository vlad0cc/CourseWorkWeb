package ru.rutmiit.services;

import java.util.List;
import java.util.Optional;
import ru.rutmiit.dto.LoanDetailsDto;

public interface LoanService {
    LoanDetailsDto borrowBook(String bookId, String username, int days);

    void returnBook(String loanId);

    List<LoanDetailsDto> activeLoansForUser(String username);

    List<LoanDetailsDto> activeLoansForStaff();

    Optional<UserLoansResult> findLoansByUserQuery(String query);

    record UserLoansResult(String userId, String userFullName, String readerCardNumber,
                           List<LoanDetailsDto> activeLoans,
                           List<LoanDetailsDto> pastLoans) {}
}
