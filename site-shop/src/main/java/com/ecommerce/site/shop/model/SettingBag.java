package com.ecommerce.site.shop.model;

import com.ecommerce.site.shop.model.entity.Setting;

import java.util.List;


public class SettingBag {

    private final List<Setting> listSettings;

    public SettingBag(List<Setting> listSettings) {
        this.listSettings = listSettings;
    }

    public Setting get(String key) {
        int index = listSettings.indexOf(new Setting(key));
        if (index >= 0) {
            return listSettings.get(index);
        }
        return null;
    }

    public String getValue(String key) {
        Setting setting = get(key);
        if (setting != null) {
            return setting.getValue();
        }
        return null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return listSettings;
    }

}
