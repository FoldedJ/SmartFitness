package cn.foldedj.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户营养目标实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNutritionTarget {

    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 热量目标(kcal)
     */
    private Integer calories;

    /**
     * 蛋白质目标(g)
     */
    private BigDecimal protein;

    /**
     * 碳水化合物目标(g)
     */
    private BigDecimal carbohydrate;

    /**
     * 脂肪目标(g)
     */
    private BigDecimal fat;

    /**
     * 膳食纤维目标(g)
     */
    private BigDecimal fiber;

    /**
     * 钠目标(mg)
     */
    private BigDecimal sodium;

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