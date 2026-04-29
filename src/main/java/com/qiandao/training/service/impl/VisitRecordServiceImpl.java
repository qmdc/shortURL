package com.qiandao.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiandao.training.dao.VisitRecordArchiveDao;
import com.qiandao.training.dao.VisitRecordDao;
import com.qiandao.training.entity.VisitRecordEntity;
import com.qiandao.training.entity.VisitStatsVo;
import com.qiandao.training.service.VisitRecordService;
import com.qiandao.training.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VisitRecordServiceImpl extends ServiceImpl<VisitRecordDao, VisitRecordEntity> implements VisitRecordService {

    @Autowired
    private VisitRecordArchiveDao archiveDao;

    @Autowired
    private RedisUtil redisUtil;

    private static final String ANTI_FLOOD_PREFIX = "anti_flood:";
    private static final int ANTI_FLOOD_MINUTES = 5;

    @Override
    @Transactional
    public boolean recordVisit(String mapId, String url, HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");

            boolean isUnique = shouldCountUnique(mapId, ip);

            Map<String, String> uaInfo = parseUserAgent(userAgent);
            Map<String, String> regionInfo = parseRegion(ip);

            VisitRecordEntity record = VisitRecordEntity.builder()
                    .map_id(mapId)
                    .url(url)
                    .ip(ip)
                    .user_agent(userAgent)
                    .referer(referer != null ? referer : "")
                    .country(regionInfo.get("country"))
                    .province(regionInfo.get("province"))
                    .city(regionInfo.get("city"))
                    .device_type(uaInfo.get("device_type"))
                    .browser(uaInfo.get("browser"))
                    .os(uaInfo.get("os"))
                    .is_unique(isUnique ? 1 : 0)
                    .build();

            boolean saved = this.save(record);

            if (isUnique) {
                setAntiFloodCache(mapId, ip);
            }

            updateRedisClickCount(url);

            log.info("访问记录保存成功: mapId={}, ip={}, isUnique={}", mapId, ip, isUnique);
            return saved;
        } catch (Exception e) {
            log.error("记录访问失败", e);
            return false;
        }
    }

    @Override
    public boolean shouldCountUnique(String mapId, String ip) {
        String key = ANTI_FLOOD_PREFIX + mapId + ":" + ip;
        String value = redisUtil.get(key);
        return value == null;
    }

    private void setAntiFloodCache(String mapId, String ip) {
        String key = ANTI_FLOOD_PREFIX + mapId + ":" + ip;
        redisUtil.setEx(key, "1", ANTI_FLOOD_MINUTES, TimeUnit.MINUTES);
    }

    private void updateRedisClickCount(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        String current = redisUtil.get(url);
        if (current == null) {
            redisUtil.set(url, "1");
        } else {
            try {
                int count = Integer.parseInt(current) + 1;
                redisUtil.set(url, String.valueOf(count));
            } catch (NumberFormatException e) {
                redisUtil.set(url, "1");
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private Map<String, String> parseUserAgent(String userAgent) {
        Map<String, String> result = new HashMap<>();

        if (userAgent == null || userAgent.isEmpty()) {
            result.put("device_type", "Unknown");
            result.put("browser", "Unknown");
            result.put("os", "Unknown");
            return result;
        }

        String deviceType = "Desktop";
        if (userAgent.contains("Mobile") || userAgent.contains("Android") ||
                userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            deviceType = "Mobile";
        }
        if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
            deviceType = "Tablet";
        }
        result.put("device_type", deviceType);

        String browser = "Unknown";
        if (userAgent.contains("Edg")) {
            browser = "Edge";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            browser = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            browser = "Safari";
        } else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
            browser = "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            browser = "IE";
        }
        result.put("browser", browser);

        String os = "Unknown";
        if (userAgent.contains("Windows NT")) {
            os = "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            os = "MacOS";
        } else if (userAgent.contains("Linux")) {
            os = "Linux";
        } else if (userAgent.contains("Android")) {
            os = "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            os = "iOS";
        }
        result.put("os", os);

        return result;
    }

    private Map<String, String> parseRegion(String ip) {
        Map<String, String> result = new HashMap<>();
        result.put("country", "未知");
        result.put("province", "未知");
        result.put("city", "未知");

        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            result.put("country", "中国");
            result.put("province", "本地");
            result.put("city", "本地");
            return result;
        }

        try {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                int first = Integer.parseInt(parts[0]);
                if (first == 10 || first == 127 ||
                        (first == 172 && Integer.parseInt(parts[1]) >= 16 && Integer.parseInt(parts[1]) <= 31) ||
                        (first == 192 && "168".equals(parts[1]))) {
                    result.put("country", "中国");
                    result.put("province", "内网");
                    result.put("city", "内网");
                }
            }
        } catch (Exception e) {
            log.warn("解析IP失败: {}", ip);
        }

        return result;
    }

    @Override
    public VisitStatsVo getVisitStatsByMapId(String mapId, int days) {
        VisitStatsVo stats = new VisitStatsVo();

        Map<String, Object> total = baseMapper.countTotalByMapId(mapId);
        if (total != null) {
            stats.setTotalVisits(((Number) total.getOrDefault("total", 0L)).longValue());
            stats.setUniqueVisits(((Number) total.getOrDefault("unique_count", 0L)).longValue());
        }

        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> today = baseMapper.countTodayByMapId(mapId, todayStart);
        if (today != null) {
            stats.setTodayVisits(((Number) today.getOrDefault("total", 0L)).longValue());
            stats.setTodayUniqueVisits(((Number) today.getOrDefault("unique_count", 0L)).longValue());
        }

        stats.setHourlyTrend(getHourlyTrend(mapId, 1));
        stats.setDailyTrend(getDailyTrend(mapId, Math.min(days, 30)));
        stats.setWeeklyTrend(getWeeklyTrend(mapId, Math.min(days / 7 + 1, 8)));

        stats.setRegionDistribution(getRegionDistribution(mapId, days));
        stats.setDeviceDistribution(getDeviceDistribution(mapId, days));
        stats.setBrowserDistribution(getBrowserDistribution(mapId, days));

        return stats;
    }

    @Override
    public Page<VisitRecordEntity> getVisitRecordsByMapId(String mapId, Integer page, Integer size) {
        int current = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1 || size > 100) ? 20 : size;

        Page<VisitRecordEntity> pageParam = new Page<>(current, pageSize);
        QueryWrapper<VisitRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("map_id", mapId).orderByDesc("create_time");

        return this.page(pageParam, wrapper);
    }

    @Override
    public List<Map<String, Object>> getHourlyTrend(String mapId, int days) {
        Date startTime = getDaysAgo(Math.max(days, 1));
        return baseMapper.countByHour(mapId, startTime);
    }

    @Override
    public List<Map<String, Object>> getDailyTrend(String mapId, int days) {
        Date startTime = getDaysAgo(Math.max(days, 1));
        return baseMapper.countByDay(mapId, startTime);
    }

    @Override
    public List<Map<String, Object>> getWeeklyTrend(String mapId, int weeks) {
        Date startTime = getDaysAgo(Math.max(weeks * 7, 7));
        return baseMapper.countByWeek(mapId, startTime);
    }

    @Override
    public List<Map<String, Object>> getRegionDistribution(String mapId, int days) {
        Date startTime = days > 0 ? getDaysAgo(days) : null;
        return baseMapper.countByProvince(mapId, startTime);
    }

    @Override
    public List<Map<String, Object>> getDeviceDistribution(String mapId, int days) {
        Date startTime = days > 0 ? getDaysAgo(days) : null;
        return baseMapper.countByDevice(mapId, startTime);
    }

    @Override
    public List<Map<String, Object>> getBrowserDistribution(String mapId, int days) {
        Date startTime = days > 0 ? getDaysAgo(days) : null;
        return baseMapper.countByBrowser(mapId, startTime);
    }

    private Date getDaysAgo(int days) {
        return Date.from(LocalDateTime.now().minusDays(days).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    @Transactional
    public int archiveOldRecords(int keepDays) {
        Date beforeDate = getDaysAgo(keepDays);
        int count = archiveDao.countRecordsToArchive(beforeDate);
        if (count == 0) {
            log.info("没有需要归档的历史数据");
            return 0;
        }
        log.info("开始归档历史数据，共{}条", count);
        int inserted = archiveDao.insertArchiveFromVisitRecord(beforeDate);
        int deleted = archiveDao.deleteOldRecords(beforeDate);
        log.info("归档完成，插入{}条，删除{}条", inserted, deleted);
        return deleted;
    }

    @Override
    public Map<String, Object> getUserVisitOverview(String userId) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<VisitRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.inSql("map_id", "SELECT id FROM map WHERE user_id = '" + userId.replace("'", "\\'") + "'");

        long totalVisits = this.count(wrapper);

        wrapper.clear();
        wrapper.inSql("map_id", "SELECT id FROM map WHERE user_id = '" + userId.replace("'", "\\'") + "'")
                .ge("create_time", getDaysAgo(1));
        long todayVisits = this.count(wrapper);

        wrapper.clear();
        wrapper.inSql("map_id", "SELECT id FROM map WHERE user_id = '" + userId.replace("'", "\\'") + "'")
                .eq("is_unique", 1);
        long uniqueVisits = this.count(wrapper);

        result.put("totalVisits", totalVisits);
        result.put("todayVisits", todayVisits);
        result.put("uniqueVisits", uniqueVisits);

        return result;
    }

    @Override
    public Page<Map<String, Object>> getUserVisitRecords(String userId, Integer page, Integer size, Integer days) {
        int current = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1 || size > 100) ? 20 : size;
        Date startTime = days != null && days > 0 ? getDaysAgo(days) : getDaysAgo(30);

        List<Map<String, Object>> allRecords = baseMapper.findRecordsWithMapByUserId(userId, startTime);

        Page<Map<String, Object>> resultPage = new Page<>(current, pageSize);
        resultPage.setTotal(allRecords.size());

        int start = (current - 1) * pageSize;
        int end = Math.min(start + pageSize, allRecords.size());
        if (start < end) {
            resultPage.setRecords(allRecords.subList(start, end));
        } else {
            resultPage.setRecords(new ArrayList<>());
        }

        return resultPage;
    }
}
