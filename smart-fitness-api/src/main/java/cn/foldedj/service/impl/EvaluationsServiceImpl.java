package cn.foldedj.service.impl;

import cn.foldedj.context.LocalThreadHolder;
import cn.foldedj.mapper.EvaluationsMapper;
import cn.foldedj.mapper.UserMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.PageResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.foldedj.pojo.entity.Evaluations;
import cn.foldedj.pojo.entity.User;
import cn.foldedj.pojo.vo.CommentChildVO;
import cn.foldedj.pojo.vo.CommentParentVO;
import cn.foldedj.pojo.vo.EvaluationsVO;
import cn.foldedj.service.EvaluationsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现类
 */
@Service
public class EvaluationsServiceImpl implements EvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> insert(Evaluations evaluations) {
        // 将当前用户ID转换为Long类型
        evaluations.setCommenterId(Long.valueOf(LocalThreadHolder.getUserId()));
        User queryConditionEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        User user = userMapper.getByActive(queryConditionEntity);
        if (user.getIsWord()) {
            return ApiResult.error("账户已被禁言");
        }
        
        // 如果是回复评论且parentId不为空，且replierId为空时，自动设置replierId为被回复评论的commenterId
        if (evaluations.getParentId() != null && evaluations.getReplierId() == null) {
            // 查询被回复的评论
            Evaluations parentComment = evaluationsMapper.getById(evaluations.getParentId());
            if (parentComment != null) {
                // 检查父评论的commenterId是否为null
                if (parentComment.getCommenterId() != null) {
                    // 设置replierId为被回复评论的commenterId
                    evaluations.setReplierId(parentComment.getCommenterId());
                }
            }
        }
        
        // TODO 需要发通知！
        evaluations.setCreateTime(LocalDateTime.now());
        // 确保replierId字段被保留，不做任何修改
        evaluationsMapper.save(evaluations);
        return ApiResult.success("评论成功");
    }

    /**
     * 查询全部评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(contentId, contentType);
        setUpvoteFlag(parentComments);
        Integer count = evaluationsMapper.totalCount(contentId, contentType);
        return ApiResult.success(new EvaluationsVO(count, parentComments));
    }

    /**
     * 设置点赞状态
     *
     * @param parentComments 评论数据列表
     */
    private void setUpvoteFlag(List<CommentParentVO> parentComments) {
        String userId = LocalThreadHolder.getUserId().toString(); // 预先获取用户ID
        parentComments.forEach(parentComment -> {
            parentComment.setUpvoteFlag(isUserUpvote(parentComment.getUpvoteList(), userId));
            parentComment.setUpvoteCount(countVotes(parentComment.getUpvoteList()));
            Optional.ofNullable(parentComment.getCommentChildVOS())
                    .orElse(Collections.emptyList())
                    .forEach(child -> {
                        child.setUpvoteFlag(isUserUpvote(child.getUpvoteList(), userId));
                        child.setUpvoteCount(countVotes(child.getUpvoteList()));
                    });
        });
    }

    /**
     * 判断用户是否已点赞
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @param userId  用户ID
     * @return 是否已点赞
     */
    private boolean isUserUpvote(String voteStr, String userId) {
        return Optional.ofNullable(voteStr)
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList())
                .contains(userId);
    }

    /**
     * 计算点赞数
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @return 点赞数
     */
    private int countVotes(String voteStr) {
        return Optional.ofNullable(voteStr)
                .map(s -> s.split(",").length)
                .orElse(0);
    }

    /**
     * 分页查询评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> query(EvaluationsQueryDto evaluationsQueryDto) {
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsQueryDto);
        Integer totalPage = evaluationsMapper.queryCount(evaluationsQueryDto);
        return PageResult.success(list, totalPage);
    }

    /**
     * 批量删除评论数据
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 评论删除
     *
     * @return Result<String>
     */
    @Override
    public Result<String> delete(Integer id) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 评论修改
     *
     * @return Result<String>
     */
    @Override
    public Result<Void> update(Evaluations evaluations) {
        // TODO 点赞需要做通知
        evaluationsMapper.update(evaluations);
        return ApiResult.success();
    }
}
