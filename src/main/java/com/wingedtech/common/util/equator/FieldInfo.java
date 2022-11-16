package com.wingedtech.common.util.equator;

import lombok.Data;

import java.util.Objects;

/**
 * 不同的属性
 *
 * @author Jason
 * @since 2019-07-16 18:46
 */
@Data
public class FieldInfo {

    private String fieldName;
    private Class<?> fieldType;
    private Object firstVal;
    private Object secondVal;

    public FieldInfo() {
    }

    public FieldInfo(String fieldName, Class<?> fieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(fieldName, fieldInfo.fieldName) &&
                Objects.equals(fieldType, fieldInfo.fieldType) &&
                Objects.equals(firstVal, fieldInfo.firstVal) &&
                Objects.equals(secondVal, fieldInfo.secondVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType, firstVal, secondVal);
    }
    
}
