package com.qiandao.training;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.qiandao.training.entity.MapEntity;
import com.qiandao.training.entity.RecordVo;
import com.qiandao.training.global.Constant;
import com.qiandao.training.service.MapService;
import com.qiandao.training.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class TrainingApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MapService mapService;

    @Test
    void contextLoads() {
        List<String> urls = new ArrayList<>(redisUtil.multiGetValues("http*"));
        List<String> values = redisUtil.multiGet(urls);
        int size = urls.size();
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < size; i++) {
            map.put(urls.get(i), Integer.parseInt(values.get(i)));
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Object> resultMap = new LinkedHashMap<>();
        int rank = 0;
        for (Map.Entry<String, Integer> mapping : list) {
            if (rank <= 10) {
                resultMap.put(mapping.getKey(), mapping.getValue());
                System.out.println("===>"+mapping.getKey()+ " " + mapping.getValue() );
                rank++;
            } else {
                break;
            }
        }
        resultMap.forEach((k,v)-> System.out.println(k+ " " + v ));

    }

    @Test
    void record() {
        List<Object> hValues = redisUtil.hValues(Constant.emailKey);
        for (Object hValue : hValues) {
            System.out.println(hValue);
            RecordVo recordVo = JSON.parseObject((String) hValue, RecordVo.class);
            System.out.println("--"+recordVo);
        }
    }

}
