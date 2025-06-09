package cn.foldedj.mapper;

import cn.foldedj.pojo.entity.UserNutritionTarget;
import cn.foldedj.pojo.vo.UserNutritionTargetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户营养目标Mapper接口
 */
@Mapper
public interface UserNutritionTargetMapper {

    /**
     * 插入用户营养目标
     *
     * @param userNutritionTarget 用户营养目标
     * @return 影响行数
     */
    int insert(UserNutritionTarget userNutritionTarget);

    /**
     * 更新用户营养目标
     *
     * @param userNutritionTarget 用户营养目标
     * @return 影响行数
     */
    int update(UserNutritionTarget userNutritionTarget);

    /**
     * 批量删除用户营养目标
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 根据用户ID获取用户营养目标
     *
     * @param userId 用户ID
     * @return 用户营养目标
     */
    UserNutritionTarget getByUserId(@Param("userId") Integer userId);

    /**
     * 根据ID获取用户营养目标
     *
     * @param id ID
     * @return 用户营养目标
     */
    UserNutritionTargetVO getById(@Param("id") Integer id);

    /**
     * 查询用户最新的身高和体重数据
     *
     * @param userId 用户ID
     * @return 包含height和weight的Map
     */
    java.util.Map<String, Object> getUserLatestHeightAndWeight(@Param("userId") Integer userId);
}