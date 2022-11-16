package com.wingedtech.common.util;

import com.wingedtech.common.dto.SensitiveData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public class SensitiveDataUtils {
    public static <D extends SensitiveData> D desensitize(D data) {
        data.desensitize();
        return data;
    }

    public static <D extends SensitiveData> Optional<D> desensitize(Optional<D> data) {
        data.ifPresent(SensitiveDataUtils::desensitize);
        return data;
    }

    public static <D extends SensitiveData> List<D> desensitize(List<D> list) {
        list.forEach(SensitiveDataUtils::desensitize);
        return list;
    }

    public static <D extends SensitiveData> Page<D> desensitize(Page<D> page) {
        page.forEach(SensitiveDataUtils::desensitize);
        return page;
    }
}
