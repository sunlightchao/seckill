package com.sunlightchao.seckill.dto;

import com.sunlightchao.seckill.entity.SuccessKilled;

public class SeckillExecution {
    private long seckillId;
    /**
     * 秒杀执行结果的状态
     */
    private int state;
    /**
     * 状态的明文标识
     */
    private String stateInfo;
    /**
     * 当秒杀成功时，需要传递秒杀成功的对象回去
     */
    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, int state, String stateInfo, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
        this.successKilled = successKilled;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
