package com.hpt.frontend.setting;

import com.hpt.common.entity.setting.Setting;
import com.hpt.common.entity.setting.SettingCategory;
import com.hpt.frontend.setting.currency.CurrencySettingBag;
import com.hpt.frontend.setting.email.EmailSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {
    @Autowired
    private SettingRepository repo;

    /**
     * Get all settings
     *
     * @return a list of settings
     */
    public List<Setting> listAllSettings() {
        return (List<Setting>) repo.findAll();
    }

    /**
     * Get general settings
     *
     * @return a list of general settings
     */
    public List<Setting> getGeneralSettings() {
        return repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    /**
     * Get email settings
     *
     * @return a list of email settings
     */
    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = repo.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));

        return new EmailSettingBag(settings);
    }

    /**
     * Get currency settings
     *
     * @return a list of currency settings
     */
    public CurrencySettingBag getCurrencySettings() {
        List<Setting> settings = repo.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(settings);
    }
}
