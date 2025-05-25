package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.NewsQueryDto;
import cn.foldedj.pojo.entity.News;
import cn.foldedj.pojo.vo.NewsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签持久化接口
 */
@Mapper
public interface NewsMapper {

    void save(News news);

    void update(News news);

    void batchDelete(@Param(value = "ids") List<Long> ids);

    List<NewsVO> query(NewsQueryDto newsQueryDto);

    Integer queryCount(NewsQueryDto newsQueryDto);

    /**
     * 增加资讯浏览次数
     *
     * @param id 资讯ID
     */
    void increaseViews(@Param("id") Integer id);

    /**
     * 根据ID获取资讯
     *
     * @param id 资讯ID
     * @return News 资讯实体
     */
    News getById(@Param("id") Integer id);

}
