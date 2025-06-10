package cn.foldedj.controller;

import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.mapper.NutritionRecommendationMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NutritionRecommendationQueryDto;
import cn.foldedj.pojo.entity.NutritionRecommendation;
import cn.foldedj.pojo.vo.NutritionRecommendationVO;
import cn.foldedj.service.NutritionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 营养推荐控制器
 */
@RestController
@RequestMapping("/nutrition")
@Api(tags = "营养推荐管理")
@Slf4j
public class NutritionController {

    @Resource
    private NutritionService nutritionService;

    @Resource
    private NutritionRecommendationMapper nutritionRecommendationMapper;



    /**
     * 为当前用户生成营养推荐
     *
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    @GetMapping("/recommendation/generate")
    @ApiOperation("为当前用户生成营养推荐")
    public Result<NutritionRecommendationVO> generateNutritionRecommendation() {
        Integer userId = LocalThreadHolder.getUserId();
        return nutritionService.generateNutritionRecommendation(userId);
    }

    /**
     * 为指定用户生成营养推荐
     *
     * @param userId 用户ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    @GetMapping("/recommendation/generate/{userId}")
    @ApiOperation("为指定用户生成营养推荐")
    public Result<NutritionRecommendationVO> generateNutritionRecommendationForUser(@PathVariable Integer userId) {
        return nutritionService.generateNutritionRecommendation(userId);
    }

    /**
     * 保存营养推荐
     *
     * @param nutritionRecommendation 营养推荐
     * @return Result<Void> 操作结果
     */
    @PostMapping("/recommendation/save")
    @ApiOperation("保存营养推荐")
    public Result<Void> saveNutritionRecommendation(@RequestBody NutritionRecommendation nutritionRecommendation) {
        return nutritionService.saveNutritionRecommendation(nutritionRecommendation);
    }

    /**
     * 查询营养推荐
     *
     * @param queryDto 查询条件
     * @return Result<List<NutritionRecommendationVO>> 营养推荐列表
     */
    @PostMapping("/recommendation/query")
    @ApiOperation("查询营养推荐")
    public Result<List<NutritionRecommendationVO>> queryNutritionRecommendations(@RequestBody NutritionRecommendationQueryDto queryDto) {
        return nutritionService.queryNutritionRecommendations(queryDto);
    }



    /**
     * 根据ID获取营养推荐
     *
     * @param id 营养推荐ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    @GetMapping("/recommendation/{id}")
    @ApiOperation("根据ID获取营养推荐")
    public Result<NutritionRecommendationVO> getNutritionRecommendationById(@PathVariable Integer id) {
        return nutritionService.getNutritionRecommendationById(id);
    }

    /**
     * 删除营养推荐
     *
     * @param ids 营养推荐ID列表
     * @return Result<Void> 操作结果
     */
    @PostMapping("/recommendation/delete")
    @ApiOperation("删除营养推荐")
    public Result<Void> deleteNutritionRecommendations(@RequestBody List<Integer> ids) {
        return nutritionService.deleteNutritionRecommendations(ids);
    }

    /**
     * 获取当前用户最新的营养推荐
     *
     * @return Result<NutritionRecommendationVO> 最新的营养推荐
     */
    @GetMapping("/recommendation/latest")
    @ApiOperation("获取当前用户最新的营养推荐")
    public Result<NutritionRecommendationVO> getLatestNutritionRecommendation() {
        Integer userId = LocalThreadHolder.getUserId();
        return nutritionService.getLatestNutritionRecommendation(userId);
    }

}