package com.icrown.backendapi.services;

import com.icrown.gameapi.daos.LanTranslationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TranslateService {

    @Autowired
    LanTranslationDAO translationDAO;

    public String translateCsvHeaderByLanguage(String language, String header) {
        if (!language.equals("zh-CN")) {
            Map<String, String> transMap = getTranslationMap(language);
            header = String.join(",", Arrays.asList(header.split(",")).stream().map(v -> fuzzyTranslationByTranlationMap(transMap, v)).collect(Collectors.toList()));
        }
        return header;
    }

    public String translateCsvHeaderByTranlationMap(Map<String, String> transMap, String header) {
        header = String.join(",", Arrays.asList(header.split(",")).stream().map(v -> fuzzyTranslationByTranlationMap(transMap, v)).collect(Collectors.toList()));

        return header;
    }

    public String fuzzyTranslationByTranlationMap(Map<String, String> transMap, String value) {
        value = value.trim();
        if (value == null || "".equals(value)) {
            return "";
        }

        if (transMap.isEmpty()) {
            return value;
        }

        String transValue = transMap.get(value);
        String tmpValue = value;
        while (transValue == null) {
            tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
            if (tmpValue.length() <= 0) {
                return value;
            }
            transValue = transMap.get(tmpValue);
        }
        return transValue;
    }

    public String fuzzyTranslation(String language, String value) {
        if (value == null) {
            return "";
        }
        String transValue = getTranslation(language, value);
        String tmpValue = value;
        while (StringUtils.isEmpty(transValue)) {
            tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
            if (tmpValue.length() == 0) {
                return value;
            }
            transValue = getTranslation(language, value);
        }
        return transValue;
    }

    public Map<String, String> getTranslationMap(String language) {
        return translationDAO.getTranslationListByLan(language);
    }

    public String getTranslation(String language, String value) {
        return translationDAO.getTranslationListByLanAndCNValue(language, value);
    }
}
