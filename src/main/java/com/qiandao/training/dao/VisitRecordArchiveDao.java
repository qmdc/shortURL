package com.qiandao.training.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiandao.training.entity.VisitRecordArchiveEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface VisitRecordArchiveDao extends BaseMapper<VisitRecordArchiveEntity> {

    @Select("<script>" +
            "INSERT INTO visit_record_archive " +
            "(id, map_id, url, ip, user_agent, referer, country, province, city, device_type, browser, os, is_unique, create_time, archive_time) " +
            "SELECT id, map_id, url, ip, user_agent, referer, country, province, city, device_type, browser, os, is_unique, create_time, NOW() " +
            "FROM visit_record " +
            "WHERE create_time < #{beforeDate}" +
            "</script>")
    int insertArchiveFromVisitRecord(@Param("beforeDate") Date beforeDate);

    @Select("<script>" +
            "DELETE FROM visit_record WHERE create_time < #{beforeDate}" +
            "</script>")
    int deleteOldRecords(@Param("beforeDate") Date beforeDate);

    @Select("<script>" +
            "SELECT COUNT(*) FROM visit_record WHERE create_time < #{beforeDate}" +
            "</script>")
    int countRecordsToArchive(@Param("beforeDate") Date beforeDate);

}
