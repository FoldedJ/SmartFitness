package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.DietSaveDTO;
import cn.foldedj.pojo.dto.query.extend.UserFoodRecordQueryDto;
import cn.foldedj.pojo.entity.UserFoodRecord;
import cn.foldedj.pojo.vo.UserFoodRecordVO;

import java.util.List;

/**
 * 用户饮食记录服务接口
 */
public interface UserFoodRecordService {

    /**
     * 批量保存饮食记录
     *
     * @param dietSaveDTO 饮食记录保存DTO
     * @return Result<Void> 保存结果
     */
    Result<Void> batchSave(DietSaveDTO dietSaveDTO);

    /**
     * 删除饮食记录
     *
     * @param id 记录ID
     * @return Result<Void> 删除结果
     */
    Result<Void> delete(Integer id);

    /**
     * 查询饮食记录
     *
     * @param queryDto 查询条件
     * @return Result<List<UserFoodRecordVO>> 查询结果
     */
    Result<List<UserFoodRecordVO>> query(UserFoodRecordQueryDto queryDto);

    /**
     * 根据用户ID和日期获取饮食记录
     *
     * @param userId 用户ID
     * @param recordDate 记录日期
     * @return Result<List<UserFoodRecordVO>> 查询结果
     */
    Result<List<UserFoodRecordVO>> getRecordsByUserIdAndDate(Long userId, String recordDate);

    /**
     * 根据ID获取饮食记录
     *
     * @param id 记录ID
     * @return UserFoodRecord 饮食记录
     */
    UserFoodRecord getById(Integer id);
}