package cn.foldedj.pojo.dto.query.extend;

import cn.foldedj.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 食物查询参数DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FoodQueryDto extends QueryDto {

    /**
     * 食物名称
     */
    private String foodName;

    /**
     * 食物来源
     */
    private String source;

}