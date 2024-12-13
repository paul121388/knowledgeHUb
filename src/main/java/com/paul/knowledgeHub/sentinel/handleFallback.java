package com.paul.knowledgeHub.sentinel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paul.knowledgeHub.common.BaseResponse;
import com.paul.knowledgeHub.common.ResultUtils;
import com.paul.knowledgeHub.model.dto.questionBank.QuestionBankQueryRequest;
import com.paul.knowledgeHub.model.vo.QuestionBankVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public class handleFallback {
    /**
     * 限流
     * @param questionBankQueryRequest
     * @param request
     * @param ex
     * @return
     */
    public static BaseResponse<Page<QuestionBankVO>> blockFallback(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request, Throwable ex) {

        return ResultUtils.success(null);
    }
}
