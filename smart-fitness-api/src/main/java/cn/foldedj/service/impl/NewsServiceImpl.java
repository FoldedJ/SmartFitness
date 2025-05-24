package cn.foldedj.service.impl;

import cn.foldedj.mapper.NewsMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.PageResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NewsQueryDto;
import cn.foldedj.pojo.entity.News;
import cn.foldedj.pojo.vo.NewsVO;
import cn.foldedj.service.NewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康资讯业务逻辑实现
 */
@Service
public class NewsServiceImpl implements NewsService {

    @Resource
    private NewsMapper newsMapper;

    /**
     * 健康资讯新增
     *
     * @param news 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(News news) {
        news.setCreateTime(LocalDateTime.now());
        newsMapper.save(news);
        return ApiResult.success();
    }

    /**
     * 健康资讯删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        newsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 健康资讯修改
     *
     * @param news 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(News news) {
        newsMapper.update(news);
        return ApiResult.success();
    }

    /**
     * 健康资讯查询
     *
     * @param NewsQueryDto 查询参数
     * @return Result<List < NewsVO>>
     */
    @Override
    public Result<List<NewsVO>> query(NewsQueryDto NewsQueryDto) {
        List<NewsVO> NewsList = newsMapper.query(NewsQueryDto);
        Integer totalCount = newsMapper.queryCount(NewsQueryDto);
        return PageResult.success(NewsList, totalCount);
    }


}
