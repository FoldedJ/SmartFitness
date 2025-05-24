package cn.foldedj.controller;

import cn.foldedj.aop.Pager;
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
        return newsService.save(news);
    }

    /**
     * 健康资讯删除
     *
     * @param ids 要删除的健康资讯ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
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

}
