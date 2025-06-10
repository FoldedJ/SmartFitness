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
import java.util.List;
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
import cn.foldedj.service.TencentHunyuanService;
import cn.foldedj.service.UserHealthService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    
    @Resource
    private TencentHunyuanService tencentHunyuanService;

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
        
        // 调用混元模型生成营养推荐
        log.info("调用混元模型生成营养推荐，用户ID: {}, 身高: {}, 体重: {}, 年龄: {}, 性别: {}", userId, height, weight, age, user.getGender());
        Result<String> modelResult = tencentHunyuanService.generateNutritionRecommendation(height, weight, age, user.getGender());
        
        log.info("混元模型返回结果对象: {}", modelResult);
        log.info("混元模型返回结果状态码: {}", modelResult.getCode());
        log.info("混元模型返回结果消息: {}", modelResult.getMsg());
        
        if (!modelResult.isSuccess()) {
            log.error("调用混元模型失败: {}", modelResult.getMsg());
            return ApiResult.error("生成营养推荐失败: " + modelResult.getMsg());
        }
        
        // 解析模型返回的纯文本结果
        String modelResponse = modelResult.getMsg();
        log.info("混元模型返回结果数据: {}", modelResponse);
        
        try {
            // 检查返回的字符串是否为空
            if (modelResponse == null || modelResponse.trim().isEmpty()) {
                log.error("混元模型返回的结果为空");
                return ApiResult.error("生成营养推荐失败: 模型返回结果为空");
            }
            
            log.info("原始响应内容: {}", modelResponse);
            
            // 解析纯文本格式的返回结果
            int calories = 2000; // 默认值
            BigDecimal protein = BigDecimal.valueOf(0);
            BigDecimal carbohydrate = BigDecimal.valueOf(0);
            BigDecimal fat = BigDecimal.valueOf(0);
            BigDecimal fiber = BigDecimal.valueOf(0);
            BigDecimal sodium = BigDecimal.valueOf(0);
            List<String> suggestions = new ArrayList<>();
            List<String> notes = new ArrayList<>();
            
            // 使用正则表达式提取数值
            Pattern caloriesPattern = Pattern.compile("热量：\\s*(\\d+)");
            Pattern proteinPattern = Pattern.compile("蛋白质：\\s*(\\d+(\\.\\d+)?)");
            Pattern carbohydratePattern = Pattern.compile("碳水化合物：\\s*(\\d+(\\.\\d+)?)");
            Pattern fatPattern = Pattern.compile("脂肪：\\s*(\\d+(\\.\\d+)?)");
            Pattern fiberPattern = Pattern.compile("膳食纤维：\\s*(\\d+(\\.\\d+)?)");
            Pattern sodiumPattern = Pattern.compile("钠：\\s*(\\d+(\\.\\d+)?)");
            
            // 提取营养数据
            try {
                Matcher caloriesMatcher = caloriesPattern.matcher(modelResponse);
                if (caloriesMatcher.find()) {
                    calories = Integer.parseInt(caloriesMatcher.group(1));
                    log.info("提取到热量: {}", calories);
                } else {
                    log.warn("未找到热量数据，使用默认值: {}", calories);
                }
                
                Matcher proteinMatcher = proteinPattern.matcher(modelResponse);
                if (proteinMatcher.find()) {
                    protein = new BigDecimal(proteinMatcher.group(1)).setScale(2, RoundingMode.HALF_UP);
                    log.info("提取到蛋白质: {}", protein);
                } else {
                    log.warn("未找到蛋白质数据，使用默认值: {}", protein);
                }
                
                Matcher carbohydrateMatcher = carbohydratePattern.matcher(modelResponse);
                if (carbohydrateMatcher.find()) {
                    carbohydrate = new BigDecimal(carbohydrateMatcher.group(1)).setScale(2, RoundingMode.HALF_UP);
                    log.info("提取到碳水化合物: {}", carbohydrate);
                } else {
                    log.warn("未找到碳水化合物数据，使用默认值: {}", carbohydrate);
                }
                
                Matcher fatMatcher = fatPattern.matcher(modelResponse);
                if (fatMatcher.find()) {
                    fat = new BigDecimal(fatMatcher.group(1)).setScale(2, RoundingMode.HALF_UP);
                    log.info("提取到脂肪: {}", fat);
                } else {
                    log.warn("未找到脂肪数据，使用默认值: {}", fat);
                }
                
                Matcher fiberMatcher = fiberPattern.matcher(modelResponse);
                if (fiberMatcher.find()) {
                    fiber = new BigDecimal(fiberMatcher.group(1)).setScale(2, RoundingMode.HALF_UP);
                    log.info("提取到膳食纤维: {}", fiber);
                } else {
                    log.warn("未找到膳食纤维数据，使用默认值: {}", fiber);
                }
                
                Matcher sodiumMatcher = sodiumPattern.matcher(modelResponse);
                if (sodiumMatcher.find()) {
                    sodium = new BigDecimal(sodiumMatcher.group(1)).setScale(2, RoundingMode.HALF_UP);
                    log.info("提取到钠: {}", sodium);
                } else {
                    log.warn("未找到钠数据，使用默认值: {}", sodium);
                }
            } catch (Exception e) {
                log.error("提取营养数据时出错", e);
                // 继续使用默认值
            }
            
            // 提取饮食建议
            Pattern suggestionPattern = Pattern.compile("饮食建议：\\s*\\n((?:(?:\\d+\\.\\s*.*\\n)+))");
            Matcher suggestionMatcher = suggestionPattern.matcher(modelResponse);
            if (suggestionMatcher.find()) {
                String suggestionsText = suggestionMatcher.group(1);
                String[] suggestionLines = suggestionsText.split("\\n");
                for (String line : suggestionLines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        // 去掉行首的数字和点
                        line = line.replaceAll("^\\d+\\.\\s*", "");
                        suggestions.add(line);
                    }
                }
                log.info("提取到{}条饮食建议", suggestions.size());
            } else {
                log.warn("未找到饮食建议");
            }
            
            // 提取注意事项
            Pattern notesPattern = Pattern.compile("注意事项：\\s*\\n((?:(?:\\d+\\.\\s*.*\\n?)+))");
            Matcher notesMatcher = notesPattern.matcher(modelResponse);
            if (notesMatcher.find()) {
                String notesText = notesMatcher.group(1);
                String[] noteLines = notesText.split("\\n");
                for (String line : noteLines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        // 去掉行首的数字和点
                        line = line.replaceAll("^\\d+\\.\\s*", "");
                        notes.add(line);
                    }
                }
                log.info("提取到{}条注意事项", notes.size());
            } else {
                log.warn("未找到注意事项");
            }
            
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
                    .notes("基于混元模型生成的营养推荐: " + String.join("; ", notes))
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
        } catch (Exception e) {
            log.error("解析混元模型返回结果失败", e);
            return ApiResult.error("解析营养推荐数据失败: " + e.getMessage());
        }
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