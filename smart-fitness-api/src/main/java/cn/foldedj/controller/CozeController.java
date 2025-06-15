package cn.foldedj.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.foldedj.service.CozeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Coze工作流控制器
 */
@Api(tags = "Coze工作流接口")
@RestController
@RequestMapping("/coze")
@Slf4j
public class CozeController {

    @Autowired
    private CozeService cozeService;
    
    /**
     * 执行Coze工作流
     * 
     * @param requestMap 请求参数，包含input用户输入
     * @return 工作流执行结果
     */
    @ApiOperation("执行Coze工作流")
    @PostMapping("/workflow/execute")
    public Map<String, Object> executeWorkflow(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从环境变量获取工作流ID，严格按照示例代码
            String workflowId = System.getenv("WORKFLOW_ID");
            if (workflowId == null || workflowId.trim().isEmpty()) {
                // 如果环境变量中没有，则尝试使用COZE_WORKFLOW_ID
                workflowId = System.getenv("COZE_WORKFLOW_ID");
                if (workflowId == null || workflowId.trim().isEmpty()) {
                    // 如果仍然没有，则使用默认值
                    workflowId = "default-workflow-id";
                }
            }
            
            String input = (String) requestMap.get("input");
            
            if (input == null || input.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "输入内容不能为空");
                return result;
            }
            
            log.info("接收到工作流执行请求，使用工作流ID: {}, input: {}", workflowId, input);
            
            // 构建工作流参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("input", input);
            
            // 执行工作流
            String response = cozeService.executeWorkflow(workflowId, parameters);
            
            result.put("code", 200);
            result.put("data", response);
            result.put("message", "工作流执行成功");
        } catch (Exception e) {
            log.error("执行工作流出错: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("message", "工作流执行失败: " + e.getMessage());
        }
        
        return result;
    }
}