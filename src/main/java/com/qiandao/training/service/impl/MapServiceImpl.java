package com.qiandao.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiandao.training.dao.MapDao;
import com.qiandao.training.entity.MapEntity;
import com.qiandao.training.service.MapService;
import org.springframework.stereotype.Service;

@Service
public class MapServiceImpl  extends ServiceImpl<MapDao, MapEntity> implements MapService {
}
