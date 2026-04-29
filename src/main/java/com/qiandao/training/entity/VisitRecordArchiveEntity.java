package com.qiandao.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("visit_record_archive")
public class VisitRecordArchiveEntity implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String map_id;

    private String url;

    private String ip;

    private String user_agent;

    private String referer;

    private String country;

    private String province;

    private String city;

    private String device_type;

    private String browser;

    private String os;

    private Integer is_unique;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date create_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @TableField(fill = FieldFill.INSERT)
    private Date archive_time;

}
