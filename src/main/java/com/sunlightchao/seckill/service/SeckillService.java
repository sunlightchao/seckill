package com.sunlightchao.seckill.service;

import com.sunlightchao.seckill.dto.Exposer;
import com.sunlightchao.seckill.dto.SeckillExecution;
import com.sunlightchao.seckill.entity.Seckill;
import com.sunlightchao.seckill.exception.RepeatKillException;
import com.sunlightchao.seckill.exception.SeckillCloseException;
import com.sunlightchao.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {
    /**
     * 查询全部秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始时输入秒杀接口的地址
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作，可能失败，可能成功，所以我抛出允许的异常SeckillExecution
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, SeckillCloseException, RepeatKillException;
}

