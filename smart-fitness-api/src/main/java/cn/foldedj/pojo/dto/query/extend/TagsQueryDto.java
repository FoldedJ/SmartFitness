package cn.foldedj.pojo.dto.query.extend;

import cn.foldedj.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagsQueryDto extends QueryDto {

    /**
     * 标签名
     */
    private String name;

}
