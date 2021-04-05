package com.heilou.common.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;

/**
 * @description: 加密工具类
 * @author heilou
 * @date 2021/4/2 8:44
 * @version 1.0
 */
public class RsaKeyUtil {
    private static RSAPrivateKey privateKey;
    static {
        InputStream resourceAsStream = RsaKeyUtil.class.getClassLoader().getResourceAsStream("keystore/auth-private.key");
        privateKey = IoUtil.readObj(resourceAsStream);
        IoUtil.close(resourceAsStream);
    }

    public static RSAPrivateKey getRSAPrivateKey(){
        return privateKey;
    }

    /**
     * 生成私钥文件
     */
    public static void main(String[] args)  {
        RSA rsa = new RSA(RsaKeyUtil.getRSAPrivateKey(),null);
        String value = rsa.encryptBcd("test", KeyType.PrivateKey);
        System.out.println(value);
//        System.out.println();
//        System.out.print("输入保存密钥文件的路径(如: f:/rsa/): ");
//        Scanner scanner = new Scanner(System.in);
//        String path = scanner.nextLine();
//        KeyPair keyPair = SecureUtil.generateKeyPair("RSA", 512, LocalDateTime.now().toString().getBytes());
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        String privatePath = path + "auth-private.key";
//        String publicKeyString = Base64.encode(publicKey.getEncoded());
//        String privateKeyString = Base64.encode((privateKey.getEncoded()));
//        System.out.println(publicKeyString);
//        System.out.println(privateKeyString);
//        IoUtil.writeObjects(FileUtil.getOutputStream(privatePath), true, privateKey);
    }
}
