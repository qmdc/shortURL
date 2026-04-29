package com.qiandao.training.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VisitStatsVo {

    private Long totalVisits;
    private Long uniqueVisits;
    private Long todayVisits;
    private Long todayUniqueVisits;

    private List<Map<String, Object>> hourlyTrend;
    private List<Map<String, Object>> dailyTrend;
    private List<Map<String, Object>> weeklyTrend;

    private List<Map<String, Object>> regionDistribution;
    private List<Map<String, Object>> deviceDistribution;
    private List<Map<String, Object>> browserDistribution;

}
