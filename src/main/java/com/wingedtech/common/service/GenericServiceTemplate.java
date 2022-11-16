package com.wingedtech.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 通用的service接口模板
 * @param <D> DTO类型
 *
 * @author taozhou
 */
@SuppressWarnings("unused")
public interface GenericServiceTemplate<D> {

    /**
     * Save a dto.
     *
     * @param dto the entity to save
     * @return the persisted entity
     */
    D save(D dto);

    /**
     * Save a list of dto.
     * @param dto
     * @return
     */
    List<D> saveAll(List<D> dto);

    List<D> findAll();

    /**
     * Get all the dto.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<D> findAll(Pageable pageable);

    List<D> findAllById(Iterable<String> ids);

    /**
     * Get all the dto using example object.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<D> findAllByExample(D example, Pageable pageable);

    /**
     * Get the "id" dto.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<D> findOne(String id);

    /**
     * Get one dto with example.
     * @param example
     * @return
     */
    Optional<D> findOne(D example);

    /**
     * Delete the "id" dto.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Whether the "id" object exists
     * @param id
     * @return
     */
    boolean existsById(String id);

    /**
     * Whether the "example" object exists
     * @param example
     * @return
     */
    boolean existsByExample(D example);
}
