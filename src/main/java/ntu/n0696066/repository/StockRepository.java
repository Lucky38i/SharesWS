package ntu.n0696066.repository;

import ntu.n0696066.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByShareSymbol(String shareSymbol);
}
