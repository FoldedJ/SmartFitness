package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.foldedj.pojo.entity.Evaluations;

import java.util.List;

/**
 * 评论服务接口
 */
public interface EvaluationsService {

    Result<Object> insert(Evaluations evaluations);

    Result<Object> list(Integer contentId, String contentType);

    Result<Object> query(EvaluationsQueryDto evaluationsQueryDto);

    Result<Object> batchDelete(List<Integer> ids);

    Result<String> delete(Integer id);

    Result<Void> update(Evaluations evaluations);

}
