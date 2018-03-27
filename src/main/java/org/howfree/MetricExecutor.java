package org.howfree;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 指标采集程序
 * 通过Ignite Restful接口获得性能指标数据，并发送到展示工具Graphite
 * 相关配置见application.properties
 * @Author: Bob Li
 * @Date: 3/27/2018 2:59 PM
 */
@Component
public class MetricExecutor {
    @Autowired
    private RestTemplate restTemplate;

    //Ignite REST接口地址
    @Value("${ignite.rest.url}")
    String url;

    //Graphite IP
    @Value("${graphite.ip}")
    String graphiteIp;

    // 部分指标，详细见接口：ignite?cmd=top&mtr=true
    String[] items = {"heapMemoryInitialized", "heapMemoryUsed", "heapMemoryCommitted", "heapMemoryMaximum",
            "currentActiveJobs",
            "currentWaitingJobs",
            "currentRejectedJobs",
            "currentCancelledJobs",
            "currentJobWaitTime",
            "currentJobExecuteTime", "averageJobExecuteTime", "idleTimePercentage", "currentCpuLoad",
            "nonHeapMemoryInitialized", "nonHeapMemoryUsed", "nonHeapMemoryCommitted", "nonHeapMemoryMaximum"};

    /**
     * 每30秒采集程序执行一次，若大规模集群应考虑多线程优化此程序
     */
    @Scheduled(fixedRate = 30000)
    public void execute() {
        SimpleGraphiteClient graphiteClient = new SimpleGraphiteClient(graphiteIp, 2003);

        String metricStr = restTemplate.getForObject(url, String.class);
        JSONObject metricObject = JSON.parseObject(metricStr);
        //所有节点数据，按节点发送数据到Graphite
        JSONArray nodes = metricObject.getJSONArray("response");
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = (String) node.get("nodeId");
            if (null != nodeId && nodeId.length() > 8)
                nodeId = nodeId.substring(0, 7);

            JSONArray hosts = (JSONArray) node.get("tcpHostNames");

            String hostName = hosts.size() > 0 ? hosts.get(0).toString() : null;
            assert null != hostName;

            JSONObject metrics = node.getJSONObject("metrics");
            if (hostName != null) {
                for (int j = 0; j < items.length; j++) {
                    String metricItem = items[j];
                    Object metricValue = metrics.get(metricItem);

                    //考虑到单主机可能多节点，指标名称应包括主机名，节点ID和指标项
                    String metricName = hostName + "." + nodeId + "." + metricItem;
                    if (metricValue instanceof Long) {
                        graphiteClient.sendMetric(metricName, (Long) metricValue);
                    } else if (metricValue instanceof BigDecimal) {
                        graphiteClient.sendMetric(metricName, (BigDecimal) metricValue);
                    } else if (metricValue instanceof Integer) {
                        graphiteClient.sendMetric(metricName, (Integer) metricValue);
                    } else {
                        //未处理数据类型，应补充处理
                        String className = metricValue.getClass().getName();
                    }
                }
            }
        }
    }

}
