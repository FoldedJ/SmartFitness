package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NutritionRecommendationQueryDto;
import cn.foldedj.pojo.entity.NutritionRecommendation;
import cn.foldedj.pojo.vo.NutritionRecommendationVO;

import java.util.List;

/**
 * 营养推荐服务接口
 */
public interface NutritionService {



    /**
     * 根据用户ID生成营养推荐
     *
     * @param userId 用户ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    Result<NutritionRecommendationVO> generateNutritionRecommendation(Integer userId);

    /**
     * 保存营养推荐
     *
     * @param nutritionRecommendation 营养推荐
     * @return Result<Void> 操作结果
     */
    Result<Void> saveNutritionRecommendation(NutritionRecommendation nutritionRecommendation);

    /**
     * 查询营养推荐
     *
     * @param queryDto 查询条件
     * @return Result<List<NutritionRecommendationVO>> 营养推荐列表
     */
    Result<List<NutritionRecommendationVO>> queryNutritionRecommendations(NutritionRecommendationQueryDto queryDto);

    /**
     * 根据ID获取营养推荐
     *
     * @param id 营养推荐ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    Result<NutritionRecommendationVO> getNutritionRecommendationById(Integer id);

    /**
     * 删除营养推荐
     *
     * @param ids 营养推荐ID列表
     * @return Result<Void> 操作结果
     */
    Result<Void> deleteNutritionRecommendations(List<Integer> ids);

    /**
     * 获取用户最新的营养推荐
     *
     * @param userId 用户ID
     * @return Result<NutritionRecommendationVO> 最新的营养推荐
     */
    Result<NutritionRecommendationVO> getLatestNutritionRecommendation(Integer userId);
}