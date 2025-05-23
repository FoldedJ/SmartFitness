package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.update.UserLoginDTO;
import cn.foldedj.pojo.dto.update.UserRegisterDTO;
import cn.foldedj.pojo.dto.update.UserUpdateDTO;
import cn.foldedj.pojo.dto.query.extend.UserQueryDto;
import cn.foldedj.pojo.entity.User;
import cn.foldedj.pojo.vo.ChartVO;
import cn.foldedj.pojo.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 */
public interface UserService {
    Result<String> register(UserRegisterDTO userRegisterDTO);

    Result<Object> login(UserLoginDTO userLoginDTO);

    Result<UserVO> auth();

    Result<List<User>> query(UserQueryDto userQueryDto);

    Result<String> update(UserUpdateDTO userUpdateDTO);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> updatePwd(Map<String, String> map);

    Result<UserVO> getById(Integer id);

    Result<String> insert(UserRegisterDTO userRegisterDTO);

    Result<String> backUpdate(User user);

    Result<List<ChartVO>> daysQuery(Integer day);

}
