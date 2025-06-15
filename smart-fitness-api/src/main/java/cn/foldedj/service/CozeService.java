package cn.foldedj.service;

import java.util.Map;

/**
 * Coze服务接口，提供工作流执行相关方法
 */
public interface CozeService {
    
    /**
     * 执行Coze工作流
     * 
     * @param workflowId 工作流ID
     * @param parameters 工作流参数
     * @return 工作流执行结果
     */
    String executeWorkflow(String workflowId, Map<String, Object> parameters);
}