package com.gewuyou.baseforge.autoconfigure.util;


import com.gewuyou.baseforge.autoconfigure.util.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Http工具
 *
 * @author gewuyou
 * @since 2024-10-04 23:08:23
 */
@Slf4j
public class HttpUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private HttpUtil() {
    }

    /**
     * 发送GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @return HttpResponse<String>
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    public static HttpResponse<String> sendGet(String url, Map<String, String> headers, Map<String, Object> params) throws IOException, InterruptedException {
        HttpRequest request;
        if (!CollectionUtils.isEmpty(params)) {
            url = buildUrlWithParams(url, params);
        }
        if (!CollectionUtils.isEmpty(headers)) {
            String[] headerArr = headers.entrySet()
                    .stream()
                    // 将键值对转换为键、值的流
                    .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                    // 收集为字符串数组
                    .toArray(String[]::new);
            request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(url))
                    .headers(headerArr)
                    .GET()
                    .build();
        } else {
            request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        }
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 发送POST请求
     * @param url 请求地址
     * @param headers 请求头
     * @param requestBody 请求体
     * @return HttpResponse<String>
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    public static HttpResponse<String> sendPost(String url, Map<String, String> headers, HttpRequest.BodyPublisher requestBody) throws IOException, InterruptedException {
        HttpRequest request;
        if (!CollectionUtils.isEmpty(headers)) {
            String[] headerArr = headers.entrySet()
                    .stream()
                    // 将键值对转换为键、值的流
                    .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                    // 收集为字符串数组
                    .toArray(String[]::new);
            request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(url))
                    .headers(headerArr)
                    .POST(requestBody)
                    .build();
        } else {
            request = HttpRequest.newBuilder().uri(URI.create(url)).POST(requestBody).build();
        }
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 构建带参数的URL
     * @param baseUrl 基础URL
     * @param params 请求参数
     * @return 带参数的URL
     */
    private static String buildUrlWithParams(String baseUrl, Map<String, Object> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (params != null && !params.isEmpty()) {
            splicingParams(params, urlBuilder);
        }
        return urlBuilder.toString();
    }

    /**
     * 拼接参数到URL中
     * @param params 请求参数
     * @param urlBuilder URL构建器
     */
    private static void splicingParams(Map<String, Object> params, StringBuilder urlBuilder) {
        urlBuilder.append("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8);
                urlBuilder.append(key).append("=").append(value).append("&");
            } catch (Exception e) {
                throw new UtilException("URL参数编码失败");
            }
        }
        // 去掉最后一个多余的 &
        urlBuilder.setLength(urlBuilder.length() - 1);
    }
}
