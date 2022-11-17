package com.wingedtech.common.sensitivity;

/**
 * @author 6688SUN
 */
@FunctionalInterface
public interface SensitivityCustomStrategy<T> {

    /**
     * 脱敏
     */
  T desensitizer(T t);
}
