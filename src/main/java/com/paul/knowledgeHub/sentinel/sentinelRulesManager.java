package com.paul.knowledgeHub.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class sentinelRulesManager {
    @PostConstruct
    public void initRules() {
        initFlowQpsRule();
        initDegradeRule();
    }

    private void initFlowQpsRule() {
        // 对单个IP进行流控
        ParamFlowRule rule = new ParamFlowRule("listPageVoByPage")
                .setParamIdx(0) // 第0个参数限流，即IP地址
                .setCount(60) // 限制每秒只能通过60个请求
                .setDurationInSec(60); // 统计时长60秒
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }

    private void initDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();

        DegradeRule slowCallRule = new DegradeRule();
        slowCallRule.setResource("listPageVoByPage"); // 资源名
        slowCallRule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType()); // 熔断策略，慢调用
        slowCallRule.setCount(0.2); // 触发熔断的阈值
        slowCallRule.setTimeWindow(60); // 熔断时长，单位为秒
        slowCallRule.setStatIntervalMs(30 * 1000); // 统计时长，单位为毫秒
        slowCallRule.setMinRequestAmount(5); // 最小请求数量
        slowCallRule.setSlowRatioThreshold(3); // 慢调用阈值
        rules.add(slowCallRule);

        DegradeRule errorRateRule = new DegradeRule("listPageVoByPage")
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.1) // 异常率大于10%
                .setTimeWindow(60) // 熔断时长60秒
                .setStatIntervalMs(30 * 1000) // 统计时长30秒
                .setMinRequestAmount(10); // 最小请求数量
        rules.add(errorRateRule);

        DegradeRuleManager.loadRules(rules);
    }
}
