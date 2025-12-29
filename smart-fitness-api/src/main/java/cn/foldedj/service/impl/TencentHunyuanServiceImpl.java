package cn.foldedj.service.impl;

import cn.foldedj.config.TencentHunyuanConfig;

import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.service.TencentHunyuanService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TencentHunyuanServiceImpl implements TencentHunyuanService {

    @Resource
    private TencentHunyuanConfig hunyuanConfig;
    


    @Override
    public Result<String> generateNutritionRecommendation(Double height, Double weight, Integer age, String gender) {
        log.info("开始调用腾讯混元模型API，参数：身高={}, 体重={}, 年龄={}, 性别={}", height, weight, age, gender);
        
        // 参数验证
        if (height == null || weight == null || age == null || gender == null) {
            log.error("调用腾讯混元模型API失败：参数不完整，身高={}, 体重={}, 年龄={}, 性别={}", height, weight, age, gender);
            return ApiResult.error("生成营养推荐失败：参数不完整");
        }
        
        try {
            // 检查配置是否完整
            if (hunyuanConfig.getSecretId() == null || hunyuanConfig.getSecretKey() == null || 
                hunyuanConfig.getEndpoint() == null || hunyuanConfig.getRegion() == null) {
                log.error("腾讯混元模型配置不完整，SecretId={}, Endpoint={}, Region={}", 
                        hunyuanConfig.getSecretId() != null ? "已配置" : "未配置", 
                        hunyuanConfig.getEndpoint(), 
                        hunyuanConfig.getRegion());
                return ApiResult.error("生成营养推荐失败：API配置不完整");
            }
            
            // 实例化一个认证对象
            log.info("初始化腾讯云认证对象，SecretId={}, Endpoint={}, Region={}", 
                    hunyuanConfig.getSecretId().substring(0, Math.min(4, hunyuanConfig.getSecretId().length())) + "****", 
                    hunyuanConfig.getEndpoint(), 
                    hunyuanConfig.getRegion());
            Credential cred = new Credential(hunyuanConfig.getSecretId(), hunyuanConfig.getSecretKey());

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(hunyuanConfig.getEndpoint());
            log.info("设置HTTP端点: {}", hunyuanConfig.getEndpoint());

            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象
            log.info("创建HunyuanClient对象，区域: {}", hunyuanConfig.getRegion());
            HunyuanClient client = new HunyuanClient(cred, hunyuanConfig.getRegion(), clientProfile);

            // 构建提示词
            String prompt = String.format(
                "请根据以下信息生成一份详细的营养建议：\n" +
                "身高：%.1f cm\n" +
                "体重：%.1f kg\n" +
                "年龄：%d 岁\n" +
                "性别：%s\n\n" +
                "请提供以下方面的建议：\n" +
                "1. 每日所需热量\n" +
                "2. 蛋白质、碳水化合物、脂肪的推荐摄入量\n" +
                "3. 维生素和矿物质的建议\n" +
                "4. 饮食建议\n" +
                "5. 注意事项\n\n" +
                "请严格以纯文本格式返回，不要返回其他格式，使用以下格式,对于热量蛋白质等给出精确值，不要给出范围：\n" +
                "热量：[每日所需热量]卡路里\n" +
                "蛋白质：[蛋白质推荐量]克\n" +
                "碳水化合物：[碳水化合物推荐量]克\n" +
                "脂肪：[脂肪推荐量]克\n" +
                "膳食纤维：[膳食纤维推荐量]克\n" +
                "钠：[钠推荐量]毫克\n\n" +
                "饮食建议：\n1. [建议1]\n2. [建议2]\n3. [建议3]\n\n" +
                "注意事项：\n1. [注意事项1]\n2. [注意事项2]",
                height, weight, age, gender
            );
            log.info("构建提示词完成，提示词长度: {}", prompt.length());
            log.debug("提示词内容: {}", prompt);

            // 实例化一个请求对象
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            
            // 设置模型名称
            req.setModel("hunyuan-turbo");
            log.info("设置模型名称: hunyuan-turbo");
            
            // 设置温度参数，控制生成文本的随机性
            req.setTemperature(0.7f);
            log.info("设置温度参数: 0.7");
            
            // 设置最大生成token数
            // req.setMaxTokens(2048);
            log.info("设置最大生成token数: 2048");
            
            // 设置消息列表
            List<Message> messages = new ArrayList<>();
            Message message = new Message();
            message.setRole("user");
            message.setContent(prompt);
            messages.add(message);
            req.setMessages(messages.toArray(new Message[0]));
            log.info("设置消息列表完成，消息数量: {}", messages.size());

            // 发送请求
            log.info("开始发送API请求...");
            long startTime = System.currentTimeMillis();
            ChatCompletionsResponse resp;
            try {
                resp = client.ChatCompletions(req);
                long endTime = System.currentTimeMillis();
                log.info("API请求已完成，耗时: {}ms", (endTime - startTime));
            } catch (Exception e) {
                log.error("API请求发送失败", e);
                return ApiResult.error("生成营养推荐失败：API请求发送失败 - " + e.getMessage());
            }
            
            // 打印响应内容以便调试
            if (resp == null) {
                log.error("API响应为空");
                return ApiResult.error("生成营养推荐失败：API响应为空");
            }
            
            log.info("API响应状态: {}", resp.getRequestId() != null ? "成功" : "异常");
            log.info("请求ID: {}", resp.getRequestId());
            
            if (resp.getChoices() == null) {
                log.error("API响应中choices为空");
                return ApiResult.error("生成营养推荐失败：API响应中choices为空");
            }
            
            log.info("选项数量: {}", resp.getChoices().length);
            
            if (resp.getChoices().length > 0) {
                if (resp.getChoices()[0] == null) {
                    log.error("API响应中第一个选项为空");
                    return ApiResult.error("生成营养推荐失败：API响应中第一个选项为空");
                }
                
                if (resp.getChoices()[0].getMessage() == null) {
                    log.error("API响应中第一个选项的消息为空");
                    return ApiResult.error("生成营养推荐失败：API响应中第一个选项的消息为空");
                }
                
                String content = resp.getChoices()[0].getMessage().getContent();
                if (content == null || content.trim().isEmpty()) {
                    log.error("API响应中第一个选项的内容为空");
                    return ApiResult.error("生成营养推荐失败：API响应中第一个选项的内容为空");
                }
                
                log.info("成功获取到模型响应内容，长度: {}", content.length());
                log.info("模型响应内容: {}", content);
                return ApiResult.success(content);
            } else {
                log.error("API响应中没有选项");
                return ApiResult.error("生成营养推荐失败：API响应中没有选项");
            }
        } catch (Exception e) {
            log.error("调用腾讯混元模型API失败", e);
            return ApiResult.error("生成营养推荐失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户最新的身高和体重数据
     * @param userId 用户ID
     * @return 包含身高和体重的数组，[0]为身高，[1]为体重
     */
    private Double[] getUserLatestHeightAndWeight(Integer userId) {
        Map<String, Object> heightAndWeight = null;
        log.info("TencentHunyuanServiceImpl - 用户ID: {} 的身高体重数据: {}", userId, heightAndWeight);
        
        Double height = null;
        Double weight = null;
        
        if (heightAndWeight != null) {
            log.info("TencentHunyuanServiceImpl - 身高数据: {}, 体重数据: {}", heightAndWeight.get("HEIGHT"), heightAndWeight.get("WEIGHT"));
            if (heightAndWeight.get("HEIGHT") != null) {
                String heightStr = String.valueOf(heightAndWeight.get("HEIGHT"));
                if (heightStr != null && !heightStr.isEmpty()) {
                    try {
                        height = Double.parseDouble(heightStr);
                        log.info("TencentHunyuanServiceImpl - 解析后身高: {}", height);
                    } catch (NumberFormatException e) {
                        log.error("TencentHunyuanServiceImpl - 解析身高数据失败: {}", heightStr, e);
                    }
                }
            }
            if (heightAndWeight.get("WEIGHT") != null) {
                String weightStr = String.valueOf(heightAndWeight.get("WEIGHT"));
                if (weightStr != null && !weightStr.isEmpty()) {
                    try {
                        weight = Double.parseDouble(weightStr);
                        log.info("TencentHunyuanServiceImpl - 解析后体重: {}", weight);
                    } catch (NumberFormatException e) {
                        log.error("TencentHunyuanServiceImpl - 解析体重数据失败: {}", weightStr, e);
                    }
                }
            }
        }
        
        log.info("TencentHunyuanServiceImpl - 返回身高体重数组: [{}, {}]", height, weight);
        return new Double[]{height, weight};
    }
}