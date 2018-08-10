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
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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
     * @param file
     */
    public void lock(File file) {
        String sourcePath = file.getPath();
        FileUtil.renameFile(file, FileUtil.getFileNameNoEx(FileUtil.getNameFromFile(file)) + ConsValue.LOCK_EXT);
        AESCipher(Cipher.ENCRYPT_MODE, sourcePath, file.getPath(), mSeed);
    }

    public void unlock(File file) {
        String sourcePath = file.getPath();
        FileUtil.renameFile(file, FileUtil.getFileNameNoEx(FileUtil.getNameFromFile(file)) + ConsValue.PIC_EXT);
        AESCipher(Cipher.DECRYPT_MODE, sourcePath, file.getPath(), mSeed);
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

            byte[] rawkey = getRawKey(seed.getBytes("UTF-8"));
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
                | NoSuchPaddingException | NoSuchProviderException e) {
            Logg.i(TAG, e.getMessage());

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
     * @param seed 密钥种子（字节）
     * @return 得到的安全密钥
     */
    private byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        // 获得一个随机数，传入的参数为默认方式。
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        // 设置一个种子,一般是用户设定的密码
        sr.setSeed(seed);
        // 获得一个key生成器（AES加密模式）
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        // 设置密匙长度128位
        keyGen.init(128, sr);
        // 获得密匙
        SecretKey key = keyGen.generateKey();
        // 返回密匙的byte数组供加解密使用
        return key.getEncoded();
    }


}
