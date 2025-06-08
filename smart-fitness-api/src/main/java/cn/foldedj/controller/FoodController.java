package cn.foldedj.controller;

import cn.foldedj.aop.Pager;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.FoodQueryDto;
import cn.foldedj.pojo.entity.Food;
import cn.foldedj.pojo.vo.FoodVO;
import cn.foldedj.service.FoodService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 食物管理的 Controller
 */
@RestController
@RequestMapping(value = "/food")
public class FoodController {

    @Resource
    private FoodService foodService;

    /**
     * 食物新增
     *
     * @param food 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Food food) {
        return foodService.save(food);
    }

    /**
     * 食物删除
     *
     * @param ids 要删除的食物ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return foodService.batchDelete(ids);
    }

    /**
     * 食物修改
     *
     * @param food 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Food food) {
        return foodService.update(food);
    }

    /**
     * 食物查询
     *
     * @param foodQueryDto 查询参数
     * @return Result<List<FoodVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<FoodVO>> query(@RequestBody FoodQueryDto foodQueryDto) {
        return foodService.query(foodQueryDto);
    }

    /**
     * 根据ID获取食物详情
     *
     * @param id 食物ID
     * @return Result<Food> 通用响应
     */
    @GetMapping(value = "/detail/{id}")
    public Result<Food> getById(@PathVariable Integer id) {
        Food food = foodService.getById(id);
        if (food != null) {
            return ApiResult.success(food);
        } else {
            return ApiResult.error("食物不存在");
        }
    }

}