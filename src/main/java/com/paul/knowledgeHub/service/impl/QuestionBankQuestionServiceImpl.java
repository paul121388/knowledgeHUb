package com.paul.knowledgeHub.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paul.knowledgeHub.common.ErrorCode;
import com.paul.knowledgeHub.constant.CommonConstant;
import com.paul.knowledgeHub.exception.BusinessException;
import com.paul.knowledgeHub.exception.ThrowUtils;
import com.paul.knowledgeHub.mapper.QuestionBankQuestionMapper;
import com.paul.knowledgeHub.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.paul.knowledgeHub.model.entity.Question;
import com.paul.knowledgeHub.model.entity.QuestionBank;
import com.paul.knowledgeHub.model.entity.QuestionBankQuestion;
import com.paul.knowledgeHub.model.entity.User;
import com.paul.knowledgeHub.model.vo.QuestionBankQuestionVO;
import com.paul.knowledgeHub.model.vo.UserVO;
import com.paul.knowledgeHub.service.QuestionBankQuestionService;
import com.paul.knowledgeHub.service.QuestionBankService;
import com.paul.knowledgeHub.service.QuestionService;
import com.paul.knowledgeHub.service.UserService;
import com.paul.knowledgeHub.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 题库题目关联服务实现
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    //    @Autowired
//    private QuestionService questionService;
//
    @Resource
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add                  对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);

        //题目和题库必须存在
//        Long questionId = questionBankQuestion.getQuestionId();
//        if(questionId != null){
//            Question question = questionService.getById(questionId);
//            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR,"题目不存在");
//        }
//
//        Long questionBankId = questionBankQuestion.getQuestionBankId();
//        if(questionBankId != null){
//            QuestionBank questionBank = questionBankService.getById(questionBankId);
//            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR,"题库不存在");
//        }


//        // todo 从对象中取值
//        String title = questionBankQuestion.getTitle();
//        // 创建数据时，参数不能为空
//        if (add) {
//            // todo 补充校验规则
//            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
//        }
//        // 修改数据时，有参数则校验
//        // todo 补充校验规则
//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        // todo 补充需要的查询条件


        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目关联封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);

        // endregion

        return questionBankQuestionVO;
    }

    /**
     * 分页获取题库题目关联封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量添加题目到题库
     *
     * @param questionIdList
     * @param bankId
     * @param LoginUser
     */
    @Override
    @Async
    public void batchAddQuestionToBank(List<Long> questionIdList, Long bankId, User LoginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(bankId == null || bankId <= 0, ErrorCode.PARAMS_ERROR, "题库id为空");
        ThrowUtils.throwIf(LoginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 检查题目id是否存在，优化sql查询
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);

        // 取出合法的题目id
        List<Long> vaildQuestionList = questionService.listObjs(questionLambdaQueryWrapper, obj -> (Long) obj);
        ThrowUtils.throwIf(CollUtil.isEmpty(vaildQuestionList), ErrorCode.NOT_FOUND_ERROR, "题目id列表不存在");

        // 检查哪些题目不在题库中，避免重复插入，
        // 首先查已经存在于题库中的题目id
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, bankId)
                .notIn(QuestionBankQuestion::getQuestionId, vaildQuestionList);
        List<QuestionBankQuestion> existQuestionIdList = this.list(lambdaQueryWrapper);

        // 排除已经存在在题库中的题目id
        vaildQuestionList = vaildQuestionList.stream().filter(questionId -> {
            return !existQuestionIdList.contains(questionId);
        }).collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(vaildQuestionList), ErrorCode.NOT_FOUND_ERROR, "所有题目已经在题库中");

        // 检查题库id是否存在
        QuestionBank questionBank = questionBankService.getById(bankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        // 自定义线程池，用于异步提交任务
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20,
                50,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );


        // 提前校验题目，再循环插入
        int batchSize = 1000;
        int totalQuestionListSize = vaildQuestionList.size();
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            List<Long> subList = vaildQuestionList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream().map(questionId -> {
                QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                questionBankQuestion.setQuestionId(questionId);
                questionBankQuestion.setQuestionBankId(bankId);
                questionBankQuestion.setUserId(LoginUser.getId());
                return questionBankQuestion;
            }).collect(Collectors.toList());

            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionBankQuestionService.batchAddQuestionToBankInner(questionBankQuestions);
            }, customExecutor);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        customExecutor.shutdown();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchAddQuestionToBankInner(List<QuestionBankQuestion> questionBankQuestionList) {
//        for(QuestionBankQuestion questionBankQuestion : questionBankQuestionList){
//            Long questionId = questionBankQuestion.getQuestionId();
//            Long questionBankId = questionBankQuestion.getQuestionBankId();
//            try{
//                boolean result = this.save(questionBankQuestion);
//                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
//            }catch (DataIntegrityViolationException e){
//                log.error("数据库唯一键冲突或违反其他完整性约束，题目id:{}，题库id:{}", questionId, questionBankId);
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在与题库中");
//            }catch (DataAccessException e){
//                log.error("数据库唯一键冲突或违反其他完整性约束，题目id:{}，题库id:{}", questionId, questionBankId);
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
//            }catch (Exception e){
//                log.error("向题库添加题目时，发生未知失败，题目id:{}，题库id:{}", questionId, questionBankId);
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目到题库失败");
//            }
//        }
        // 修改为批量插入
        try {
            boolean result = this.saveBatch(questionBankQuestionList);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束，错误信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在与题库中");
        } catch (DataAccessException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束，错误信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            log.error("向题库添加题目时，发生未知失败，错误信息：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加题目到题库失败");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionFromBank(List<Long> questionIdList, Long bankId) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        ThrowUtils.throwIf(bankId == null || bankId <= 0, ErrorCode.PARAMS_ERROR, "题库id为空");

//        // 检查题目id是否存在
//        List<Question> questionList = questionService.listByIds(questionIdList);
//        // 取出合法的题目id
//        List<Long> vaildQuestionList = questionList.stream()
//                .map(Question::getId)
//                .collect(Collectors.toList());
//
//        // 检查题库id是否存在
//        QuestionBank questionBank = questionBankService.getById(bankId);
//        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR,"题库不存在");

        // 循环插入
        for (Long questionId : questionIdList) {
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, bankId);
            boolean result = this.remove(lambdaQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "从题库中移除题目失败");
            }
        }
    }

}
