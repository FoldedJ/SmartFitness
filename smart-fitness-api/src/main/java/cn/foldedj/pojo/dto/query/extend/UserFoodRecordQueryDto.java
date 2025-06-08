package cn.foldedj.pojo.dto.query.extend;

import cn.foldedj.pojo.dto.query.base.QueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户饮食记录查询DTO
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFoodRecordQueryDto extends QueryDto {

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
    private String recordDate;

    /**
     * 餐次类型(1:早餐 2:午餐 3:晚餐 4:加餐)
     */
    private Integer mealType;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}