package com.sakiko.service;

import com.sakiko.dto.UserLoginDTO;
import com.sakiko.entity.User;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

}
