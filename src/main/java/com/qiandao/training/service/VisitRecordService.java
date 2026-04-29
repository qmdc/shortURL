package com.qiandao.training.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiandao.training.entity.VisitRecordEntity;
import com.qiandao.training.entity.VisitStatsVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VisitRecordService extends IService<VisitRecordEntity> {

    boolean recordVisit(String mapId, String url, HttpServletRequest request);

    boolean shouldCountUnique(String mapId, String ip);

    VisitStatsVo getVisitStatsByMapId(String mapId, int days);

    Page<VisitRecordEntity> getVisitRecordsByMapId(String mapId, Integer page, Integer size);

    List<Map<String, Object>> getHourlyTrend(String mapId, int days);

    List<Map<String, Object>> getDailyTrend(String mapId, int days);

    List<Map<String, Object>> getWeeklyTrend(String mapId, int weeks);

    List<Map<String, Object>> getRegionDistribution(String mapId, int days);

    List<Map<String, Object>> getDeviceDistribution(String mapId, int days);

    List<Map<String, Object>> getBrowserDistribution(String mapId, int days);

    int archiveOldRecords(int keepDays);

    Map<String, Object> getUserVisitOverview(String userId);

    Page<Map<String, Object>> getUserVisitRecords(String userId, Integer page, Integer size, Integer days);

}
