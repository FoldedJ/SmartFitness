package cn.foldedj.pojo.dto.query.extend;

import cn.foldedj.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 营养推荐查询DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NutritionRecommendationQueryDto extends QueryDto {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 推荐日期
     */
    private LocalDate recommendationDate;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 推荐类型
     */
    private String recommendationType;
}