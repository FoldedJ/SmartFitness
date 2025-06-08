package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.Food;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户饮食记录视图对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFoodRecordVO {

    /**
     * 记录ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 食物ID
     */
    private Integer foodId;

    /**
     * 记录日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    /**
     * 餐次类型(1:早餐 2:午餐 3:晚餐 4:加餐)
     */
    private Integer mealType;

    /**
     * 食用量(克)
     */
    private BigDecimal consumeAmount;

    /**
     * 实际摄入热量(kcal)
     */
    private BigDecimal actualCalories;

    /**
     * 实际摄入蛋白质(g)
     */
    private BigDecimal actualProtein;

    /**
     * 实际摄入碳水化合物(g)
     */
    private BigDecimal actualCarbohydrate;

    /**
     * 实际摄入脂肪(g)
     */
    private BigDecimal actualFat;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 食物信息
     */
    private Food food;
}