package cn.foldedj.controller;

import cn.foldedj.aop.Pager;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.DietSaveDTO;
import cn.foldedj.pojo.dto.query.extend.UserFoodRecordQueryDto;
import cn.foldedj.pojo.entity.UserFoodRecord;
import cn.foldedj.pojo.vo.UserFoodRecordVO;
import cn.foldedj.service.UserFoodRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 饮食记录管理的 Controller
 */
@RestController
@RequestMapping(value = "/diet")
public class DietController {

    @Resource
    private UserFoodRecordService userFoodRecordService;

    /**
     * 饮食记录保存
     *
     * @param dietSaveDTO 保存数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody DietSaveDTO dietSaveDTO) {
        return userFoodRecordService.batchSave(dietSaveDTO);
    }

    /**
     * 饮食记录删除
     *
     * @param id 要删除的记录ID
     * @return Result<Void> 通用响应体
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        return userFoodRecordService.delete(id);
    }

    /**
     * 饮食记录查询
     *
     * @param queryDto 查询参数
     * @return Result<List<UserFoodRecordVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<UserFoodRecordVO>> query(@RequestBody UserFoodRecordQueryDto queryDto) {
        return userFoodRecordService.query(queryDto);
    }

    /**
     * 获取用户饮食记录
     *
     * @param userId 用户ID
     * @param recordDate 记录日期 (格式: yyyy-MM-dd)
     * @return Result<List<UserFoodRecordVO>> 通用响应
     */
    @GetMapping(value = "/records")
    public Result<List<UserFoodRecordVO>> getRecords(
            @RequestParam Long userId,
            @RequestParam String recordDate) {
        return userFoodRecordService.getRecordsByUserIdAndDate(userId, recordDate);
    }

    /**
     * 根据ID获取饮食记录详情
     *
     * @param id 记录ID
     * @return Result<UserFoodRecord> 通用响应
     */
    @GetMapping(value = "/detail/{id}")
    public Result<UserFoodRecord> getById(@PathVariable Integer id) {
        UserFoodRecord record = userFoodRecordService.getById(id);
        if (record != null) {
            return ApiResult.success(record);
        } else {
            return ApiResult.error("饮食记录不存在");
        }
    }
}