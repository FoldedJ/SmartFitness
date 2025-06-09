package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;

public interface TencentHunyuanService {
    /**
     * 生成营养推荐
     *
     * @param height 身高(cm)
     * @param weight 体重(kg)
     * @param age 年龄
     * @param gender 性别
     * @return 营养推荐结果
     */
    Result<String> generateNutritionRecommendation(Double height, Double weight, Integer age, String gender);
} 