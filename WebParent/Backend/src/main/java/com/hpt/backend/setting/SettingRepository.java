package com.hpt.backend.setting;

import com.hpt.common.entity.Setting;
import org.springframework.data.repository.CrudRepository;

public interface SettingRepository extends CrudRepository<Setting, String> {
}
