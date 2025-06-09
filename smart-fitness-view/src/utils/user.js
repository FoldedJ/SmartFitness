/**
 * 用户相关工具函数
 */

/**
 * 从sessionStorage获取用户信息
 * @returns {Object} 用户信息对象
 */
export function getUserInfo() {
  const userInfo = sessionStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
}

/**
 * 从sessionStorage获取用户ID
 * @returns {Number} 用户ID
 */
export function getUserId() {
  const userInfo = getUserInfo();
  return userInfo ? userInfo.id : null;
}

/**
 * 检查用户是否已登录
 * @returns {Boolean} 是否已登录
 */
export function isUserLoggedIn() {
  return !!getUserInfo();
}