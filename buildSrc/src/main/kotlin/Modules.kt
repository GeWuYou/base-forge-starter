/**
 *集中管理所有子模块路径
 *使用方式：project(Modules.Json.springBootStarter)
 * @since 2025-04-03 09:07:33
 * @author gewuyou
 */
object Modules {
    /**
     * JSON
     * @author gewuyou
     * @date 2025/04/03
     */
    object JSON {
        const val SPRING_BOOT_STARTER = ":base-forge-json-spring-boot-starter"
    }


    /**
     * util
     * @author gewuyou
     * @date 2025/04/03
     */
    object UTIL {
        const val SPRING_BOOT_STARTER = ":base-forge-util-spring-boot-starter"
    }

    /**
     * Mybatis 集成
     * @author gewuyou
     * @date 2025-04-03 09:14:43
     */
    object MYBATIS {
        const val EXTENSION = ":base-forge-mybatis-extension-spring-boot-starter"
    }

    /**
     * Redis 集成模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object REDIS {
        const val SPRING_BOOT_STARTER = ":base-forge-redis-spring-boot-starter"
    }

    /**
     * 国际化(i18n)模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object I18N {
        const val SPRING_BOOT_STARTER = ":base-forge-i18n-spring-boot-starter"
    }

    /**
     * Web 相关模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object WEB {
        const val SPRING_BOOT_STARTER = ":base-forge-web-spring-boot-starter"
        const val AUTOCONFIGURE = ":base-forge-web-spring-boot-starter-autoconfigure"
    }

    /**
     * 异步处理模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object ASYNC {
        const val SPRING_BOOT_STARTER = ":base-forge-async-spring-boot-starter"
    }

    /**
     * 日志模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object LOG {
        const val SPRING_BOOT_STARTER = ":base-forge-log-spring-boot-starter"
        const val AUTOCONFIGURE = ":base-forge-log-spring-boot-starter-autoconfigure"
    }

    /**
     * JWT 认证模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object JWT {
        const val SPRING_BOOT_STARTER = ":base-forge-jwt-spring-boot-starter"
    }

    /**
     * 安全相关模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object SECURITY {
        /**
         * 身份认证模块
         * @author gewuyou
         * @since 2025/04/03
         */
        object Authentication {
            const val SPRING_BOOT_STARTER = ":base-forge-security-authentication-spring-boot-starter"
            const val AUTOCONFIGURE = ":base-forge-security-authentication-spring-boot-starter-autoconfigure"
        }

        /**
         * 授权认证模块
         * @author gewuyou
         * @since 2025/04/03
         */
        object AUTHORIZATION {
            const val SPRING_BOOT_STARTER = ":base-forge-security-authorization-spring-boot-starter"
            const val AUTOCONFIGURE = ":base-forge-security-authorization-spring-boot-starter-autoconfigure"
        }
    }

    /**
     * 核心基础模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object CORE {
        const val SPRING_BOOT_STARTER = ":base-forge-core-spring-boot-starter"
    }

    /**
     * gRPC 集成模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object GRPC {
        const val SPRING_BOOT_STARTER = ":base-forge-grpc-spring-boot-starter"
    }

    /**
     * Feign 客户端模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object FEIGN {
        const val SPRING_BOOT_STARTER = ":base-forge-feign-spring-boot-starter"
    }

    /**
     * 请求追踪模块
     * @author gewuyou
     * @since 2025/04/03
     */
    object TRACE {
        const val SPRING_BOOT_STARTER = ":base-forge-request-trace-spring-boot-starter"
    }

}