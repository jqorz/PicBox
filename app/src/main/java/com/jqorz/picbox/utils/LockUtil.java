package com.jqorz.picbox.utils;

import com.jqorz.picbox.cons.ConsValue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author jqorz
 * @since 2018/8/4
 */
public class LockUtil {
    private static final String TAG = LockUtil.class.getSimpleName();
    private String mSeed = "dfdas7894513xc21asd878ds4c5x1v32df4g56wr7qw89d43c1324165wef4w";

    /**
     * 将文件加密并更改后缀为ConsValue.LOCK_EXT
     */
    public boolean lock(File file) {
        boolean result = AESCipher(Cipher.ENCRYPT_MODE, file.getPath(), file.getPath(), mSeed);
        boolean reNameResult = FileUtil.renameFile(file, FileUtil.getFileNameNoEx(FileUtil.getNameFromFile(file)) + ConsValue.LOCK_EXT);
        return result && reNameResult;
    }

    public boolean unlock(File file) {
        boolean result = AESCipher(Cipher.DECRYPT_MODE, file.getPath(), file.getPath(), mSeed);
        boolean reNameResult = FileUtil.renameFile(file, FileUtil.getFileNameNoEx(FileUtil.getNameFromFile(file)) + ConsValue.PIC_EXT);
        return result && reNameResult;
    }

    public boolean AESCipher(int cipherMode, String sourceFilePath,
                             String targetFilePath, String seed) {
        boolean result = false;
        FileChannel sourceFC = null;
        FileChannel targetFC = null;

        try {

            if (cipherMode != Cipher.ENCRYPT_MODE
                    && cipherMode != Cipher.DECRYPT_MODE) {
                return false;
            }

            Cipher mCipher = Cipher.getInstance("AES/CFB/NoPadding");

            byte[] rawkey = getRawKey(seed);
            File sourceFile = new File(sourceFilePath);
            File targetFile = new File(targetFilePath);

            sourceFC = new RandomAccessFile(sourceFile, "r").getChannel();
            targetFC = new RandomAccessFile(targetFile, "rw").getChannel();

            SecretKeySpec secretKey = new SecretKeySpec(rawkey, "AES");

            mCipher.init(cipherMode, secretKey, new IvParameterSpec(
                    new byte[mCipher.getBlockSize()]));

            ByteBuffer byteData = ByteBuffer.allocate(1024);
            while (sourceFC.read(byteData) != -1) {
                // 通过通道读写交叉进行。
                // 将缓冲区准备为数据传出状态
                byteData.flip();

                byte[] byteList = new byte[byteData.remaining()];
                byteData.get(byteList, 0, byteList.length);
                //此处，若不使用数组加密解密会失败，因为当byteData达不到1024个时，加密方式不同对空白字节的处理也不相同，从而导致成功与失败。
                byte[] bytes = mCipher.doFinal(byteList);
                targetFC.write(ByteBuffer.wrap(bytes));
                byteData.clear();
            }

            result = true;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException
                | NoSuchPaddingException | InvalidKeySpecException e) {
            Logg.e(TAG, e.getMessage());

        } finally {
            try {
                if (sourceFC != null) {
                    sourceFC.close();
                }
                if (targetFC != null) {
                    targetFC.close();
                }
            } catch (IOException e) {
                Logg.i(TAG, e.getMessage());
            }
        }

        return result;
    }

    /**
     * 使用一个安全的随机数来产生一个密匙,密匙加密使用的
     *
     * @param seed 密钥
     * @return 得到的安全密钥
     */
    private byte[] getRawKey(String seed) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // 密钥的比特位数，注意这里是比特位数
        // AES 支持 128、192 和 256 比特长度的密钥
        int keyLength = 256;
        // 盐值的字节数组长度，注意这里是字节数组的长度
        // 其长度值需要和最终输出的密钥字节数组长度一致
        // 由于这里密钥的长度是 256 比特，则最终密钥将以 256/8 = 32 位长度的字节数组存在
        // 所以盐值的字节数组长度也应该是 32

        // 先获取一个随机的盐值
        // 你需要将此次生成的盐值保存到磁盘上下次再从字符串换算密钥时传入
        // 如果盐值不一致将导致换算的密钥值不同
        // 保存密钥的逻辑官方并没写，需要自行实现
//        int saltLength = 32;
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[saltLength];
//        random.nextBytes(salt);

        //为了省事，直接用密码的字节
        byte[] salt = seed.getBytes();
        // 将密码明文、盐值等使用新的方法换算密钥
        int iterationCount = 1000;
        KeySpec keySpec = new PBEKeySpec(seed.toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        // 到这里你就能拿到一个安全的密钥了
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        return key.getEncoded();
    }

}
