package com.qiandao.training.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiandao.training.entity.VisitRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VisitRecordDao extends BaseMapper<VisitRecordEntity> {

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d %H:00') as time, " +
            "COUNT(*) as total, " +
            "SUM(is_unique) as unique_count " +
            "FROM visit_record " +
            "WHERE map_id = #{mapId} " +
            "AND create_time >= #{startTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d %H:00') " +
            "ORDER BY time ASC")
    List<Map<String, Object>> countByHour(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as time, " +
            "COUNT(*) as total, " +
            "SUM(is_unique) as unique_count " +
            "FROM visit_record " +
            "WHERE map_id = #{mapId} " +
            "AND create_time >= #{startTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "ORDER BY time ASC")
    List<Map<String, Object>> countByDay(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select("SELECT YEARWEEK(create_time, 1) as week, " +
            "DATE_FORMAT(MIN(create_time), '%Y-%m-%d') as start_date, " +
            "DATE_FORMAT(MAX(create_time), '%Y-%m-%d') as end_date, " +
            "COUNT(*) as total, " +
            "SUM(is_unique) as unique_count " +
            "FROM visit_record " +
            "WHERE map_id = #{mapId} " +
            "AND create_time >= #{startTime} " +
            "GROUP BY YEARWEEK(create_time, 1) " +
            "ORDER BY week ASC")
    List<Map<String, Object>> countByWeek(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select({"<script>",
            "SELECT ",
            "  COALESCE(province, '未知') as name, ",
            "  COUNT(*) as value ",
            "FROM visit_record ",
            "WHERE map_id = #{mapId} ",
            "<if test='startTime != null'>",
            "  AND create_time >= #{startTime} ",
            "</if>",
            "GROUP BY province ",
            "ORDER BY value DESC",
            "</script>"})
    List<Map<String, Object>> countByProvince(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select({"<script>",
            "SELECT ",
            "  COALESCE(device_type, '未知') as name, ",
            "  COUNT(*) as value ",
            "FROM visit_record ",
            "WHERE map_id = #{mapId} ",
            "<if test='startTime != null'>",
            "  AND create_time >= #{startTime} ",
            "</if>",
            "GROUP BY device_type ",
            "ORDER BY value DESC",
            "</script>"})
    List<Map<String, Object>> countByDevice(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select({"<script>",
            "SELECT ",
            "  COALESCE(browser, '未知') as name, ",
            "  COUNT(*) as value ",
            "FROM visit_record ",
            "WHERE map_id = #{mapId} ",
            "<if test='startTime != null'>",
            "  AND create_time >= #{startTime} ",
            "</if>",
            "GROUP BY browser ",
            "ORDER BY value DESC",
            "</script>"})
    List<Map<String, Object>> countByBrowser(@Param("mapId") String mapId, @Param("startTime") Date startTime);

    @Select("SELECT COUNT(*) as total, SUM(is_unique) as unique_count " +
            "FROM visit_record " +
            "WHERE map_id = #{mapId}")
    Map<String, Object> countTotalByMapId(@Param("mapId") String mapId);

    @Select("SELECT COUNT(*) as total, SUM(is_unique) as unique_count " +
            "FROM visit_record " +
            "WHERE map_id = #{mapId} " +
            "AND create_time >= #{todayStart}")
    Map<String, Object> countTodayByMapId(@Param("mapId") String mapId, @Param("todayStart") Date todayStart);

    @Select("SELECT vr.*, m.url as original_url, m.random_url, m.create_time as map_create_time " +
            "FROM visit_record vr " +
            "LEFT JOIN map m ON vr.map_id = m.id " +
            "WHERE m.user_id = #{userId} " +
            "AND vr.create_time >= #{startTime} " +
            "ORDER BY vr.create_time DESC")
    List<Map<String, Object>> findRecordsWithMapByUserId(@Param("userId") String userId, @Param("startTime") Date startTime);

}
