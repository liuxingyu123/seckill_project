package com.xxxx.seckill.exception;


import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor//无参构造
@AllArgsConstructor//空参构造
public class GlobalException extends RuntimeException {
    private RespBeanEnum respBeanEnum;
}
