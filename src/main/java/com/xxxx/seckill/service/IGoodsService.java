package com.xxxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author zhoubin
 * @since 2023-03-14
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo>  findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
