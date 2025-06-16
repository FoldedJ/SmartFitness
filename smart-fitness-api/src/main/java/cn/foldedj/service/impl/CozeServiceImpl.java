package cn.foldedj.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.coze.openapi.client.workflows.run.RunWorkflowReq;
import com.coze.openapi.client.workflows.run.RunWorkflowResp;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.service.CozeAPI;

import cn.foldedj.config.CozeConfig;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.DietSaveDTO;
import cn.foldedj.pojo.dto.query.extend.FoodQueryDto;
import cn.foldedj.pojo.vo.FoodVO;
import cn.foldedj.service.CozeService;
import cn.foldedj.service.FoodService;
import cn.foldedj.service.UserFoodRecordService;
import lombok.extern.slf4j.Slf4j;

/**
 * Coze服务实现类
 */
@Slf4j
@Service
public class CozeServiceImpl implements CozeService {

    @Autowired
    private CozeConfig cozeConfig;
    
    @Autowired
    private FoodService foodService;
    
    @Autowired
    private UserFoodRecordService userFoodRecordService;
    
    /**
     * 执行Coze工作流
     * 
     * @param workflowId 工作流ID
     * @param parameters 工作流参数
     * @return 工作流执行结果
     */
    @Override
    public String executeWorkflow(String workflowId, Map<String, Object> parameters) {
        try {
            log.info("执行Coze工作流，workflowId: {}, 参数: {}", workflowId, parameters);
            
            // 从环境变量获取Token
            String token = System.getenv("COZE_API_TOKEN");
            if (token == null || token.isEmpty()) {
                // 如果环境变量中没有，则使用配置文件中的token
                token = cozeConfig.getToken();
            }
            
            // 创建Token认证
            TokenAuth authCli = new TokenAuth(token);
            
            // 从环境变量获取baseURL
            String baseURL = System.getenv("COZE_API_BASE");
            if (baseURL == null || baseURL.isEmpty()) {
                // 如果环境变量中没有，则使用配置文件中的baseUrl
                baseURL = cozeConfig.getBaseUrl();
            }
            
            // 初始化Coze客户端，严格按照示例代码设置
            CozeAPI coze = new CozeAPI.Builder()
                    .baseURL(baseURL)
                    .auth(authCli)
                    .readTimeout(10000) // 按照示例代码设置为10000毫秒
                    .build();
            
            // 构建工作流请求
            RunWorkflowReq req = RunWorkflowReq.builder()
                    .workflowID(workflowId)
                    .parameters(parameters)
                    .build();
            
            // 执行工作流
            RunWorkflowResp resp = coze.workflows().runs().create(req);
            log.info("工作流执行完成，响应: {}", resp);
            
            // 直接返回output字段的内容
            String result = "";
            if (resp != null && resp.getData() != null) {
                // 从响应中提取output字段的值
                Object output = resp.getData();
                log.info("提取的data对象: {}, 类型: {}", output, output.getClass().getName());
                
                if (output instanceof String) {
                    String outputStr = (String) output;
                    log.info("output是String类型，值为: {}", outputStr);
                    
                    // 尝试解析JSON字符串，提取output字段的值
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObj = JSON.parseObject(outputStr);
                        if (jsonObj != null && jsonObj.containsKey("output")) {
                            // 直接返回output字段的值
                            result = jsonObj.getString("output");
                            log.info("从JSON中提取的output值: {}", result);
                            
                            // 检查是否包含SQL前缀的食物记录
                            if (result.contains("SQL")) {
                                processFoodRecord(result);
                                // 修改返回值，不返回SQL语句
                                result = "我已经成功记录了您的饮食";
                            }
                        } else {
                            // 如果没有output字段，直接返回原始字符串
                            result = outputStr;
                            log.info("JSON中没有output字段，返回原始字符串: {}", result);
                        }
                    } catch (Exception e) {
                        // 如果解析失败，直接返回原始字符串
                        log.warn("解析JSON失败，直接返回原始字符串: {}", e.getMessage());
                        result = outputStr;
                        
                        // 检查是否包含SQL前缀的食物记录
                        if (result.contains("SQL")) {
                            processFoodRecord(result);
                            // 修改返回值，不返回SQL语句
                            result = "我已经成功记录了您的饮食";
                        }
                    }
                } else if (output instanceof Map) {
                    // 如果是Map类型，尝试获取output字段
                    Map<?, ?> dataMap = (Map<?, ?>) output;
                    log.info("output是Map类型，内容为: {}", dataMap);
                    
                    Object outputValue = dataMap.get("output");
                    if (outputValue != null) {
                        result = outputValue.toString();
                        log.info("从Map中提取的output值: {}", result);
                        
                        // 检查是否包含SQL前缀的食物记录
                        if (result.contains("SQL")) {
                            processFoodRecord(result);
                            // 修改返回值，不返回SQL语句
                            result = "我已经成功记录了您的饮食";
                        }
                    } else {
                        // 如果没有output字段，返回整个数据的JSON字符串
                        result = JSON.toJSONString(output);
                        log.info("Map中没有output字段，返回整个JSON: {}", result);
                    }
                } else {
                    // 其他情况返回整个数据的JSON字符串
                    result = JSON.toJSONString(output);
                    log.info("output是其他类型，转换为JSON: {}", result);
                }
            } else {
                // 如果响应为空，返回空字符串
                log.warn("响应为空或data为空");
                result = "";
            }
            
            log.info("最终返回给前端的结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("执行Coze工作流失败: {}", e.getMessage(), e);
            return "执行工作流失败: " + e.getMessage();
        }
    }
    
    /**
     * 处理食物记录
     * 格式："SQL食物名称，数量；SQL食物名称，数量"
     * 
     * @param content 包含食物记录的内容
     */
    private void processFoodRecord(String content) {
        try {
            log.info("开始处理食物记录: {}", content);
            
            // 解析食物记录
            List<Long> foodIds = new ArrayList<>();
            List<Integer> foodNums = new ArrayList<>();
            
            // 使用正则表达式匹配"SQL食物名称，数量"格式
            Pattern pattern = Pattern.compile("SQL([^，,]+)[，,](\\d+)");
            Matcher matcher = pattern.matcher(content);
            
            while (matcher.find()) {
                String foodName = matcher.group(1).trim();
                int quantity = Integer.parseInt(matcher.group(2).trim());
                
                log.info("解析到食物: {}, 数量: {}", foodName, quantity);
                
                // 查询食物ID
                FoodQueryDto queryDto = new FoodQueryDto();
                queryDto.setFoodName(foodName);
                queryDto.setCurrent(1);
                queryDto.setSize(1);
                Result<List<FoodVO>> queryResult = foodService.query(queryDto);
                
                if (queryResult.isSuccess() && queryResult.getData() != null && !queryResult.getData().isEmpty()) {
                    FoodVO food = queryResult.getData().get(0);
                    Long foodId = food.getId().longValue();
                    foodIds.add(foodId);
                    foodNums.add(quantity);
                    log.info("找到食物ID: {}", foodId);
                } else {
                    log.warn("未找到食物: {}, 跳过此记录", foodName);
                }
            }
            
            if (!foodIds.isEmpty()) {
                // 根据当前时间判断餐次类型
                Integer mealType = determineMealTypeByTime();
                log.info("当前餐次类型: {}", mealType);
                
                // 创建饮食记录DTO
                DietSaveDTO dietSaveDTO = DietSaveDTO.builder()
                        .foodIds(foodIds)
                        .foodNums(foodNums)
                        .recordDate(LocalDate.now())
                        .mealType(mealType)
                        .build();
                
                // 保存饮食记录
                cn.foldedj.pojo.api.Result<Void> saveResult = userFoodRecordService.batchSave(dietSaveDTO);
                if (saveResult.isSuccess()) {
                    log.info("饮食记录保存成功");
                } else {
                    log.error("饮食记录保存失败: {}", saveResult.getMsg());
                }
            } else {
                log.warn("没有找到有效的食物记录");
            }
        } catch (Exception e) {
            log.error("处理食物记录失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 根据当前时间判断餐次类型
     * 1:早餐(5:00-10:00) 2:午餐(10:00-15:00) 3:晚餐(15:00-22:00) 4:加餐(其他时间)
     * 
     * @return 餐次类型
     */
    private Integer determineMealTypeByTime() {
        LocalTime currentTime = LocalTime.now();
        
        if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.of(10, 0))) {
            return 1; // 早餐
        } else if (currentTime.isAfter(LocalTime.of(10, 0)) && currentTime.isBefore(LocalTime.of(15, 0))) {
            return 2; // 午餐
        } else if (currentTime.isAfter(LocalTime.of(15, 0)) && currentTime.isBefore(LocalTime.of(22, 0))) {
            return 3; // 晚餐
        } else {
            return 4; // 加餐
        }
    }
}