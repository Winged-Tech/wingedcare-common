package com.wingedtech.common.service.beloging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用于管理ObjectWithUserId类型对象的service接口模板
 * @param <D>
 */
public interface ObjectWithUserIdServiceTemplate<D extends ObjectWithUserId> {

    /**
     * 为当前登录用户保存一条记录
     * @param dto
     * @return
     */
    D saveForCurrentUser(D dto);

    /**
     * 为当前登录用户获取所有记录
     * @param pageable
     * @return
     */
    Page<D> findAllForCurrentUser(Pageable pageable);

    /**
     * 使用Example对象为当前登录用户获取所有记录
     * @param pageable
     * @return
     */
    Page<D> findAllForCurrentUserByExample(D example, Pageable pageable);

    /**
     * 为当前登录用户获取一条记录 - 如果该记录不属于当前用户则返回空
     * @param id
     * @return
     */
    Optional<D> findOneForCurrentUser(String id);

    /**
     * 根据Example对象查询当前用户是否有指定的记录
     * @param example
     * @return
     */
    boolean existsForCurrentUser(D example);

    /**
     * 获取当前用户的userId
     * @return
     */
    String getCurrentUserId();

    /**
     * 判断指定对象是否属于当前用户
     * @param example
     * @return
     */
    boolean belongsToCurrentUser(D example);
}
