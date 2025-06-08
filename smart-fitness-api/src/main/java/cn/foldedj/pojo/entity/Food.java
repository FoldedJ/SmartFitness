package cn.foldedj.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Food {

    /**
     * 食物ID
     */
    private Integer id;

    /**
     * 食物名称
     */
    private String foodName;

    /**
     * 标准份量(克)
     */
    private BigDecimal servingSize;

    /**
     * 份量单位
     */
    private String servingUnit;

    /**
     * 卡路里(kcal)
     */
    private BigDecimal calories;

    /**
     * 蛋白质(g)
     */
    private BigDecimal protein;

    /**
     * 碳水化合物(g)
     */
    private BigDecimal carbohydrate;

    /**
     * 脂肪(g)
     */
    private BigDecimal fat;

    /**
     * 膳食纤维(g)
     */
    private BigDecimal fiber;

    /**
     * 钠(mg)
     */
    private BigDecimal sodium;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}