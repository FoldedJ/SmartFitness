package cn.foldedj.controller;

import cn.foldedj.aop.Pager;
import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NewsQueryDto;
import cn.foldedj.pojo.entity.News;
import cn.foldedj.pojo.vo.NewsVO;
import cn.foldedj.service.NewsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 健康资讯的 Controller
 */
@RestController
@RequestMapping(value = "/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    /**
     * 健康资讯新增
     *
     * @param news 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody News news) {
        // 设置发布者ID为当前登录用户
        news.setPublisher(LocalThreadHolder.getUserId());
        return newsService.save(news);
    }

    /**
     * 健康资讯删除
     *
     * @param ids 要删除的健康资讯ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return newsService.batchDelete(ids);
    }

    /**
     * 健康资讯修改
     *
     * @param news 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody News news) {
        return newsService.update(news);
    }

    /**
     * 健康资讯查询
     *
     * @param NewsQueryDto 查询参数
     * @return Result<List < NewsVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<NewsVO>> query(@RequestBody NewsQueryDto NewsQueryDto) {
        return newsService.query(NewsQueryDto);
    }

    /**
     * 查询当前用户发布的帖子
     *
     * @return Result<List<NewsVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/queryMyNews")
    public Result<List<NewsVO>> queryMyNews() {
        NewsQueryDto newsQueryDto = new NewsQueryDto();
        newsQueryDto.setPublisher(LocalThreadHolder.getUserId());
        return newsService.query(newsQueryDto);
    }

    /**
     * 删除当前用户发布的帖子
     *
     * @param ids 要删除的帖子ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/deleteMyNews")
    public Result<Void> deleteMyNews(@RequestBody List<Integer> ids) {
        // 验证帖子是否属于当前用户
        for (Integer id : ids) {
            News news = newsService.getById(id);
            if (news == null || !news.getPublisher().equals(LocalThreadHolder.getUserId())) {
                return Result.error("无权删除该帖子");
            }
        }
        return newsService.batchDelete(ids);
    }

    /**
     * 增加资讯浏览次数
     *
     * @param news 包含ID的资讯对象
     * @return Result<Integer> 返回更新后的浏览次数
     */
    @PostMapping(value = "/increaseViews")
    public Result<Integer> increaseViews(@RequestBody News news) {
        return newsService.increaseViews(news.getId());
    }

}
