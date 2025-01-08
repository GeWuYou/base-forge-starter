package com.gewuyou.baseforge.autoconfigure.util;


import com.gewuyou.baseforge.autoconfigure.i18n.config.I18nAutoConfiguration;
import org.springframework.context.MessageSource;

/**
 * i18消息工具
 *
 * @author gewuyou
 * @since 2024-11-25 17:40:12
 */
public class I18nMessageUtil {
    private I18nMessageUtil() {
    }

    /**
     * 获取i18n消息源
     * @return i18n消息源
     */
    public static MessageSource getI18nMessageSource() {
        return (MessageSource) SpringUtil.getBean(I18nAutoConfiguration.MESSAGE_SOURCE_BEAN_NAME);
    }

    /**
     * 获取自定义的i18n消息源
     * @param messageSourceName 自定义的i18n消息源bean名称
     * @return 自定义的i18n消息源
     */
    public static MessageSource getI18nMessageSource(String messageSourceName) {
        return (MessageSource) SpringUtil.getBean(messageSourceName);
    }
}
