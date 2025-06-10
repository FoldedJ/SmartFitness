import request from '@/utils/request';



// 为当前用户生成营养推荐
export function generateNutritionRecommendation() {
  return request({
    url: '/nutrition/recommendation/generate',
    method: 'get'
  });
}

// 为指定用户生成营养推荐
export function generateUserNutritionRecommendation(userId) {
  return request({
    url: `/nutrition/recommendation/generate/${userId}`,
    method: 'get'
  });
}

// 保存当前用户的营养推荐到历史记录
export function saveNutritionRecommendation(data) {
  return request({
    url: '/nutrition/recommendation/save',
    method: 'post',
    data
  });
}

// 保存指定用户的营养推荐到历史记录
export function saveUserNutritionRecommendation(userId, data) {
  // 确保data中包含userId
  const recommendationData = { ...data, userId };
  return request({
    url: '/nutrition/recommendation/save',
    method: 'post',
    data: recommendationData
  });
}

// 查询当前用户的营养推荐历史
export function queryNutritionRecommendations(params) {
  return request({
    url: '/nutrition/recommendation/query',
    method: 'post',
    data: params
  });
}

// 查询指定用户的营养推荐历史
export function queryUserNutritionRecommendations(userId, params) {
  return request({
    url: `/nutrition/recommendation/${userId}/query`,
    method: 'post',
    data: params
  });
}

// 获取当前用户的营养推荐详情
export function getNutritionRecommendationById(id) {
  return request({
    url: `/nutrition/recommendation/${id}`,
    method: 'get'
  });
}

// 删除当前用户的营养推荐记录
export function deleteNutritionRecommendation(id) {
  return request({
    url: `/nutrition/recommendation/${id}`,
    method: 'delete'
  });
}

// 删除指定用户的营养推荐记录
export function deleteUserNutritionRecommendation(userId, id) {
  return request({
    url: `/nutrition/recommendation/${userId}/${id}`,
    method: 'delete'
  });
}

// 将最新的营养推荐设置为当前营养目标
export function setLatestRecommendationAsTarget() {
  return request({
    url: '/nutrition/recommendation/set-as-target',
    method: 'get'
  });
}

// 获取当前用户最新的营养推荐
export function getLatestNutritionRecommendation() {
  return request({
    url: '/nutrition/recommendation/latest',
    method: 'get'
  });
}