package cn.foldedj.pojo.vo;

import cn.foldedj.pojo.entity.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息出参VO类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageVO extends Message {

    /**
     * 发送者名称
     */
    private String senderName;
    /**
     * 接收者头像
     */
    private String receiverAvatar;
    /**
     * 接收者名称
     */
    private String receiverName;
    /**
     * 评论内容
     */
    private String evaluationsContent;

}
