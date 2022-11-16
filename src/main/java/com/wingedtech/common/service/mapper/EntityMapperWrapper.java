package com.wingedtech.common.service.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于包装多个EntityMapper，并能根据传入参数的类型动态选择合适的mapper来处理数据
 *
 * @author taozhou
 */
public class EntityMapperWrapper {
    private final Map<Class, Object> mapperReferenceByDto = new HashMap<>();
    private final Map<Class, Object> mapperReferenceByEntity = new HashMap<>();

    public EntityMapperWrapper() {}

    public <D, E> void registerMapper(Class<D> dtoClass, Class<E> entityClass, EntityMapper<D, E> mapper) {
        mapperReferenceByDto.put(dtoClass, mapper);
        mapperReferenceByEntity.put(entityClass, mapper);
    }

    private <D, E> EntityMapper<D, E> getMapperByDto(D dto) {
        return (EntityMapper<D, E>) mapperReferenceByDto.get(dto.getClass());
    }

    private <D, E> EntityMapper<D, E> getMapperByEntity(E entity) {
        return (EntityMapper<D, E>) mapperReferenceByEntity.get(entity.getClass());
    }

    public <D, E> E toEntity(D dto) {
        final EntityMapper<D, E> mapperByDto = getMapperByDto(dto);
        return mapperByDto.toEntity(dto);
    }

    public <D, E> D toDto(E entity) {
        final EntityMapper<D, E> mapperByEntity = getMapperByEntity(entity);
        return mapperByEntity.toDto(entity);
    }

    public <D, E> List<E> toEntity(List<D> dtoList) {
        List<E> entityList = new ArrayList<>();
        for (D d : dtoList) {
            entityList.add(toEntity(d));
        }
        return entityList;
    }

    public <D, E> List<D> toDto(List<E> entityList) {
        List<D> dtoList = new ArrayList<>();
        for (E e : entityList) {
            dtoList.add(toDto(e));
        }
        return dtoList;
    }
}
