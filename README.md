# Ignite-monitor-sample
Apache Ignite 监控示例程序
基于Apache Ignite Restful接口开发，采集CPU,HEAP和JOB指标数据（可可充）发送到Graphite实现集群各节点监控图形展示。
**使用说明**

1 Apache Ignite启动前打开RESTful服务

2 安装Graphite,推荐方法：
docker run -d\
 --name graphite\
 --restart=always\
 -p 80:80\
 -p 2003-2004:2003-2004\
 -p 2023-2024:2023-2024\
 -p 8125:8125/udp\
 -p 8126:8126\
 graphiteapp/graphite-statsd

3 修改相关配置见文件application.properties

4 运行SpringBoot服务启动文件Startup 
 
