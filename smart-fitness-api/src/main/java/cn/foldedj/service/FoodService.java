package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.FoodQueryDto;
import cn.foldedj.pojo.entity.Food;
import cn.foldedj.pojo.vo.FoodVO;

import java.util.List;

/**
 * 食物业务逻辑接口
 */
public interface FoodService {

    /**
     * 新增食物
     *
     * @param food 食物信息
     * @return Result<Void> 通用响应体
     */
    Result<Void> save(Food food);

    /**
     * 批量删除食物
     *
     * @param ids 食物ID列表
     * @return Result<Void> 通用响应体
     */
    Result<Void> batchDelete(List<Integer> ids);

    /**
     * 更新食物信息
     *
     * @param food 食物信息
     * @return Result<Void> 通用响应体
     */
    Result<Void> update(Food food);

    /**
     * 查询食物列表
     *
     * @param foodQueryDto 查询参数
     * @return Result<List<FoodVO>> 通用响应
     */
    Result<List<FoodVO>> query(FoodQueryDto foodQueryDto);

    /**
     * 根据ID获取食物信息
     *
     * @param id 食物ID
     * @return Food 食物信息
     */
    Food getById(Integer id);

}