package com.qiandao.training.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RecordVo {

    private String dateTime;

    private String from;

    private String to;

    private String message;

}
