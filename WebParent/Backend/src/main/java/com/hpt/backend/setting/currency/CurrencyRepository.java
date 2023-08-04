package com.hpt.backend.setting.currency;

import com.hpt.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
    List<Currency> findAllByOrderByNameAsc();
}
