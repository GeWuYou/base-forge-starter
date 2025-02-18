package com.gewuyou.baseforge.security.authentication.autoconfigure.factory

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.autoconfigure.parser.LoginRequestParser
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.ApplicationContext

/**
 *登录请求解析器工厂
 *
 * 该类用于注册和选择合适的登录请求解析器
 * @since 2025-02-15 02:42:33
 * @author gewuyou
 */
class LoginRequestParserFactory(
    private val applicationContext: ApplicationContext
) {
    private val parsers = mutableMapOf<String, LoginRequestParser>()

    init {
        // 从 Spring 上下文获取自定义的解析器，并注册
        registerParsers()
    }

    /**
     * 注册请求解析器
     */
    private fun registerParser(contentType: String, parser: LoginRequestParser) {
        parsers[contentType] = parser
    }

    /**
     * 根据请求获取合适地解析器
     */
    fun getParser(request: HttpServletRequest): LoginRequestParser {
        val contentType = request.contentType ?: throw IllegalArgumentException("Content-Type is missing")
        return parsers[contentType] ?: throw IllegalArgumentException("No parser found for Content-Type: $contentType")
    }

    /**
     * 从 Spring 上下文获取自定义的解析器并注册
     */
    private fun registerParsers() {
        val parsers = applicationContext.getBeansOfType(LoginRequestParser::class.java).toMap()
        for ((_, parser) in parsers) {
            val contentType = parser.getSupportedContentType()
            log.info("为登录请求的内容类型:{} 注册登录请求解析器: {}", contentType, parser::class.java.name)
            registerParser(contentType, parser)
        }
    }
}