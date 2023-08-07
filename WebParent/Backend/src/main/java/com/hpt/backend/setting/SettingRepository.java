package com.hpt.backend.setting;

import com.hpt.common.entity.Setting;
import com.hpt.common.entity.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}
