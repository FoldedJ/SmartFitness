package cn.foldedj.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日营养汇总实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDailyNutrition {

    /**
     * 汇总ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    /**
     * 总卡路里
     */
    private BigDecimal totalCalories;

    /**
     * 总蛋白质
     */
    private BigDecimal totalProtein;

    /**
     * 总碳水化合物
     */
    private BigDecimal totalCarbohydrate;

    /**
     * 总脂肪
     */
    private BigDecimal totalFat;

    /**
     * 目标卡路里
     */
    private BigDecimal targetCalories;

    /**
     * 目标蛋白质
     */
    private BigDecimal targetProtein;

    /**
     * 目标碳水化合物
     */
    private BigDecimal targetCarbohydrate;

    /**
     * 目标脂肪
     */
    private BigDecimal targetFat;

    /**
     * 卡路里完成度(%)
     */
    private BigDecimal caloriesCompletionRate;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}