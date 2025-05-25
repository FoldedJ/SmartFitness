package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NewsQueryDto;
import cn.foldedj.pojo.entity.News;
import cn.foldedj.pojo.vo.NewsVO;

import java.util.List;

/**
 * 健康资讯业务逻辑接口
 */
public interface NewsService {

    Result<Void> save(News news);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(News news);

    Result<List<NewsVO>> query(NewsQueryDto newsQueryDto);

    /**
     * 增加资讯浏览次数
     *
     * @param id 资讯ID
     * @return Result<Integer> 返回更新后的浏览次数
     */
    Result<Integer> increaseViews(Integer id);

}
