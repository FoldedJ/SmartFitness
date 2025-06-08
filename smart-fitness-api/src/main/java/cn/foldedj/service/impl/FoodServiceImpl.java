package cn.foldedj.service.impl;

import cn.foldedj.mapper.FoodMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.PageResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.FoodQueryDto;
import cn.foldedj.pojo.entity.Food;
import cn.foldedj.pojo.vo.FoodVO;
import cn.foldedj.service.FoodService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 食物业务逻辑实现
 */
@Service
public class FoodServiceImpl implements FoodService {

    @Resource
    private FoodMapper foodMapper;

    /**
     * 新增食物
     *
     * @param food 食物信息
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Food food) {
        food.setCreateTime(LocalDateTime.now());
        foodMapper.save(food);
        return ApiResult.success();
    }

    /**
     * 批量删除食物
     *
     * @param ids 食物ID列表
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        foodMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 更新食物信息
     *
     * @param food 食物信息
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Food food) {
        foodMapper.update(food);
        return ApiResult.success();
    }

    /**
     * 查询食物列表
     *
     * @param foodQueryDto 查询参数
     * @return Result<List<FoodVO>>
     */
    @Override
    public Result<List<FoodVO>> query(FoodQueryDto foodQueryDto) {
        List<FoodVO> foodList = foodMapper.query(foodQueryDto);
        Integer totalCount = foodMapper.queryCount(foodQueryDto);
        return PageResult.success(foodList, totalCount);
    }

    /**
     * 根据ID获取食物信息
     *
     * @param id 食物ID
     * @return Food 食物信息
     */
    @Override
    public Food getById(Integer id) {
        return foodMapper.getById(id);
    }

}