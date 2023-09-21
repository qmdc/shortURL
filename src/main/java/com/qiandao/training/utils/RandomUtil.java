package com.qiandao.training.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class RandomUtil {

    /**
     * @param n 位数
     * @return 返回uuid的前n位
     */
    public static String getUUID(int n) {
        String uuid = UUID.randomUUID().toString();
        String replace = uuid.replace("-", "");
        return replace.substring(0, n);
    }

    /**
     * 生成随机数字和字母
     *
     * @param length 生成长度
     * @return shortUrlId
     */
    public static String getStringRandom(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else {
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }

    /**
     *
     * @return 字符串 获取当前（yyyy-MM-dd HH:mm:ss）时间
     */
    public static String nowtime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

}
