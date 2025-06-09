package cn.foldedj.controller;

import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NutritionRecommendationQueryDto;
import cn.foldedj.pojo.entity.NutritionRecommendation;
import cn.foldedj.pojo.entity.UserNutritionTarget;
import cn.foldedj.pojo.vo.NutritionRecommendationVO;
import cn.foldedj.pojo.vo.UserNutritionTargetVO;
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

    /**
     * 获取当前用户的营养目标
     *
     * @return Result<UserNutritionTargetVO> 用户营养目标
     */
    @GetMapping("/target")
    @ApiOperation("获取当前用户的营养目标")
    public Result<UserNutritionTargetVO> getUserNutritionTarget() {
        Integer userId = LocalThreadHolder.getUserId();
        return nutritionService.getUserNutritionTarget(userId);
    }

    /**
     * 获取指定用户的营养目标
     *
     * @param userId 用户ID
     * @return Result<UserNutritionTargetVO> 用户营养目标
     */
    @GetMapping("/target/{userId}")
    @ApiOperation("获取指定用户的营养目标")
    public Result<UserNutritionTargetVO> getUserNutritionTargetByUserId(@PathVariable Integer userId) {
        return nutritionService.getUserNutritionTarget(userId);
    }

    /**
     * 保存或更新用户营养目标
     *
     * @param userNutritionTarget 用户营养目标
     * @return Result<Void> 操作结果
     */
    @PostMapping("/target/save")
    @ApiOperation("保存或更新用户营养目标")
    public Result<Void> saveOrUpdateUserNutritionTarget(@RequestBody UserNutritionTarget userNutritionTarget) {
        return nutritionService.saveOrUpdateUserNutritionTarget(userNutritionTarget);
    }

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
}