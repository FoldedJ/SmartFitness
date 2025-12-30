package cn.foldedj.service.impl;

import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.mapper.HealthModelConfigMapper;
import cn.foldedj.mapper.UserHealthMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.PageResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.HealthModelConfigQueryDto;
import cn.foldedj.pojo.dto.query.extend.UserHealthQueryDto;
import cn.foldedj.pojo.entity.HealthModelConfig;
import cn.foldedj.pojo.vo.HealthModelConfigVO;
import cn.foldedj.pojo.vo.UserHealthVO;
import cn.foldedj.service.HealthModelConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康模型业务逻辑实现
 */
@Service
public class HealthModelConfigServiceImpl implements HealthModelConfigService {

    @Resource
    private HealthModelConfigMapper healthModelConfigMapper;

    @Resource
    private UserHealthMapper userHealthMapper;

    /**
     * 健康模型新增
     *
     * @param healthModelConfig 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(HealthModelConfig healthModelConfig) {
        // 将用户的ID设置上
        healthModelConfig.setUserId(LocalThreadHolder.getUserId());
        healthModelConfigMapper.save(healthModelConfig);
        return ApiResult.success();
    }

    /**
     * 健康模型删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        // 先删除关联的用户健康记录
        for (Long id : ids) {
            UserHealthQueryDto userHealthQueryDto = new UserHealthQueryDto();
            userHealthQueryDto.setHealthModelConfigId(id.intValue());
            List<UserHealthVO> userHealths = userHealthMapper.query(userHealthQueryDto);
            if (userHealths != null && !userHealths.isEmpty()) {
                List<Long> userHealthIds = userHealths.stream()
                        .map(vo -> Long.valueOf(vo.getId()))
                        .collect(Collectors.toList());
                userHealthMapper.batchDelete(userHealthIds);
            }
        }
        healthModelConfigMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 健康模型修改
     *
     * @param healthModelConfig 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(HealthModelConfig healthModelConfig) {
        healthModelConfigMapper.update(healthModelConfig);
        return ApiResult.success();
    }

    /**
     * 查询用户自己配置的模型及全局模型
     *
     * @param healthModelConfigQueryDto 查询参数
     * @return Result<List < HealthModelConfigVO>>
     */
    @Override
    public Result<List<HealthModelConfigVO>> modelList() {
        HealthModelConfigQueryDto healthModelConfigQueryDto = new HealthModelConfigQueryDto();
        healthModelConfigQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<HealthModelConfigVO> modelConfigs = healthModelConfigMapper.query(healthModelConfigQueryDto);
        healthModelConfigQueryDto.setUserId(null);
        healthModelConfigQueryDto.setIsGlobal(true);
        List<HealthModelConfigVO> modelConfigsGlobal = healthModelConfigMapper.query(healthModelConfigQueryDto);
        List<HealthModelConfigVO> modelAll = new ArrayList<>();
        modelAll.addAll(modelConfigs);
        modelAll.addAll(modelConfigsGlobal);
        return ApiResult.success(modelAll);
    }

    /**
     * 健康模型查询
     *
     * @param healthModelConfigQueryDto 查询参数
     * @return Result<List < HealthModelConfigVO>>
     */
    @Override
    public Result<List<HealthModelConfigVO>> query(HealthModelConfigQueryDto healthModelConfigQueryDto) {
        List<HealthModelConfigVO> modelConfigs = healthModelConfigMapper.query(healthModelConfigQueryDto);
        Integer totalCount = healthModelConfigMapper.queryCount(healthModelConfigQueryDto);
        return PageResult.success(modelConfigs, totalCount);
    }

}
