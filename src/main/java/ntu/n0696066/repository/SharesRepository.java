package ntu.n0696066.repository;

import ntu.n0696066.model.Shares;
import ntu.n0696066.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharesRepository extends JpaRepository<Shares, Long> {

    Shares findByCompanySymbolAndUser(String companySymbol, User user);
}
