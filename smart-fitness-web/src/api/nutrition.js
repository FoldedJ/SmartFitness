import request from '@/utils/request';

/**
 * 获取当前用户的营养目标
 * @returns {Promise<any>}
 */
export function getNutritionTarget() {
  return request({
    url: '/nutrition/target',
    method: 'get'
  });
}

/**
 * 获取指定用户的营养目标
 * @param {number} userId 用户ID
 * @returns {Promise<any>}
 */
export function getNutritionTargetByUserId(userId) {
  return request({
    url: `/nutrition/target/${userId}`,
    method: 'get'
  });
}

/**
 * 保存或更新用户营养目标
 * @param {Object} data 用户营养目标数据
 * @returns {Promise<any>}
 */
export function saveNutritionTarget(data) {
  return request({
    url: '/nutrition/target/save',
    method: 'post',
    data
  });
}

/**
 * 为当前用户生成营养推荐
 * @returns {Promise<any>}
 */
export function generateNutritionRecommendation() {
  return request({
    url: '/nutrition/recommendation/generate',
    method: 'get'
  });
}

/**
 * 为指定用户生成营养推荐
 * @param {number} userId 用户ID
 * @returns {Promise<any>}
 */
export function generateNutritionRecommendationForUser(userId) {
  return request({
    url: `/nutrition/recommendation/generate/${userId}`,
    method: 'get'
  });
}

/**
 * 保存营养推荐
 * @param {Object} data 营养推荐数据
 * @returns {Promise<any>}
 */
export function saveNutritionRecommendation(data) {
  return request({
    url: '/nutrition/recommendation/save',
    method: 'post',
    data
  });
}

/**
 * 查询营养推荐
 * @param {Object} data 查询条件
 * @returns {Promise<any>}
 */
export function queryNutritionRecommendations(data) {
  return request({
    url: '/nutrition/recommendation/query',
    method: 'post',
    data
  });
}



/**
 * 根据ID获取营养推荐
 * @param {number} id 营养推荐ID
 * @returns {Promise<any>}
 */
export function getNutritionRecommendationById(id) {
  return request({
    url: `/nutrition/recommendation/${id}`,
    method: 'get'
  });
}

/**
 * 删除营养推荐
 * @param {Array<number>} ids 营养推荐ID列表
 * @returns {Promise<any>}
 */
export function deleteNutritionRecommendations(ids) {
  return request({
    url: '/nutrition/recommendation/delete',
    method: 'post',
    data: ids
  });
}