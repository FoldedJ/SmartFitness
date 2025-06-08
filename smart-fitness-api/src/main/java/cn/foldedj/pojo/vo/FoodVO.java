package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.Food;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 食物视图对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FoodVO extends Food {

    // 目前继承Food实体类的所有字段即可
    // 如果后续需要额外的展示字段，可以在这里添加

}