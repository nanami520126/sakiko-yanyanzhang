package com.sakiko.controller.user;

import com.sakiko.constant.JwtClaimsConstant;
import com.sakiko.dto.UserLoginDTO;
import com.sakiko.entity.User;
import com.sakiko.properties.JwtProperties;
import com.sakiko.result.Result;
import com.sakiko.service.UserService;
import com.sakiko.service.impl.UserServiceImpl;
import com.sakiko.utils.JwtUtil;
import com.sakiko.vo.UserLoginVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController("userController")
@RequestMapping("/user/user")
public class UserController {

    @Resource
    UserService userService ;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO ){
        User user = userService.wxLogin(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        redisTemplate.opsForValue().set(
                "login:token:" + token,
                user.getId().toString(),
                jwtProperties.getAdminTtl(),
                TimeUnit.MILLISECONDS);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);

    }

}
