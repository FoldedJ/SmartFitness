package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.UserNutritionTarget;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户营养目标VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserNutritionTargetVO extends UserNutritionTarget {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 用户年龄
     */
    private Integer age;

    /**
     * 用户身高(cm)
     */
    private Double height;

    /**
     * 用户体重(kg)
     */
    private Double weight;

    /**
     * 用户BMI
     */
    private Double bmi;
}