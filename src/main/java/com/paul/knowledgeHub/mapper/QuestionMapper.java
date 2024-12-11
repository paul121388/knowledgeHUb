package com.paul.knowledgeHub.mapper;

import com.paul.knowledgeHub.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author 22653
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-12-07 14:52:25
* @Entity com.paul.knowledgeHub.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Question> listPostWithDelete(Date minUpdateTime);
}




