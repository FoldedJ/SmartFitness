package cn.foldedj.pojo.dto.query.extend;

import cn.foldedj.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserHealthQueryDto extends QueryDto {

    /**
     * 用户ID
     */
    private Integer userId;

    private String name;

    /**
     * 健康模型ID
     */
    private Integer healthModelConfigId;

}
