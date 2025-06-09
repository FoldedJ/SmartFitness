package cn.foldedj.service.impl;

import cn.foldedj.config.TencentHunyuanConfig;
import cn.foldedj.mapper.UserNutritionTargetMapper;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TencentHunyuanServiceImpl implements TencentHunyuanService {

    @Resource
    private TencentHunyuanConfig hunyuanConfig;
    
    @Resource
    private UserNutritionTargetMapper userNutritionTargetMapper;

    @Override
    public Result<String> generateNutritionRecommendation(Double height, Double weight, Integer age, String gender) {
        try {
            // 实例化一个认证对象
            Credential cred = new Credential(hunyuanConfig.getSecretId(), hunyuanConfig.getSecretKey());

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(hunyuanConfig.getEndpoint());

            // 实例化一个client选项
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象
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
                "请以JSON格式返回，包含以下字段：\n" +
                "{\n" +
                "  \"calories\": 每日所需热量,\n" +
                "  \"protein\": 蛋白质推荐量(g),\n" +
                "  \"carbohydrate\": 碳水化合物推荐量(g),\n" +
                "  \"fat\": 脂肪推荐量(g),\n" +
                "  \"fiber\": 膳食纤维推荐量(g),\n" +
                "  \"sodium\": 钠推荐量(mg),\n" +
                "  \"suggestions\": [\"饮食建议1\", \"饮食建议2\", ...],\n" +
                "  \"notes\": [\"注意事项1\", \"注意事项2\", ...]\n" +
                "}",
                height, weight, age, gender
            );

            // 实例化一个请求对象
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            
            // 设置模型名称
            req.setModel("hunyuan-turbo");
            
            // 设置消息列表
            List<Message> messages = new ArrayList<>();
            Message message = new Message();
            message.setRole("user");
            message.setContent(prompt);
            messages.add(message);
            req.setMessages(messages.toArray(new Message[0]));

            // 发送请求
            ChatCompletionsResponse resp = client.ChatCompletions(req);
            
            // 打印响应内容以便调试
            System.out.println("API Response: " + resp.toString());
            System.out.println("Choices: " + resp.getChoices().length);
            if (resp.getChoices().length > 0) {
                System.out.println("First Choice: " + resp.getChoices()[0].toString());
            }
            
            // 返回结果
            return ApiResult.success(resp.getChoices()[0].getMessage().getContent());
        } catch (Exception e) {
            return ApiResult.error("生成营养推荐失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户最新的身高和体重数据
     * @param userId 用户ID
     * @return 包含身高和体重的数组，[0]为身高，[1]为体重
     */
    private Double[] getUserLatestHeightAndWeight(Integer userId) {
        Map<String, Object> heightAndWeight = userNutritionTargetMapper.getUserLatestHeightAndWeight(userId);
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