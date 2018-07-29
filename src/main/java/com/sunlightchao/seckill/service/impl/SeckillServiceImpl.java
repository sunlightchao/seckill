package com.sunlightchao.seckill.service.impl;

import com.sunlightchao.seckill.dao.SeckillDao;
import com.sunlightchao.seckill.dao.SuccessKilledDao;
import com.sunlightchao.seckill.dto.Exposer;
import com.sunlightchao.seckill.dto.SeckillExecution;
import com.sunlightchao.seckill.entity.Seckill;
import com.sunlightchao.seckill.entity.SuccessKilled;
import com.sunlightchao.seckill.exception.RepeatKillException;
import com.sunlightchao.seckill.exception.SeckillCloseException;
import com.sunlightchao.seckill.exception.SeckillException;
import com.sunlightchao.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    /**
     * 日志对象
     */
    private static Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    //加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
    private final String salt = "cfctcdjtucf";

    private final SeckillDao seckillDao;

    private final SuccessKilledDao successKilledDao;

    @Autowired
    public SeckillServiceImpl(SeckillDao seckillDao, SuccessKilledDao successKilledDao) {
        this.seckillDao = seckillDao;
        this.successKilledDao = successKilledDao;
    }

    @Override
    public List<Seckill> getSeckillList() {

        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(),
                    startTime.getTime(), endTime.getTime());
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, SeckillCloseException, RepeatKillException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = new Date();

        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount < 1) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount < 1) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, 1, "秒杀成功", successKilled);
                }
            }
        } catch (RepeatKillException | SeckillCloseException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所以编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error :" + e.getMessage());
        }
    }
}
