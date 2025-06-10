package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.NutritionRecommendationQueryDto;
import cn.foldedj.pojo.entity.NutritionRecommendation;
import cn.foldedj.pojo.vo.NutritionRecommendationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 营养推荐Mapper接口
 */
@Mapper
public interface NutritionRecommendationMapper {

    /**
     * 插入营养推荐
     *
     * @param nutritionRecommendation 营养推荐
     * @return 影响行数
     */
    int insert(NutritionRecommendation nutritionRecommendation);

    /**
     * 更新营养推荐
     *
     * @param nutritionRecommendation 营养推荐
     * @return 影响行数
     */
    int update(NutritionRecommendation nutritionRecommendation);

    /**
     * 批量删除营养推荐
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 查询营养推荐
     *
     * @param queryDto 查询条件
     * @return 营养推荐列表
     */
    List<NutritionRecommendationVO> query(NutritionRecommendationQueryDto queryDto);

    /**
     * 查询营养推荐数量
     *
     * @param queryDto 查询条件
     * @return 营养推荐数量
     */
    Integer queryCount(NutritionRecommendationQueryDto queryDto);

    /**
     * 根据ID获取营养推荐
     *
     * @param id ID
     * @return 营养推荐
     */
    NutritionRecommendationVO getById(@Param("id") Integer id);

    /**
     * 获取用户最新的营养推荐
     *
     * @param userId 用户ID
     * @return 最新的营养推荐
     */
    NutritionRecommendationVO getLatestByUserId(@Param("userId") Integer userId);
}