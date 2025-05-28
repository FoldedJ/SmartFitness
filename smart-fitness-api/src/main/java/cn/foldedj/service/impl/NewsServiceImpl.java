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
    public Result<Void> batchDelete(List<Integer> ids) {
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

    /**
     * 增加资讯浏览次数
     *
     * @param id 资讯ID
     * @return Result<Integer> 返回更新后的浏览次数
     */
    @Override
    public Result<Integer> increaseViews(Integer id) {
        newsMapper.increaseViews(id);
        NewsQueryDto queryDto = new NewsQueryDto();
        News news = newsMapper.getById(id);
        if (news != null) {
            queryDto.setName(news.getName());
            List<NewsVO> newsList = newsMapper.query(queryDto);
            if (newsList != null && !newsList.isEmpty()) {
                return ApiResult.success(newsList.get(0).getViewsNumber());
            }
        }
        return ApiResult.error("帖子不存在");
    }

    @Override
    public News getById(Integer id) {
        return newsMapper.getById(id);
    }
}
