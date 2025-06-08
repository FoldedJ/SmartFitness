package cn.foldedj.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食记录保存DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DietSaveDTO {

    /**
     * 食物ID列表
     */
    private List<Long> foodIds;

    /**
     * 食物数量列表（对应foodIds的顺序）
     */
    private List<Integer> foodNums;

    /**
     * 记录日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    /**
     * 餐次类型(1:早餐 2:午餐 3:晚餐 4:加餐)
     */
    private Integer mealType;
}