package com.gewuyou.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.util.enums.IPAddressResolutionService;
import com.gewuyou.util.exception.UtilException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;

/**
 * ip工具类
 *
 * @author gewuyou
 * @since 2024-04-17 下午8:33:33
 */
@Slf4j
public class IpUtil {

    //region 响应状态码相关常量
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    private static final String UNKNOWN = "unknown";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    //endregion
    private IpUtil() {
    }

    /**
     * 获取当前网络ip
     *
     * @param request 请求
     * @return ip地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    throw new UtilException("获取本机IP地址失败");
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            int start = ipAddress.indexOf(",");
            //"***.***.***.***".length() = 15
            if (start > 0) {
                ipAddress = ipAddress.substring(0, start);
            }
        }
        return ipAddress;
    }

    public static String getIpSource(String ip) {
        // todo 增加其他查询接口
        String ipSource = "未知";
        // 默认使用IP-API接口查询
        String url = IPAddressResolutionService.IP_API.getUrl() + ip;
        try {
            HttpResponse<String> response = HttpUtil.sendGet(url, null, IPAddressResolutionService.getParams(IPAddressResolutionService.IP_API));
            JsonNode jsonNode = MAPPER.readTree(response.body());
            if (response.statusCode() == 200 && SUCCESS.equals(jsonNode.get("status").asText())) {
                ipSource = jsonNode.get("city").asText();
            }
        } catch (IOException | InterruptedException e) {
            log.error("获取IP来源失败", e);
        }
        return ipSource;
    }
}
