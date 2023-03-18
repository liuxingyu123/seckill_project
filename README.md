# seckill_project
# 秒杀项目

## 技术点介绍

![1676532765462](D:\graduate_study\java学习\项目\assets\1676532765462.png)

![1676533039548](D:\graduate_study\java学习\项目\assets\1676533039548.png)

![1676533309771](D:\graduate_study\java学习\项目\assets\1676533309771.png)

## 秒杀系统设计流程

秒杀系统实际主要解决两个问题，一个是并发读，一个是并发写。

并发读的核心优化理念是尽量减少用户到服务端来“读”数据，或减少读读取的数据量。

并发写也同样是以上要求；要求在数据库层面独立出来一个库做特殊处理。

最后要对秒杀系统做一些保护，以防最坏情况发生。

![1676533981880](D:\graduate_study\java学习\项目\assets\1676533981880.png)

![1676534032172](D:\graduate_study\java学习\项目\assets\1676534032172.png)

### 手机号验证

**使用注解来简化手机号的验证**：①创建新注解 ②定义检验规则 ，实现ConstraintValidator接口

### 异常处理

springmvc中可以将异常解耦出来，可以对异常信息进行统一处理

springboot：全局异常处理方式，

①使用controlleradvice和exceptionhandler的组合注解 

只能处理controller中的异常，自由度更大一点

②使用errorcontroller类来实现

可以处理任何异常

项目采用的第一种方式

### 分布式session

**分布式session**（多台服务器上都部署该项目，用户分别在不同服务器上进行登录，session就会存到不同服务器上）如何解决：项目中使用redis来解决

实现方案：①springsession来解决 （在项目中导入springsession等3个依赖包，配置redis）

之前我们获取用户信息是通过session来获取，现在我们将用户信息存到redis中，需要的时候直接获取

**关于session和cookie的一些问题： **

为什么可以通过cookie来获取用户信息？cookie是哪里来的？

cookie是服务端生成，发送给浏览器的，之后浏览器再次请求服务端就会将该cookie传递给浏览器，浏览器也就能通过该cookie中的值判断该用户。

为什么使用cookie不用session？

session会在一定时间内保存在服务器上。当访问增多，会比较占用你服务器的性能考虑到减轻服务器性能方面，应当使用COOKIE

但是cookie不是很安全，别人可以分析存放在本地的COOKIE并进行COOKIE欺骗，考虑到安全应当使用session。

个人建议：将登陆信息等重要信息存放为SESSION。其他信息如果需要保留，可以放在COOKIE中

### 压测工具JMeter

qps：每秒查询率，规定时间内处理流量的衡量标准。

tps：服务端接收到客户请求，处理请求返回客户端的时间。每秒完成多少个流程

如果服务端只处理一个接口，那么qps和tps基本相同

在服务器上保存好测试所用的脚本

执行./gmeter.sh -n -t first.jmx -l result.jtl



执行秒杀的压测导致数据库秒杀商品超卖（-12），订单数量达到了400多个。

对于一些不变的数据适合做缓存，因为如果变的化还得考虑和mysql数据库数据一致问题

### 页面优化

页面缓存： 

将商品列表页和商品详情页缓存到redis中------------》由于页面里的内容比较多，所以使用前后端分离，将静态资源缓存

使用前后端分离：

静态页面缓存到浏览器中，数据是ajax发送过去的，但是默认的缓存配置还是有限的，需要在spring配置web resource（在application.yml中进行配置）

### 解决超卖问题

解决超卖问题：给秒杀订单增加了联合唯一索引（userid-goodsid）：解决同一个用户秒杀多次同一个商品的现象存在，行级锁，利用联合主键来防止同一用户对同一秒杀商品产生多个订单问题

超卖问题：

已经设计的业务逻辑：

点击立即秒杀触发seckillcontroller，判断库存是否小于1，判断是否该用户买过该商品，（这里可能同时多个相同或不同的用户都符合该条件，压测中显示都是不同用户）进行秒杀商品：根据id获取秒杀商品，秒杀商品的库存-1，生成秒杀订单和订单（然后产生多个订单和商品超卖问题）。

**解决库存为负数的问题**：在orderserviceimpl（秒杀过程）中设置如果商品小于0，返回null，

**解决订单数超过库存的问题**：**事务控制+行级锁（乐观锁）**（在更新的时候判断是否库存小于0，更新的时候其他线程是不允许更新的）

**解决一个用户重复购买**

①加上**联合唯一索引**，否则，如果同一用户多次秒杀还是会好多用户都购买了商品

②通过给每个线程根据用户id加悲观锁，synchon， 

### 预减库存

1.通过redis预减库存，较少数据库的访问

2.内存标记减少redis的访问  ：由于库存只有10个，10个以上的用户查询都没用，值会增加redis的负担

### 消息队列实现流量削峰

3.请求进入队列缓存，异步下单（rabbitmp）实现流量削峰

在排队的时候，还是要告诉客户端是否秒杀成功：使用轮询操作

### 分布式锁

由于在分布式系统或者集群模式下可能出现同一用户并发进入秒杀。

使用分布式锁：满足在分布式系统或集群模式下多进程可见并且互斥的锁

**注意**：在多用户压测的时候，redis必须存有用户的cookie信息，

由于使用了redis预减库存，只有启动项目才会将秒杀商品数量存入redis中（小bug）。

### 安全优化

使用验证码，动态生成秒杀网址。

## 出现的问题

### 1.注入mapper会爆红

原因：mapper注入是mybatis实现的（mybatis配置文件中将mapper进行了扫描），idea只能识别spring注入的

解决：1.可以不用管，2.可以使用

为什么mapper不用注入：

因为在spring和mybatis整合的时候，mybtis调用mapperscannerconfigurer可以将所有mapper接口创建为动态代理类（bean），装配到spring容器中。

问题：前端的themleaf拿不到后端的值，前端页面报错，

解决：由于后端没有添加注解@Controller等

## 配置

#### redis

**启动redis的流程**

cd /usr/local/redis/bin

./redis-server redis.conf

yum安装报错

![1679107376580](D:\graduate_study\java学习\项目\assets\1679107376580.png)

redis报错：

报错显示设置stop-writes-on-bgsave-error

config set stop-writes-on-bgsave-error no

服务器端口问题

1、查看端口状态，比如redis 6379

```
firewall-cmd --zone=public --query-port=6379/tcp
```

2、如果是no-表示关闭，yes-表示开启

```
[root@localhost ~]# firewall-cmd --zone=public --query-port=6379/tcp
no
```

3、开启状态

```
firewall-cmd --zone=public --add-port=6379/tcp --permanent
```

4、防火墙重载

```
[root@localhost ~]# firewall-cmd --reload
success
```

5、再次查看端口状态

```
[root@localhost ~]# firewall-cmd --zone=public --query-port=6379/tcp
yes
```

6、可以在客户端随意连接成功。

#### rabbitmq

首先安装lang包，再安装rabbitmq；
