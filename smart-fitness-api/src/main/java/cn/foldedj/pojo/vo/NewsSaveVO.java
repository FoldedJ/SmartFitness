package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.NewsSave;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewsSaveVO extends NewsSave {

    /**
     * 用户名
     */
    private String userName;
    /**
     * 健康资讯封面
     */
    private String cover;
    /**
     * 健康资讯标题
     */
    private String name;
    /**
     * 健康资讯内容
     */
    private String content;
    /**
     * 标签
     */
    private String tagName;
    /**
     * 资讯发布时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime newsCreateTime;

    /**
     * 浏览次数
     */
    private Integer viewsNumber;

    /**
     * 资讯ID
     */
    private Integer newsId;
}
