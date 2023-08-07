package com.hpt.backend.setting;

import com.hpt.backend.setting.currency.CurrencyRepository;
import com.hpt.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class CurrencyRepositoryTest {
    @Autowired
    private CurrencyRepository repo;

    @Test
    public void testCreateCurrencies() {
        List<Currency> listCurrencies = Arrays.asList(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("Vietnamese đồng ", "₫", "VND")
        );

        repo.saveAll(listCurrencies);

        Iterable<Currency> iterable = repo.findAll();

        assertThat(iterable).size().isEqualTo(2);
    }
}