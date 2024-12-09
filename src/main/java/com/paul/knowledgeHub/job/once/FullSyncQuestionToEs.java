package com.paul.knowledgeHub.job.once;

import cn.hutool.core.collection.CollUtil;
import com.paul.knowledgeHub.esdao.PostEsDao;
import com.paul.knowledgeHub.esdao.QuestionEsDao;
import com.paul.knowledgeHub.model.dto.post.PostEsDTO;
import com.paul.knowledgeHub.model.dto.question.QuestionEsDTO;
import com.paul.knowledgeHub.model.entity.Post;
import com.paul.knowledgeHub.model.entity.Question;
import com.paul.knowledgeHub.service.PostService;
import com.paul.knowledgeHub.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) {
        List<Question> questionList = questionService.list();
        if (CollUtil.isEmpty(questionList)) {
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream().map(QuestionEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
