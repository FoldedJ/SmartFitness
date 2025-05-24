package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.UserHealthQueryDto;
import cn.foldedj.pojo.entity.UserHealth;
import cn.foldedj.pojo.vo.UserHealthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户健康记录持久化接口
 */
@Mapper
public interface UserHealthMapper {

    void batchSave(List<UserHealth> userHealths);

    void update(UserHealth userHealth);

    void batchDelete(@Param(value = "ids") List<Long> ids);

    List<UserHealthVO> query(UserHealthQueryDto userHealthQueryDto);

    Integer queryCount(UserHealthQueryDto userHealthQueryDto);

}
