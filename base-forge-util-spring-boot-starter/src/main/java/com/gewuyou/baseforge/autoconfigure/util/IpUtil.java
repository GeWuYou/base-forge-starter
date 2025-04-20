package com.gewuyou.baseforge.autoconfigure.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.util.enums.IPAddressResolutionService;
import com.gewuyou.baseforge.autoconfigure.util.exception.UtilException;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        String ipAddress = getIpAddressByHeaders(request);
        // 如果是本地地址，获取机器的真实 IP
        ipAddress = getLocalIpAddress(ipAddress);
        // 处理通过多个代理的情况
        ipAddress = getIpAddress(ipAddress);
        return ipAddress;
    }

    /**
     * 获取IP地址
     * @param ipAddress ip地址
     * @return ip地址
     */
    private static String getIpAddress(String ipAddress) {
        if (Objects.nonNull(ipAddress) && ipAddress.length() > 15) {
            int start = ipAddress.indexOf(",");
            if (start > 0) {
                ipAddress = ipAddress.substring(0, start);
            }
        }
        return ipAddress;
    }

    /**
     * 获取本地IP地址
     * @param ipAddress ip地址
     * @return 本地IP地址
     */
    private static String getLocalIpAddress(String ipAddress) {
        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            try {
                InetAddress inet = InetAddress.getLocalHost();
                ipAddress = inet.getHostAddress();
            } catch (UnknownHostException e) {
                throw new UtilException("获取本机IP地址失败");
            }
        }
        return ipAddress;
    }

    /**
     * 获取请求头中的IP地址
     * @param request 请求
     * @return ip地址
     */
    private static String getIpAddressByHeaders(HttpServletRequest request) {
        List<String> ipHeaders = Arrays.asList(
                "x-forwarded-for",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        );
        return ipHeaders.stream()
                .map(request::getHeader)
                .filter(ip -> ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip))
                .findFirst()
                .orElse(request.getRemoteAddr());
    }

    /**
     * 获取IP来源
     * @param ip ip地址
     * @return ip来源
     */
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
        } catch (IOException e) {
            log.error("获取IP来源失败", e);
        } catch (InterruptedException e) {
            log.error("线程被中断", e);
            // 重新设置中断状态
            Thread.currentThread().interrupt();
        }
        return ipSource;
    }
    /**
     * 获取用户代理对象
     *
     * @param request 请求
     * @return {@link UserAgent} 用户代理对象
     */
    public static UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }

}
