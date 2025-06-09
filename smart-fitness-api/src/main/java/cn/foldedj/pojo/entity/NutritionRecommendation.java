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
 * 营养推荐实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NutritionRecommendation {

    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 推荐日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recommendationDate;

    /**
     * 推荐热量(kcal)
     */
    private Integer calories;

    /**
     * 推荐蛋白质(g)
     */
    private BigDecimal protein;

    /**
     * 推荐碳水化合物(g)
     */
    private BigDecimal carbohydrate;

    /**
     * 推荐脂肪(g)
     */
    private BigDecimal fat;

    /**
     * 推荐膳食纤维(g)
     */
    private BigDecimal fiber;

    /**
     * 推荐钠(mg)
     */
    private BigDecimal sodium;

    /**
     * 推荐类型：DAILY(每日), WEEKLY(每周), CUSTOM(自定义)
     */
    private String recommendationType;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}