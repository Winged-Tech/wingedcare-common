package com.wingedtech.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 通用的service接口模板
 *
 * @param <D> DTO类型
 * @author Jason
 */
@SuppressWarnings("unused")
public interface ServiceTemplate<D> {

    /**
     * Save a dto.
     *
     * @param dto the entity to save
     * @return the persisted entity
     */
    <S extends D> S save(S dto);

    /**
     * Save a list of dto.
     *
     * @param dto
     * @return
     */
    <S extends D> List<S> saveAll(List<S> dto);

    <S extends D> List<S> findAll();

    /**
     * Get all the dto.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    <S extends D> Page<S> findAll(Pageable pageable);

    <S extends D> List<S> findAllById(Iterable<String> ids);

    /**
     * Get all the dto using example object.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    <S extends D> Page<S> findAllByExample(S example, Pageable pageable);

    /**
     * Get the "id" dto.
     *
     * @param id the id of the entity
     * @return the entity
     */
    <S extends D> Optional<S> findOne(String id);

    /**
     * Get one dto with example.
     *
     * @param example
     * @return
     */
    <S extends D> Optional<S> findOne(S example);

    /**
     * Delete the "id" dto.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Whether the "id" object exists
     *
     * @param id
     * @return
     */
    boolean existsById(String id);

    /**
     * Whether the "example" object exists
     *
     * @param example
     * @return
     */
    <S extends D> boolean existsByExample(S example);
}
