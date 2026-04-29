package com.qiandao.training.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiandao.training.config.UserContext;
import com.qiandao.training.entity.MapEntity;
import com.qiandao.training.entity.RecordVo;
import com.qiandao.training.global.Constant;
import com.qiandao.training.global.R;
import com.qiandao.training.service.MapService;
import com.qiandao.training.service.VisitRecordService;
import com.qiandao.training.utils.RandomUtil;
import com.qiandao.training.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
public class ShortCodeController {

    @SuppressWarnings("all")
    @Autowired
    private RedisUtil redisUtil;

    @SuppressWarnings("all")
    @Autowired
    private MapService mapService;

    @SuppressWarnings("all")
    @Autowired
    private JavaMailSender mailSender;

    @SuppressWarnings("all")
    @Autowired
    private VisitRecordService visitRecordService;

    @GetMapping("/create/shorturl")
    public R shortcode(String url) {
        if (url == null || url.isBlank()) {
            return R.error("url不能为空");
        }

        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }

        QueryWrapper<MapEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("url", url).eq("user_id", userId);
        MapEntity mapEntity = mapService.getOne(wrapper);
        if (mapEntity != null) {
            return R.ok().setData(mapEntity);
        }

        String md5 = SecureUtil.md5(url);
        String random_url = RandomUtil.getStringRandom(7);
        MapEntity entity = MapEntity.builder()
                .user_id(userId)
                .url(url)
                .md5(md5)
                .random_url(random_url)
                .build();
        boolean save = mapService.save(entity);
        if (save) {
            log.info("短链接生成成功:{}", JSON.toJSON(entity));
            return R.ok().setData(entity);
        }
        return R.error("短码生成失败");
    }

    @GetMapping("/user/shorturls")
    public R getUserShortUrls() {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }

        QueryWrapper<MapEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        List<MapEntity> list = mapService.list(wrapper);

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (MapEntity entity : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entity.getId());
            map.put("url", entity.getUrl());
            map.put("md5", entity.getMd5());
            map.put("random_url", entity.getRandom_url());
            map.put("create_time", entity.getCreate_time());

            String clickNum = redisUtil.get(entity.getUrl());
            map.put("click_num", clickNum != null ? clickNum : "0");

            resultList.add(map);
        }

        return R.ok().setData(resultList);
    }

    @DeleteMapping("/shorturl/{id}")
    public R deleteShortUrl(@PathVariable("id") String id) {
        String userId = UserContext.getUserId();
        if (userId == null) {
            return R.error("用户未登录");
        }

        QueryWrapper<MapEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("user_id", userId);
        MapEntity entity = mapService.getOne(wrapper);

        if (entity == null) {
            return R.error("短链接不存在或无权删除");
        }

        boolean remove = mapService.remove(wrapper);
        if (remove) {
            log.info("短链接删除成功:{}", id);
            return R.ok("删除成功");
        }
        return R.error("删除失败");
    }

    //访问短链接
    @GetMapping("/{shorturl}")
    public void view(@PathVariable("shorturl") String shorturl, HttpServletResponse response, HttpServletRequest request) {
        QueryWrapper<MapEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", shorturl).or().eq("md5", shorturl).or().eq("random_url", shorturl);
        MapEntity mapEntity = mapService.getOne(wrapper);
        if (mapEntity != null) {
            boolean recorded = visitRecordService.recordVisit(mapEntity.getId(), mapEntity.getUrl(), request);
            if (recorded) {
                log.info("访问记录保存成功: {} -> {}", shorturl, mapEntity.getUrl());
            }
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", mapEntity.getUrl());
        } else {
            try {
                request.getRequestDispatcher("/404").forward(request, response);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //还原短链
    @GetMapping("/recharge")
    public R recharge(String url) {
        String shorturl = url.substring(url.lastIndexOf("/") + 1);
        QueryWrapper<MapEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", shorturl).or().eq("md5", shorturl).or().eq("random_url", shorturl);
        MapEntity mapEntity = mapService.getOne(wrapper);
        if (mapEntity != null) {
            String num = redisUtil.get(mapEntity.getUrl());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
            String format = dateFormat.format(mapEntity.getCreate_time());
            Map<String, String> map = new HashMap<>() {{
                put("num", num);
                put("longURL", mapEntity.getUrl());
                put("createTime", format);
            }};
            return R.ok().setData(map);
        } else {
            return R.error("该短链不存在于此系统！");
        }
    }

    //链接访问TOP10
    @GetMapping("/top10")
    public R top10() {
        log.info("获取访问链接TOP10记录");
        List<String> urls = new ArrayList<>(redisUtil.multiGetValues("http*"));
        List<String> values = redisUtil.multiGet(urls);
        int size = urls.size();
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < size; i++) {
            map.put(urls.get(i), Integer.parseInt(values.get(i)));
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Integer> resultMap = new LinkedHashMap<>();
        int rank = 0;
        for (Map.Entry<String, Integer> mapping : list) {
            if (rank <= 10) {
                resultMap.put(mapping.getKey(), mapping.getValue());
                rank++;
            } else {
                break;
            }
        }
        return R.ok().setData(resultMap);
    }

    //短链不存在
    @GetMapping("/404")
    @ResponseBody
    public String nopage() {
        return "该短链接不存在！";
    }

    //获取发件记录
    @GetMapping("/record")
    @ResponseBody
    public R record() {
        List<Object> hValues = redisUtil.hValues(Constant.emailKey);
        List<RecordVo> recordVos = new ArrayList<>();
        for (Object hValue : hValues) {
            RecordVo recordVo = JSON.parseObject((String) hValue, RecordVo.class);
            recordVos.add(recordVo);
        }
        return R.ok().setData(recordVos);
    }

    //统计点击量
    public boolean clickNum(String url) {
        if (url.isBlank()) {
            return false;
        }
        String shortcode = redisUtil.get(url);
        if (shortcode == null) {
            redisUtil.set(url, String.valueOf(1));
        } else {
            int clickNum = Integer.parseInt(shortcode) + 1;
            redisUtil.set(url, String.valueOf(clickNum));
            if (clickNum > 10) {
                boolean flag = emailToRoot(url, clickNum);
                if (flag) {
                    log.info("url：{} 邮件发送成功，点击次数：{}", url, clickNum);
                } else {
                    log.info("不触发邮件推送 {} {}", url, clickNum);
                }
            } else {
                log.info("不触发邮件推送 {} {}", url, clickNum);
            }
        }
        return true;
    }

    //发送邮件
    public boolean emailToRoot(String url, int clickNum) {
        MapEntity mapEntity = mapService.getOne(new QueryWrapper<MapEntity>().eq("url", url));
        if (mapEntity == null || (clickNum - mapEntity.getEmail_num()) < 10) {
            return false;
        }
        log.info("开始发送邮件 {} {}", url, clickNum);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("notre1024@163.com");
            helper.setTo(Constant.email);
            helper.setSubject("链接访问量");
            String nowtime = RandomUtil.nowtime();
            String msg = "截止到：" + nowtime + " 链接：" + url + " 的访问量为：" + clickNum;
            helper.setText(msg);
            mailSender.send(message);
            log.info("邮件发送成功 {} {}", url, clickNum);
            mapEntity.setEmail_num(clickNum);
            boolean updateById = mapService.updateById(mapEntity);
            if (updateById) {
                log.info("数据库访问量更改成功");
            } else {
                log.error("数据库访问量更改失败");
            }
            boolean emailRecord = emailRecord(msg, "notre1024@163.com", Constant.email, nowtime);
            if (emailRecord) {
                log.info("发件记录成功 {}", msg);
            } else {
                log.error("发件记录失败 {}", msg);
            }
        } catch (Exception e) {
            log.error("邮件发送失败 {} {}", url, clickNum);
        }
        return true;
    }

    //记录发件记录
    public boolean emailRecord(String msg, String from, String to, String dateTime) {
        Map<String, String> map = new HashMap<>(){{
            put("message",msg);
            put("from",from);
            put("to",to);
            put("dateTime",dateTime);
        }};
        try {
            redisUtil.hPut(Constant.emailKey,RandomUtil.getUUID(8),JSON.toJSONString(map));
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
