package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.UserFoodRecordQueryDto;
import cn.foldedj.pojo.entity.UserFoodRecord;
import cn.foldedj.pojo.vo.UserFoodRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户饮食记录Mapper接口
 */
@Mapper
public interface UserFoodRecordMapper {

    /**
     * 批量保存饮食记录
     *
     * @param records 饮食记录列表
     */
    void batchSave(@Param("records") List<UserFoodRecord> records);

    /**
     * 删除饮食记录
     *
     * @param id 记录ID
     */
    void delete(@Param("id") Integer id);

    /**
     * 查询饮食记录
     *
     * @param queryDto 查询条件
     * @return List<UserFoodRecordVO> 查询结果
     */
    List<UserFoodRecordVO> query(@Param("queryDto") UserFoodRecordQueryDto queryDto);

    /**
     * 查询饮食记录总数
     *
     * @param queryDto 查询条件
     * @return Integer 总数
     */
    Integer queryCount(@Param("queryDto") UserFoodRecordQueryDto queryDto);

    /**
     * 根据ID获取饮食记录
     *
     * @param id 记录ID
     * @return UserFoodRecord 饮食记录
     */
    UserFoodRecord getById(@Param("id") Integer id);
}