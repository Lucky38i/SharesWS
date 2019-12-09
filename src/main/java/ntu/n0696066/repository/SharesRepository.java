package ntu.n0696066.repository;

import ntu.n0696066.model.Shares;
import ntu.n0696066.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharesRepository extends JpaRepository<Shares, Long> {

    Optional<Shares> findByCompanySymbolAndUser(String companySymbol, User user);
}
