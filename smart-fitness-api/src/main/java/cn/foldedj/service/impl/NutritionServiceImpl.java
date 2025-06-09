package cn.foldedj.service.impl;

import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.mapper.NutritionRecommendationMapper;
import cn.foldedj.mapper.UserHealthMapper;
import cn.foldedj.mapper.UserMapper;
import cn.foldedj.mapper.UserNutritionTargetMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.PageResult;
import cn.foldedj.pojo.api.Result;
import java.util.ArrayList;
import cn.foldedj.pojo.dto.query.extend.HealthModelConfigQueryDto;
import cn.foldedj.pojo.dto.query.extend.NutritionRecommendationQueryDto;
import cn.foldedj.pojo.dto.query.extend.UserHealthQueryDto;
import cn.foldedj.pojo.entity.HealthModelConfig;
import cn.foldedj.pojo.entity.NutritionRecommendation;
import cn.foldedj.pojo.entity.User;
import cn.foldedj.pojo.entity.UserHealth;
import cn.foldedj.pojo.entity.UserNutritionTarget;
import cn.foldedj.pojo.vo.HealthModelConfigVO;
import cn.foldedj.pojo.vo.NutritionRecommendationVO;
import cn.foldedj.pojo.vo.UserHealthVO;
import cn.foldedj.pojo.vo.UserNutritionTargetVO;
import cn.foldedj.service.HealthModelConfigService;
import cn.foldedj.service.NutritionService;
import cn.foldedj.service.UserHealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;

/**
 * 营养推荐服务实现类
 */
@Service
@Slf4j
public class NutritionServiceImpl implements NutritionService {

    @Resource
    private UserNutritionTargetMapper userNutritionTargetMapper;

    @Resource
    private NutritionRecommendationMapper nutritionRecommendationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserHealthMapper userHealthMapper;

    @Resource
    private HealthModelConfigService healthModelConfigService;

    /**
     * 根据用户ID获取用户营养目标
     *
     * @param userId 用户ID
     * @return Result<UserNutritionTargetVO> 用户营养目标
     */
    @Override
    public Result<UserNutritionTargetVO> getUserNutritionTarget(Integer userId) {
        UserNutritionTarget target = userNutritionTargetMapper.getByUserId(userId);
        if (target == null) {
            // 如果没有设置过营养目标，则生成默认目标
            target = generateDefaultNutritionTarget(userId);
            userNutritionTargetMapper.insert(target);
        }

        UserNutritionTargetVO targetVO = new UserNutritionTargetVO();
        BeanUtils.copyProperties(target, targetVO);
        
        // 获取用户最新的身高和体重数据
        java.util.Map<String, Object> heightAndWeight = userNutritionTargetMapper.getUserLatestHeightAndWeight(userId);
        log.info("用户ID: {} 的身高体重数据: {}", userId, heightAndWeight);
        
        if (heightAndWeight != null) {
            log.info("身高数据: {}, 体重数据: {}", heightAndWeight.get("HEIGHT"), heightAndWeight.get("WEIGHT"));
            if (heightAndWeight.get("HEIGHT") != null) {
                String heightStr = String.valueOf(heightAndWeight.get("HEIGHT"));
                if (heightStr != null && !heightStr.isEmpty()) {
                    try {
                        targetVO.setHeight(Double.parseDouble(heightStr));
                        log.info("设置身高: {}", targetVO.getHeight());
                    } catch (NumberFormatException e) {
                        log.error("解析身高数据失败: {}", heightStr, e);
                    }
                }
            }
            if (heightAndWeight.get("WEIGHT") != null) {
                String weightStr = String.valueOf(heightAndWeight.get("WEIGHT"));
                if (weightStr != null && !weightStr.isEmpty()) {
                    try {
                        targetVO.setWeight(Double.parseDouble(weightStr));
                        log.info("设置体重: {}", targetVO.getWeight());
                    } catch (NumberFormatException e) {
                        log.error("解析体重数据失败: {}", weightStr, e);
                    }
                }
            }
        }

        // 获取用户信息
        User user = userMapper.getByActive(User.builder().id(userId).build());
        if (user != null) {
            targetVO.setUserName(user.getUserName());
            targetVO.setGender(user.getGender());
            
            // 计算年龄
            if (user.getBirthDate() != null) {
                LocalDate now = LocalDate.now();
                Period period = Period.between(user.getBirthDate(), now);
                targetVO.setAge(period.getYears());
            }
        }

        // 获取用户身高和体重
        // 直接从USER_HEALTH表查询，HEALTH_MODEL_CONFIG_ID=1为身高，HEALTH_MODEL_CONFIG_ID=2为体重
        UserHealthQueryDto heightQueryDto = new UserHealthQueryDto();
        heightQueryDto.setUserId(userId);
        heightQueryDto.setHealthModelConfigId(1); // 身高的HEALTH_MODEL_CONFIG_ID为1
        List<UserHealthVO> heightRecords = userHealthMapper.query(heightQueryDto);
        
        UserHealthQueryDto weightQueryDto = new UserHealthQueryDto();
        weightQueryDto.setUserId(userId);
        weightQueryDto.setHealthModelConfigId(2); // 体重的HEALTH_MODEL_CONFIG_ID为2
        List<UserHealthVO> weightRecords = userHealthMapper.query(weightQueryDto);
        
        if (!heightRecords.isEmpty() && !weightRecords.isEmpty()) {
            // 获取最新的身高和体重记录（查询结果已按CREATE_TIME DESC排序）
            Double height = Double.parseDouble(heightRecords.get(0).getValue());
            Double weight = Double.parseDouble(weightRecords.get(0).getValue());
            
            targetVO.setHeight(height);
            targetVO.setWeight(weight);
            
            // 计算BMI
            if (height > 0) {
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);
                targetVO.setBmi(BigDecimal.valueOf(bmi).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
        }
        
        return ApiResult.success(targetVO);
    }

    /**
     * 保存或更新用户营养目标
     *
     * @param userNutritionTarget 用户营养目标
     * @return Result<Void> 操作结果
     */
    @Override
    public Result<Void> saveOrUpdateUserNutritionTarget(UserNutritionTarget userNutritionTarget) {
        Integer userId = userNutritionTarget.getUserId();
        if (userId == null) {
            userId = LocalThreadHolder.getUserId();
            userNutritionTarget.setUserId(userId);
        }
        
        UserNutritionTarget existingTarget = userNutritionTargetMapper.getByUserId(userId);
        if (existingTarget == null) {
            userNutritionTarget.setCreateTime(LocalDateTime.now());
            userNutritionTargetMapper.insert(userNutritionTarget);
        } else {
            userNutritionTarget.setId(existingTarget.getId());
            userNutritionTarget.setUpdateTime(LocalDateTime.now());
            userNutritionTargetMapper.update(userNutritionTarget);
        }
        
        return ApiResult.success();
    }

    /**
     * 根据用户ID生成营养推荐
     *
     * @param userId 用户ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    @Override
    public Result<NutritionRecommendationVO> generateNutritionRecommendation(Integer userId) {
        // 获取用户信息
        User user = userMapper.getByActive(User.builder().id(userId).build());
        if (user == null) {
            return ApiResult.error("用户不存在");
        }
        
        // 获取用户身高和体重
        Double height = null;
        Double weight = null;
        
        // 直接从USER_HEALTH表查询，HEALTH_MODEL_CONFIG_ID=1为身高，HEALTH_MODEL_CONFIG_ID=2为体重
        UserHealthQueryDto heightQueryDto = new UserHealthQueryDto();
        heightQueryDto.setUserId(userId);
        heightQueryDto.setHealthModelConfigId(1); // 身高的HEALTH_MODEL_CONFIG_ID为1
        List<UserHealthVO> heightRecords = userHealthMapper.query(heightQueryDto);
        
        UserHealthQueryDto weightQueryDto = new UserHealthQueryDto();
        weightQueryDto.setUserId(userId);
        weightQueryDto.setHealthModelConfigId(2); // 体重的HEALTH_MODEL_CONFIG_ID为2
        List<UserHealthVO> weightRecords = userHealthMapper.query(weightQueryDto);
        
        if (!heightRecords.isEmpty() && !weightRecords.isEmpty()) {
            // 获取最新的身高和体重记录（查询结果已按CREATE_TIME DESC排序）
            height = Double.parseDouble(heightRecords.get(0).getValue());
            weight = Double.parseDouble(weightRecords.get(0).getValue());
        }
        
        if (height == null || weight == null) {
            return ApiResult.error("缺少用户身高或体重信息，无法生成营养推荐");
        }
        
        // 计算年龄
        Integer age = null;
        if (user.getBirthDate() != null) {
            LocalDate now = LocalDate.now();
            Period period = Period.between(user.getBirthDate(), now);
            age = period.getYears();
        } else {
            return ApiResult.error("缺少用户出生日期信息，无法生成营养推荐");
        }
        
        // 计算BMI
        double heightInMeters = height / 100.0;
        double bmi = weight / (heightInMeters * heightInMeters);
        bmi = BigDecimal.valueOf(bmi).setScale(2, RoundingMode.HALF_UP).doubleValue();
        
        // 设置默认的营养素分配
        int calories = 2000; // 默认热量
        
        // 蛋白质：每千克体重1.6-2.2克
        BigDecimal protein = BigDecimal.valueOf(weight * 1.8).setScale(2, RoundingMode.HALF_UP);
        
        // 脂肪：总热量的25-30%
        BigDecimal fat = BigDecimal.valueOf(calories * 0.25 / 9).setScale(2, RoundingMode.HALF_UP);
        
        // 碳水化合物：剩余热量
        BigDecimal proteinCalories = protein.multiply(BigDecimal.valueOf(4));
        BigDecimal fatCalories = fat.multiply(BigDecimal.valueOf(9));
        BigDecimal carbCalories = BigDecimal.valueOf(calories).subtract(proteinCalories).subtract(fatCalories);
        BigDecimal carbohydrate = carbCalories.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        
        // 膳食纤维：每1000卡路里14克
        BigDecimal fiber = BigDecimal.valueOf(calories * 14.0 / 1000).setScale(2, RoundingMode.HALF_UP);
        
        // 钠：2300毫克
        BigDecimal sodium = BigDecimal.valueOf(2300).setScale(2, RoundingMode.HALF_UP);
        
        // 创建营养推荐
        NutritionRecommendation recommendation = NutritionRecommendation.builder()
                .userId(userId)
                .recommendationDate(LocalDate.now())
                .calories(calories)
                .protein(protein)
                .carbohydrate(carbohydrate)
                .fat(fat)
                .fiber(fiber)
                .sodium(sodium)
                .recommendationType("DAILY")
                .notes("基于用户身高、体重、年龄和性别自动生成的营养推荐")
                .createTime(LocalDateTime.now())
                .build();
        
        // 保存营养推荐
        nutritionRecommendationMapper.insert(recommendation);
        
        // 构建返回VO
        NutritionRecommendationVO recommendationVO = new NutritionRecommendationVO();
        BeanUtils.copyProperties(recommendation, recommendationVO);
        recommendationVO.setUserName(user.getUserName());
        recommendationVO.setGender(user.getGender());
        recommendationVO.setAge(age);
        recommendationVO.setHeight(height);
        recommendationVO.setWeight(weight);
        recommendationVO.setBmi(bmi);
        
        return ApiResult.success(recommendationVO);
    }

    /**
     * 保存营养推荐
     *
     * @param nutritionRecommendation 营养推荐
     * @return Result<Void> 操作结果
     */
    @Override
    public Result<Void> saveNutritionRecommendation(NutritionRecommendation nutritionRecommendation) {
        if (nutritionRecommendation.getUserId() == null) {
            nutritionRecommendation.setUserId(LocalThreadHolder.getUserId());
        }
        
        if (nutritionRecommendation.getRecommendationDate() == null) {
            nutritionRecommendation.setRecommendationDate(LocalDate.now());
        }
        
        nutritionRecommendation.setCreateTime(LocalDateTime.now());
        nutritionRecommendationMapper.insert(nutritionRecommendation);
        
        return ApiResult.success();
    }

    /**
     * 查询营养推荐
     *
     * @param queryDto 查询条件
     * @return Result<List<NutritionRecommendationVO>> 营养推荐列表
     */
    @Override
    public Result<List<NutritionRecommendationVO>> queryNutritionRecommendations(NutritionRecommendationQueryDto queryDto) {
        List<NutritionRecommendationVO> recommendations = nutritionRecommendationMapper.query(queryDto);
        Integer count = nutritionRecommendationMapper.queryCount(queryDto);
        return PageResult.success(recommendations, count);
    }

    /**
     * 根据ID获取营养推荐
     *
     * @param id 营养推荐ID
     * @return Result<NutritionRecommendationVO> 营养推荐
     */
    @Override
    public Result<NutritionRecommendationVO> getNutritionRecommendationById(Integer id) {
        NutritionRecommendationVO recommendation = nutritionRecommendationMapper.getById(id);
        if (recommendation == null) {
            return ApiResult.error("营养推荐不存在");
        }
        
        // 获取用户信息
        User user = userMapper.getByActive(User.builder().id(recommendation.getUserId()).build());
        if (user != null) {
            recommendation.setUserName(user.getUserName());
            recommendation.setGender(user.getGender());
            
            // 计算年龄
            if (user.getBirthDate() != null) {
                LocalDate now = LocalDate.now();
                Period period = Period.between(user.getBirthDate(), now);
                recommendation.setAge(period.getYears());
            }
        }
        
        // 获取用户身高和体重
        // 直接从USER_HEALTH表查询，HEALTH_MODEL_CONFIG_ID=1为身高，HEALTH_MODEL_CONFIG_ID=2为体重
        UserHealthQueryDto heightQueryDto = new UserHealthQueryDto();
        heightQueryDto.setUserId(recommendation.getUserId());
        heightQueryDto.setHealthModelConfigId(1); // 身高的HEALTH_MODEL_CONFIG_ID为1
        List<UserHealthVO> heightRecords = userHealthMapper.query(heightQueryDto);
        
        UserHealthQueryDto weightQueryDto = new UserHealthQueryDto();
        weightQueryDto.setUserId(recommendation.getUserId());
        weightQueryDto.setHealthModelConfigId(2); // 体重的HEALTH_MODEL_CONFIG_ID为2
        List<UserHealthVO> weightRecords = userHealthMapper.query(weightQueryDto);
        
        if (!heightRecords.isEmpty() && !weightRecords.isEmpty()) {
            // 获取最新的身高和体重记录（查询结果已按CREATE_TIME DESC排序）
            Double height = Double.parseDouble(heightRecords.get(0).getValue());
            Double weight = Double.parseDouble(weightRecords.get(0).getValue());
            
            recommendation.setHeight(height);
            recommendation.setWeight(weight);
            
            // 计算BMI
            if (height > 0) {
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);
                recommendation.setBmi(BigDecimal.valueOf(bmi).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            
            // 不再计算BMR和TDEE
        }
        
        return ApiResult.success(recommendation);
    }

    /**
     * 删除营养推荐
     *
     * @param ids 营养推荐ID列表
     * @return Result<Void> 操作结果
     */
    @Override
    public Result<Void> deleteNutritionRecommendations(List<Integer> ids) {
        nutritionRecommendationMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 生成默认营养目标
     *
     * @param userId 用户ID
     * @return UserNutritionTarget 默认营养目标
     */
    private UserNutritionTarget generateDefaultNutritionTarget(Integer userId) {
        // 获取用户信息
        User user = userMapper.getByActive(User.builder().id(userId).build());
        if (user == null) {
            // 如果用户不存在，返回通用默认值
            return UserNutritionTarget.builder()
                    .userId(userId)
                    .calories(2000)
                    .protein(BigDecimal.valueOf(60))
                    .carbohydrate(BigDecimal.valueOf(250))
                    .fat(BigDecimal.valueOf(70))
                    .fiber(BigDecimal.valueOf(28))
                    .sodium(BigDecimal.valueOf(2300))
                    .createTime(LocalDateTime.now())
                    .build();
        }
        
        // 获取用户身高和体重
        Double height = null;
        Double weight = null;
        
        // 直接从USER_HEALTH表查询，HEALTH_MODEL_CONFIG_ID=1为身高，HEALTH_MODEL_CONFIG_ID=2为体重
        UserHealthQueryDto heightQueryDto = new UserHealthQueryDto();
        heightQueryDto.setUserId(userId);
        heightQueryDto.setHealthModelConfigId(1); // 身高的HEALTH_MODEL_CONFIG_ID为1
        List<UserHealthVO> heightRecords = userHealthMapper.query(heightQueryDto);
        
        UserHealthQueryDto weightQueryDto = new UserHealthQueryDto();
        weightQueryDto.setUserId(userId);
        weightQueryDto.setHealthModelConfigId(2); // 体重的HEALTH_MODEL_CONFIG_ID为2
        List<UserHealthVO> weightRecords = userHealthMapper.query(weightQueryDto);
        
        if (!heightRecords.isEmpty() && !weightRecords.isEmpty()) {
            // 获取最新的身高和体重记录（查询结果已按CREATE_TIME DESC排序）
            height = Double.parseDouble(heightRecords.get(0).getValue());
            weight = Double.parseDouble(weightRecords.get(0).getValue());
        }
        
        // 如果没有身高体重信息，返回基于性别的默认值
        if (height == null || weight == null) {
            if ("男".equals(user.getGender()) || "male".equalsIgnoreCase(user.getGender())) {
                return UserNutritionTarget.builder()
                        .userId(userId)
                        .calories(2500)
                        .protein(BigDecimal.valueOf(80))
                        .carbohydrate(BigDecimal.valueOf(300))
                        .fat(BigDecimal.valueOf(80))
                        .fiber(BigDecimal.valueOf(35))
                        .sodium(BigDecimal.valueOf(2300))
                        .createTime(LocalDateTime.now())
                        .build();
            } else {
                return UserNutritionTarget.builder()
                        .userId(userId)
                        .calories(2000)
                        .protein(BigDecimal.valueOf(60))
                        .carbohydrate(BigDecimal.valueOf(250))
                        .fat(BigDecimal.valueOf(65))
                        .fiber(BigDecimal.valueOf(28))
                        .sodium(BigDecimal.valueOf(2300))
                        .createTime(LocalDateTime.now())
                        .build();
            }
        }
        
        // 计算年龄
        Integer age = null;
        if (user.getBirthDate() != null) {
            LocalDate now = LocalDate.now();
            Period period = Period.between(user.getBirthDate(), now);
            age = period.getYears();
        } else {
            // 如果没有出生日期，返回基于性别的默认值
            if ("男".equals(user.getGender()) || "male".equalsIgnoreCase(user.getGender())) {
                return UserNutritionTarget.builder()
                        .userId(userId)
                        .calories(2500)
                        .protein(BigDecimal.valueOf(80))
                        .carbohydrate(BigDecimal.valueOf(300))
                        .fat(BigDecimal.valueOf(80))
                        .fiber(BigDecimal.valueOf(35))
                        .sodium(BigDecimal.valueOf(2300))
                        .createTime(LocalDateTime.now())
                        .build();
            } else {
                return UserNutritionTarget.builder()
                        .userId(userId)
                        .calories(2000)
                        .protein(BigDecimal.valueOf(60))
                        .carbohydrate(BigDecimal.valueOf(250))
                        .fat(BigDecimal.valueOf(65))
                        .fiber(BigDecimal.valueOf(28))
                        .sodium(BigDecimal.valueOf(2300))
                        .createTime(LocalDateTime.now())
                        .build();
            }
        }
        
        // 设置默认的热量值
        int calories = 2000; // 默认热量
        
        // 蛋白质：每千克体重1.6-2.2克
        BigDecimal protein = BigDecimal.valueOf(weight * 1.8).setScale(2, RoundingMode.HALF_UP);
        
        // 脂肪：总热量的25-30%
        BigDecimal fat = BigDecimal.valueOf(calories * 0.25 / 9).setScale(2, RoundingMode.HALF_UP);
        
        // 碳水化合物：剩余热量
        BigDecimal proteinCalories = protein.multiply(BigDecimal.valueOf(4));
        BigDecimal fatCalories = fat.multiply(BigDecimal.valueOf(9));
        BigDecimal carbCalories = BigDecimal.valueOf(calories).subtract(proteinCalories).subtract(fatCalories);
        BigDecimal carbohydrate = carbCalories.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
        
        // 膳食纤维：每1000卡路里14克
        BigDecimal fiber = BigDecimal.valueOf(calories * 14.0 / 1000).setScale(2, RoundingMode.HALF_UP);
        
        // 钠：2300毫克
        BigDecimal sodium = BigDecimal.valueOf(2300).setScale(2, RoundingMode.HALF_UP);
        
        return UserNutritionTarget.builder()
                .userId(userId)
                .calories(calories)
                .protein(protein)
                .carbohydrate(carbohydrate)
                .fat(fat)
                .fiber(fiber)
                .sodium(sodium)
                .createTime(LocalDateTime.now())
                .build();
    }
}