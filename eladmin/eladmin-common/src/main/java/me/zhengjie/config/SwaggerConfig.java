/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * api页面 /doc.html
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Configuration
public class SwaggerConfig {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${swagger.enabled}")
    private Boolean enabled;
//    @Bean
//    @SuppressWarnings("all")
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .enable(enabled)
//                .pathMapping("/")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(PathSelectors.regex("^(?!/error).*"))
//                .paths(PathSelectors.any())
//                .build()
//                //添加登陆认证
//                .securitySchemes(securitySchemes())
//                .securityContexts(securityContexts());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .description("一个简单且易上手的 Spring boot 后台管理框架")
//                .title("ELADMIN 接口文档")
//                .version("1.1")
//                .build();
//    }
//
//    private List<SecurityScheme> securitySchemes() {
//        //设置请求头信息
//        List<SecurityScheme> securitySchemes = new ArrayList<>();
//        ApiKey apiKey = new ApiKey(tokenHeader, tokenHeader, "header");
//        securitySchemes.add(apiKey);
//        return securitySchemes;
//    }
//
//    private List<SecurityContext> securityContexts() {
//        //设置需要登录认证的路径
//        List<SecurityContext> securityContexts = new ArrayList<>();
//        securityContexts.add(getContextByPath());
//        return securityContexts;
//    }
//
//    private SecurityContext getContextByPath() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                // 表示 /auth/code、/auth/login 接口不需要使用securitySchemes即不需要带token
//                .operationSelector(o->o.requestMappingPattern().matches("^(?!/auth/code|/auth/login).*$"))
//                .build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        List<SecurityReference> securityReferences = new ArrayList<>();
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        securityReferences.add(new SecurityReference(tokenHeader, authorizationScopes));
//        return securityReferences;
//    }

    /**
     * 根据@Tag 上的排序，写入x-order
     *
     * @return the global open api customizer
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            if(openApi.getPaths()!=null){
                openApi.getPaths().forEach((s, pathItem) -> {
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
                    });
                });
            }

        };
    }

    //@Bean
    public GroupedOpenApi userApi(){
        String[] paths = { "/**" };
        String[] packagedToMatch = { "me.zhengjie.modules.system.rest" };
        return GroupedOpenApi.builder().group("用户模块")
                .pathsToMatch(paths)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    return operation.addParametersItem(new HeaderParameter().name("groupCode").example("测试").description("集团code").schema(new StringSchema()._default("BR").name("groupCode").description("集团code")));
                })
                .packagesToScan(packagedToMatch).build();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API")
                        .version("1.0")
//                        .description( "Knife4j集成springdoc-openapi示例")
//                        .termsOfService("http://doc.xiaominfo.com")
//                        .license(new License().name("Apache 2.0")
//                                .url("http://doc.xiaominfo.com"))
                ).addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION,new SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION).type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }

}
