package com.xxxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author zhoubin
 * @since 2023-03-14
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    //获取秒杀结果
    Long getResult(User user, Long goodsId);
}
