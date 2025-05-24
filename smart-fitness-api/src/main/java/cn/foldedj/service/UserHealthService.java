package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.UserHealthQueryDto;
import cn.foldedj.pojo.entity.UserHealth;
import cn.foldedj.pojo.vo.ChartVO;
import cn.foldedj.pojo.vo.UserHealthVO;

import java.util.List;

/**
 * 用户健康记录业务逻辑接口
 */
public interface UserHealthService {

    Result<Void> save(List<UserHealth> userHealths);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(UserHealth userHealth);

    Result<List<UserHealthVO>> query(UserHealthQueryDto userHealthQueryDto);

    Result<List<ChartVO>> daysQuery(Integer day);
}
