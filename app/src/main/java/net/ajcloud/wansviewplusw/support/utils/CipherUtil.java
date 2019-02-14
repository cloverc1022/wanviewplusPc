package net.ajcloud.wansviewplusw.support.utils;

import net.ajcloud.wansviewplusw.support.jnacl.NaCl;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static net.ajcloud.wansviewplusw.support.jnacl.curve25519xsalsa20poly1305.crypto_secretbox_NONCEBYTES;

/**
 * Created by mamengchao on 2018/05/23.
 * 加解密
 */
public class CipherUtil {

    private static String TAG = "CipherUtil";
    private static int SALT_LENGTH = 12;
    public static String PUBLICKEY = "0cba66066896ffb51e92bc3c36ffa627c2493770d9b0b4368a2466c801b0184e";
    public static String PRIVATEKEY = "176970653848be5242059e2308dfa30245b93a13befd2ebd09f09b971273b728";

    //uac

    /**
     * 生成随机nonce
     */
    public static byte[] getNonce() {
        byte[] nonce = new byte[crypto_secretbox_NONCEBYTES];
        Random random = new Random();
        for (int i = 0; i < nonce.length; i++) {
            nonce[i] = (byte) random.nextInt(256);
        }
        return nonce;
    }

    public static String naclEncode(String text, String privateKey, String publicKey, byte[] nonce) {
        String encodeMsgContent = null;
        try {
            byte[] encryptPrivateKey = Base64.getDecoder().decode(privateKey);
            byte[] encryptPublicKey = Base64.getDecoder().decode(publicKey);
            byte[] ciphertext = NaCl.encrypt(text.getBytes(), nonce, encryptPublicKey, encryptPrivateKey);
            ciphertext = clearZero(ciphertext);
            encodeMsgContent = new String(Base64.getEncoder().encode(ciphertext), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return encodeMsgContent;
        }
    }

    private static byte[] clearZero(byte[] oldArr) {
        List<Byte> list = new ArrayList<>();
        boolean isBefore = true;
        for (int i = 0; i < oldArr.length; i++) {
            if (i == 0) {
                isBefore = oldArr[i] == 0;
            }
            if (isBefore) {
                if (oldArr[i] != 0) {
                    list.add(oldArr[i]);
                    isBefore = false;
                }
            } else {
                list.add(oldArr[i]);
            }
        }
        byte newArr[] = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArr[i] = list.get(i);
        }
        return newArr;
    }

    public static String naclDecode(String text, String privateKey, String publicKey, byte[] nonce) {
        String decodeMsgContent = null;
        try {
            byte[] decryptPrivateKey = Base64.getDecoder().decode(privateKey);
            byte[] decryptPublicKey = Base64.getDecoder().decode(publicKey);
            byte[] decryptValue = Base64.getDecoder().decode(text);
            decodeMsgContent = new String(NaCl.decrypt(decryptValue, nonce, decryptPublicKey, decryptPrivateKey), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return decodeMsgContent;
        }
    }

    //local
    public static String md5Encode(String strIN, String salt) {
        String msg = strIN + salt;
        MessageDigest alg;
        try {
            alg = MessageDigest.getInstance("MD5");
            alg.update(msg.getBytes());
            byte[] bytes = alg.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String temp = Integer.toHexString(0xFF & bytes[i]);
                if (temp.length() == 1) {
                    hexString.append("0");
                    hexString.append(temp);
                } else if (temp.length() == 2) {
                    hexString.append(temp);
                } else {
                    WLog.w("MD5Util", "error str:" + msg);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            WLog.w(TAG, e.getMessage());
        }
        return msg;
    }

    public static String getRandomSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltByte = new byte[crypto_secretbox_NONCEBYTES];
        secureRandom.nextBytes(saltByte);
        return Base64.getEncoder().encodeToString(saltByte);
    }

    //本地nacl加密
    public static String naclEncodeLocal(String text, String salt) {
        try {
            byte[] publicKey = PUBLICKEY.getBytes();
            byte[] privateKey = PRIVATEKEY.getBytes();
            byte[] nonce = Base64.getDecoder().decode(salt);
//            return new String(NaCl.encrypt(text.getBytes(), nonce, publicKey, privateKey));
            return new String(Base64.getEncoder().encode(NaCl.encrypt(text.getBytes(), nonce, publicKey, privateKey)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //本地nacl解密
    public static String naclDecodeLocal(String text, String salt) {
        try {
            byte[] publicKey = PUBLICKEY.getBytes();
            byte[] privateKey = PRIVATEKEY.getBytes();
            byte[] nonce = Base64.getDecoder().decode(salt);
            return new String(NaCl.decrypt(Base64.getDecoder().decode(text), nonce, publicKey, privateKey), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取Api请求中需要的签名sign字段
     */
    public static String getClondApiSign(String key, String data) {
        String signature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(
                    key.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);
            signature = urlSafe_base64(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            WLog.w(TAG, "Hash algorithm SHA-1 is not supported", e);
        } catch (UnsupportedEncodingException e) {
            WLog.w(TAG, "Encoding UTF-8 is not supported", e);
        } catch (InvalidKeyException e) {
            WLog.w(TAG, "Invalid key", e);
        }
        return signature;
    }

    /**
     * sha256加密
     */
    public static String getSha256(String str) {
        if (null == str || 0 == str.length()) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA-256");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转hex字符串
     */
    public static String strToHex(String str) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, str.getBytes("UTF-8")));
    }

    /**
     * byte[]转hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }

    /**
     * urlsafe-base64
     */
    public static String urlSafe_base64(byte[] bytes) {
        String result = null;
        try {
            result = new String(Base64.getEncoder().encode(bytes), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result
                .replace("+", "-")
                .replace("/", "_")
                .replace("=", "");
    }

    /**
     * sha1加密
     */
    public static String getSha1(InputStream in) {
        MessageDigest messageDigest;
        InputStream inputStream = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            inputStream = new BufferedInputStream(in);
            byte[] buffer = new byte[8192];
            int len = inputStream.read(buffer);

            while (len != -1) {
                messageDigest.update(buffer, 0, len);
                len = inputStream.read(buffer);
            }

            return bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
