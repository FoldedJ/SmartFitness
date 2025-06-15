package cn.foldedj.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.coze.openapi.client.workflows.run.RunWorkflowReq;
import com.coze.openapi.client.workflows.run.RunWorkflowResp;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.service.CozeAPI;

import cn.foldedj.config.CozeConfig;
import cn.foldedj.service.CozeService;
import lombok.extern.slf4j.Slf4j;

/**
 * Coze服务实现类
 */
@Slf4j
@Service
public class CozeServiceImpl implements CozeService {

    @Autowired
    private CozeConfig cozeConfig;
    
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
                        } else {
                            // 如果没有output字段，直接返回原始字符串
                            result = outputStr;
                            log.info("JSON中没有output字段，返回原始字符串: {}", result);
                        }
                    } catch (Exception e) {
                        // 如果解析失败，直接返回原始字符串
                        log.warn("解析JSON失败，直接返回原始字符串: {}", e.getMessage());
                        result = outputStr;
                    }
                } else if (output instanceof Map) {
                    // 如果是Map类型，尝试获取output字段
                    Map<?, ?> dataMap = (Map<?, ?>) output;
                    log.info("output是Map类型，内容为: {}", dataMap);
                    
                    Object outputValue = dataMap.get("output");
                    if (outputValue != null) {
                        result = outputValue.toString();
                        log.info("从Map中提取的output值: {}", result);
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
}