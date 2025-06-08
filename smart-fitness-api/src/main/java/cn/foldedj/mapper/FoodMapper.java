package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.FoodQueryDto;
import cn.foldedj.pojo.entity.Food;
import cn.foldedj.pojo.vo.FoodVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 食物持久化接口
 */
@Mapper
public interface FoodMapper {

    /**
     * 新增食物
     *
     * @param food 食物信息
     */
    void save(Food food);

    /**
     * 更新食物信息
     *
     * @param food 食物信息
     */
    void update(Food food);

    /**
     * 批量删除食物
     *
     * @param ids 食物ID列表
     */
    void batchDelete(@Param(value = "ids") List<Integer> ids);

    /**
     * 查询食物列表
     *
     * @param foodQueryDto 查询参数
     * @return 食物列表
     */
    List<FoodVO> query(FoodQueryDto foodQueryDto);

    /**
     * 查询食物总数
     *
     * @param foodQueryDto 查询参数
     * @return 总数
     */
    Integer queryCount(FoodQueryDto foodQueryDto);

    /**
     * 根据ID获取食物信息
     *
     * @param id 食物ID
     * @return 食物信息
     */
    Food getById(@Param("id") Integer id);

}