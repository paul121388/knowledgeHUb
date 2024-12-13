package com.paul.knowledgeHub.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paul.knowledgeHub.common.BaseResponse;
import com.paul.knowledgeHub.common.ErrorCode;
import com.paul.knowledgeHub.common.ResultUtils;
import com.paul.knowledgeHub.model.dto.questionBank.QuestionBankQueryRequest;
import com.paul.knowledgeHub.model.entity.QuestionBank;
import com.paul.knowledgeHub.model.vo.QuestionBankVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public class blockHandler {

    /**
     * 流控操作
     * @param questionBankQueryRequest
     * @param request
     * @param ex
     * @return
     */
    public static BaseResponse<Page<QuestionBankVO>> handleBlockException(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request, BlockException ex) {

        if (ex instanceof DegradeException) {
            return handleFallback.blockFallback(questionBankQueryRequest, request, ex);
        }

        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后再试");
    }

}
