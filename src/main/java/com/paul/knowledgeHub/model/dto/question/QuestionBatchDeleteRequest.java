package com.paul.knowledgeHub.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {


    /**
     * 标签列表
     */
    private List<Long> questionIdList;


    private static final long serialVersionUID = 1L;
}