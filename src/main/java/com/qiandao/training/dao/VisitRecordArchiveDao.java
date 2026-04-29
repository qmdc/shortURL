package com.qiandao.training.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiandao.training.entity.VisitRecordArchiveEntity;
import org.apache.ibatis.annotations.*;

import java.util.Date;

@Mapper
public interface VisitRecordArchiveDao extends BaseMapper<VisitRecordArchiveEntity> {

    @Insert("INSERT INTO visit_record_archive " +
            "(id, map_id, url, ip, user_agent, referer, country, province, city, device_type, browser, os, is_unique, create_time, archive_time) " +
            "SELECT id, map_id, url, ip, user_agent, referer, country, province, city, device_type, browser, os, is_unique, create_time, NOW() " +
            "FROM visit_record " +
            "WHERE create_time < #{beforeDate}")
    int insertArchiveFromVisitRecord(@Param("beforeDate") Date beforeDate);

    @Delete("DELETE FROM visit_record WHERE create_time < #{beforeDate}")
    int deleteOldRecords(@Param("beforeDate") Date beforeDate);

    @Select("SELECT COUNT(*) FROM visit_record WHERE create_time < #{beforeDate}")
    int countRecordsToArchive(@Param("beforeDate") Date beforeDate);

}
