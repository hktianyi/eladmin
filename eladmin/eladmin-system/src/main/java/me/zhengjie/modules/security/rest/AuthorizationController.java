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
package me.zhengjie.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.wf.captcha.base.Captcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousDeleteMapping;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.config.RsaProperties;
import me.zhengjie.modules.security.config.bean.LoginCodeEnum;
import me.zhengjie.modules.security.config.bean.LoginProperties;
import me.zhengjie.modules.security.config.bean.SecurityProperties;
import me.zhengjie.modules.security.security.TokenProvider;
import me.zhengjie.modules.security.service.OnlineUserService;
import me.zhengjie.modules.security.service.dto.AuthUserDto;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.RsaUtils;
import me.zhengjie.utils.SMSUtil;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "登录授权接口")
public class AuthorizationController {
    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDetailsService userDetailsService;
    @Resource
    private LoginProperties loginProperties;

    @Log("用户登录")
    @Operation(summary = "登录授权", description = "用户名密码登录时(type=0)，正常传username、password；\n使用手机号登录时(type=1)，username传手机号，code传验证码；\n使用手机号快捷登录时，password需传入“type+phone+key”的MD5值。")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        Authentication authentication = null;
        if (authUser.getType() == 0) {
            String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
            // 查询验证码
            if (loginProperties.isCheckCode()) {
                String code = (String) redisUtils.get(authUser.getUuid());
                // 清除验证码
                redisUtils.del(authUser.getUuid());
                if (StringUtils.isBlank(code)) {
                    return ResponseEntity.badRequest().body("验证码不存在或已过期");
                }
                if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
                    return ResponseEntity.badRequest().body("验证码错误");
                }
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } else if (authUser.getType() == 1) {
            String sendKey = "smsLogin:" + authUser.getUsername();
            String code = (String) redisUtils.get(sendKey);
            // FIXME 上线时移除
            if ("202406".equals(authUser.getCode())) log.warn("测试验证码202406，直接通过"); else
            if (StringUtils.isBlank(code)) {
                return ResponseEntity.badRequest().body("验证码不存在或已过期");
            } else if (!code.equals(authUser.getCode())) {
                return ResponseEntity.badRequest().body("验证码错误");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(authUser.getUsername());
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else if (authUser.getType() == 2) {
            String password = new Digester(DigestAlgorithm.MD5).digestHex(authUser.getType() + authUser.getUsername() + "202406");
            if (!password.equalsIgnoreCase(authUser.getPassword())) {
                return ResponseEntity.badRequest().body("登录无效");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(authUser.getUsername());
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            return ResponseEntity.badRequest().body("登录类型不支持");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前已经登录的token
            onlineUserService.kickOutForUsername(authUser.getUsername());
        }
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回登录信息
        return ResponseEntity.ok(authInfo);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<UserDetails> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }

    @Operation(summary = "获取验证码", hidden = true)
    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @Operation(summary = "获取短信验证码")
    @AnonymousGetMapping(value = "/smsCode")
    public ResponseEntity<Object> getSmsCode(String phone) {
        String sendRetryKey = "sendRetry:" + phone;
        if (redisUtils.get(sendRetryKey) != null) {
            Map<String, Object> result = new HashMap<>(2) {{
                put("code", "1");
                put("msg", "发送太频繁");
            }};
            return ResponseEntity.ok(result);
        }

        String sendKey = "smsLogin:" + phone;
        String code = (String) redisUtils.get(sendKey);
        if (StringUtils.isEmpty(code)) {
            code = RandomUtil.randomNumbers(6);
        }
        // 保存
        redisUtils.set(phone, code, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        log.debug("发送验证码：{} {}", phone, code);
        SMSUtil.send(phone, code);

        // 验证码信息
        Map<String, Object> result = new HashMap<String, Object>(2) {{
            put("code", 200);
            put("msg", "发送成功");
        }};
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String token = tokenProvider.getToken(request);
        onlineUserService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
