package com.hpt.backend.setting;

import com.hpt.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
}
