package com.qiandao.training.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiandao.training.config.UserContext;
import com.qiandao.training.entity.VisitRecordEntity;
import com.qiandao.training.entity.VisitStatsVo;
import com.qiandao.training.global.R;
import com.qiandao.training.service.VisitRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/analytics")
public class VisitAnalyticsController {

    @Autowired
    private VisitRecordService visitRecordService;

    @GetMapping("/overview")
    public R getUserOverview() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        Map<String, Object> overview = visitRecordService.getUserVisitOverview(userId);
        return R.ok().setData(overview);
    }

    @GetMapping("/records")
    public R getUserRecords(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        Page<Map<String, Object>> records = visitRecordService.getUserVisitRecords(userId, page, size, days);
        return R.ok().setData(records);
    }

    @GetMapping("/stats/{mapId}")
    public R getStatsByMapId(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        VisitStatsVo stats = visitRecordService.getVisitStatsByMapId(mapId, days);
        return R.ok().setData(stats);
    }

    @GetMapping("/records/{mapId}")
    public R getRecordsByMapId(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        Page<VisitRecordEntity> records = visitRecordService.getVisitRecordsByMapId(mapId, page, size);
        return R.ok().setData(records);
    }

    @GetMapping("/trend/hourly/{mapId}")
    public R getHourlyTrend(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "1") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> trend = visitRecordService.getHourlyTrend(mapId, days);
        return R.ok().setData(trend);
    }

    @GetMapping("/trend/daily/{mapId}")
    public R getDailyTrend(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> trend = visitRecordService.getDailyTrend(mapId, days);
        return R.ok().setData(trend);
    }

    @GetMapping("/trend/weekly/{mapId}")
    public R getWeeklyTrend(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "8") Integer weeks) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> trend = visitRecordService.getWeeklyTrend(mapId, weeks);
        return R.ok().setData(trend);
    }

    @GetMapping("/region/{mapId}")
    public R getRegionDistribution(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> distribution = visitRecordService.getRegionDistribution(mapId, days);
        return R.ok().setData(distribution);
    }

    @GetMapping("/device/{mapId}")
    public R getDeviceDistribution(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> distribution = visitRecordService.getDeviceDistribution(mapId, days);
        return R.ok().setData(distribution);
    }

    @GetMapping("/browser/{mapId}")
    public R getBrowserDistribution(
            @PathVariable("mapId") String mapId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }
        List<Map<String, Object>> distribution = visitRecordService.getBrowserDistribution(mapId, days);
        return R.ok().setData(distribution);
    }

}
