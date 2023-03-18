package com.xxxx.seckill.vo;

import com.xxxx.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/*vo包里面的都是和前端交互的参数*/
/*登录参数*/
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min=32)//由于密码是经过md5加密，所以是32位的，这里进行一个约束
    private String password;
}
