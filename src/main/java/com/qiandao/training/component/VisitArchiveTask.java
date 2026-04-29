package com.qiandao.training.component;

import com.qiandao.training.service.VisitRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VisitArchiveTask {

    @Autowired
    private VisitRecordService visitRecordService;

    private static final int KEEP_DAYS = 30;

    @Scheduled(cron = "0 30 3 * * ?")
    public void archiveOldRecords() {
        log.info("开始执行定时归档任务，保留最近{}天的数据", KEEP_DAYS);
        try {
            int count = visitRecordService.archiveOldRecords(KEEP_DAYS);
            log.info("归档任务完成，处理{}条记录", count);
        } catch (Exception e) {
            log.error("归档任务执行失败", e);
        }
    }

}
