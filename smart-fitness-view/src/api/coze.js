import request from '@/utils/request'

/**
 * 执行Coze工作流
 * @param {String} input 用户输入内容
 * @returns {Promise} 返回工作流执行结果
 */
export function executeWorkflow(input) {
  return request({
    url: '/coze/workflow/execute',
    method: 'post',
    data: {
      input
    }
  })
}