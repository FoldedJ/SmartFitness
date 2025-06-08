package cn.foldedj.pojo.dto.query.base;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 查询参数接收实体类基类，含有四项基础参数，使用时可以拓展
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
public class QueryDto {
    /**
     * 当前页
     */
    private Integer current;
    /**
     * 页面数据大小
     */
    private Integer size;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 获取分页偏移量
     * @return 偏移量
     */
    public Integer getOffset() {
        if (current != null && size != null && current > 0) {
            return (current - 1) * size;
        }
        return 0;
    }
}

