package com.heilou.iot.service.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.heilou.common.utils.RsaKeyUtil;
import com.heilou.iot.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @description: auth
 * @author heilou
 * @date 2021/4/2 10:17
 * @version 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public boolean checkValid(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return false;
        }
        RSA rsa = new RSA(RsaKeyUtil.getRSAPrivateKey(),null);
        String value = rsa.encryptBcd(username, KeyType.PrivateKey);
        return value.equals(password) ? true : false;
    }

}
