package cn.foldedj.mapper;

import cn.foldedj.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.foldedj.pojo.entity.Evaluations;
import cn.foldedj.pojo.vo.CommentChildVO;
import cn.foldedj.pojo.vo.CommentParentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论持久化接口
 */
public interface EvaluationsMapper {

    /**
     * 查询指定内容下的全部评论
     *
     * @param contentId   内容ID
     * @param contentType 内容类型
     * @return List<CommentParentVO>
     */
    List<CommentParentVO> getParentComments(@Param(value = "contentId") Integer contentId,
                                            @Param(value = "contentType") String contentType);

    /**
     * 分页查询评论
     *
     * @param evaluationsQueryDto 参数
     * @return List<CommentParentVO>
     */
    List<CommentChildVO> query(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * 分页查询评论总数
     *
     * @param evaluationsQueryDto 参数
     * @return List<CommentParentVO>
     */
    Integer queryCount(EvaluationsQueryDto evaluationsQueryDto);
    
    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return Evaluations
     */
    Evaluations getById(@Param(value = "id") Integer id);

    /**
     * 查询全部二级评论
     *
     * @param ids ID列表
     * @return List<Integer>
     */
    List<Integer> selectChildComments(@Param(value = "ids") List<Integer> ids);

    /**
     * 批量删除
     *
     * @param ids ID列表
     */
    void batchDelete(@Param(value = "ids") List<Integer> ids);

    /**
     * 根据内容ID列表和内容类型批量删除评论
     *
     * @param ids         内容ID列表
     * @param contentType 内容类型
     */
    void deleteByContentIds(@Param(value = "ids") List<Integer> ids, @Param(value = "contentType") String contentType);

    /**
     * 查询指定评论的数目
     *
     * @param contentId   内容ID
     * @param contentType 内容类型
     * @return Integer
     */
    Integer totalCount(Integer contentId, String contentType);

    /**
     * 评论新增
     *
     * @param evaluations 评论信息实体
     */
    void save(Evaluations evaluations);

    /**
     * 评论修改
     *
     * @param evaluations 评论实体
     */
    void update(Evaluations evaluations);

}
