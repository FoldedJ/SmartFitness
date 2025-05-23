package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.News;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewsVO extends News {

    /**
     * 标签名
     */
    private String tagName;

}
