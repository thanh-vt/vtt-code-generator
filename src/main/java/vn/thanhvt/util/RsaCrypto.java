package vn.thanhvt.util;


import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Log4j2
@SuppressWarnings("java:S5542")
public class RsaCrypto {

    private RsaCrypto() {
    }

//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }

    /**
     * Gen RSA couple key
     *
     * @param keySize
     * @return [] key: [0] = public key, [1] = private key
     */
    public static String[] genRSAKey(int keySize)
        throws NoSuchAlgorithmException {
        String[] array = new String[2];
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        kpg.initialize(keySize, new SecureRandom());
        KeyPair keys = kpg.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keys.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keys.getPublic();
        array[0] = genKey(publicKey);
        array[1] = genKey(privateKey);
        return array;
    }

    static String genKey(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null.");
        }
        byte[] bKeyEncoded = key.getEncoded();
        byte[] b = dertostring(bKeyEncoded);
        return new String(b);
    }

    private static byte[] dertostring(byte[] bytes) {
        ByteArrayOutputStream pemStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(pemStream);

        byte[] stringBytes = encodeBase64(bytes).getBytes(StandardCharsets.UTF_8);
        String encoded = new String(stringBytes);
        encoded = encoded.replace("\r", "");
        encoded = encoded.replace("\n", "");

        int i = 0;
        while ((i + 1) * 64 <= encoded.length()) {
            writer.print(encoded.substring(i * 64, (i + 1) * 64));
            i++;
        }
        if (encoded.length() % 64 != 0) {
            writer.print(encoded.substring(i * 64));
        }
        writer.flush();
        return pemStream.toByteArray();
    }

    /**
     * Encrypt data
     *
     * @param dataToEncrypt
     * @param pubCer
     * @param isFile
     * @return
     * @throws Exception
     */
//    public static String encrypt(String dataToEncrypt, String pubCer, Boolean isFile)
//        throws InvalidKeyException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
//        RSAPublicKey rsaPublicKey = loadPublicKey(pubCer, isFile);
//        Cipher cipher
//            = Cipher.getInstance(Constant.CIPHER_INSTANCE);
//
//        if (rsaPublicKey == null) {
//            return null;
//        }
//
//        cipher.init(1, rsaPublicKey);
//
//        int keySize = rsaPublicKey.getModulus().bitLength() / 8;
//        int maxLength = keySize - 42;
//
//        byte[] bytes = dataToEncrypt.getBytes(StandardCharsets.UTF_8);
//
//        int dataLength = bytes.length;
//        int iterations = dataLength / maxLength;
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i <= iterations; i++) {
//            byte[] tempBytes = new byte[Math.min(dataLength - maxLength * i, maxLength)];
//            System.arraycopy(bytes, maxLength * i, tempBytes, 0,
//                tempBytes.length);
//            byte[] encryptedBytes = cipher.doFinal(tempBytes);
//            sb.append(encodeBase64(reverse(encryptedBytes)));
//        }
//
//        String sEncrypted = sb.toString();
//        sEncrypted = sEncrypted.replace("\r", "");
//        sEncrypted = sEncrypted.replace("\n", "");
//        return sEncrypted;
//    }

    /**
     * verify data
     *
     * @param dataToVerify
     * @param signedData
     * @param pubCer
     * @param isFile
     * @return
     * @throws Exception
     */
    public static boolean verify(String dataToVerify, String signedData, String pubCer, Boolean isFile)
        throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        long startTime = System.currentTimeMillis();
        RSAPublicKey rsaPublicKey = loadPublicKey(pubCer, isFile);
        Signature signature = Signature.getInstance("SHA512withRSA");
        signature.initVerify(rsaPublicKey);
        signature.update(dataToVerify.getBytes(StandardCharsets.UTF_8), 0,
            dataToVerify.getBytes(StandardCharsets.UTF_8).length);
        byte[] bSign = decodeBase64(signedData);
        boolean pass = signature.verify(bSign);
        log.info("Verify signature by RSA running time {} ms", System.currentTimeMillis() - startTime);
        return pass;
    }

    /**
     * Sign data
     *
     * @param dataToSign
     * @param privateKey
     * @param isFile
     * @return
     * @throws Exception
     */
    public static String sign(String dataToSign, String privateKey, Boolean isFile)
        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        long startTime = System.currentTimeMillis();
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey, isFile);
        Signature signature = Signature.getInstance("SHA512withRSA");
        signature.initSign(rsaPrivateKey);
        signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
        byte[] bSigned = signature.sign();
        String sResult = encodeBase64(bSigned);
        log.info("Sign message by RSA running time {} ms", System.currentTimeMillis() - startTime);
        return sResult;
    }

    /**
     * encrypt data
     *
     * @param dataOriginal
     * @param publicKey
     * @param isFile
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public static String encrypt(String dataOriginal, String publicKey, Boolean isFile) {
        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey, isFile);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] secretMessageBytesOriginal = dataOriginal.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = cipher.doFinal(secretMessageBytesOriginal);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    /**
     * decrypt data
     *
     * @param dataEncrypted
     * @param privateKey
     * @param isFile
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public static String decrypt(String dataEncrypted, String privateKey, Boolean isFile) {
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey, isFile);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] secretMessageBytesDecrypted = Base64.getDecoder().decode(dataEncrypted);
        byte[] decryptedMessageBytes = cipher.doFinal(secretMessageBytesDecrypted);
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    /**
     * load private key from key string if load key from file: isFile = true,
     * else isFile = false
     *
     * @param key
     * @param isFile
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static RSAPrivateKey loadPrivateKey(String key, Boolean isFile)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String sReadFile;
        if (isFile.booleanValue()) {
            File file = new File(key);
            sReadFile = fullyReadFile(file);
        } else {
            sReadFile = key.trim();
        }
        if ((sReadFile.startsWith("-----BEGIN PRIVATE KEY-----"))
            && (sReadFile.endsWith("-----END PRIVATE KEY-----"))) {
            sReadFile = sReadFile.replace("-----BEGIN PRIVATE KEY-----", "");
            sReadFile = sReadFile.replace("-----END PRIVATE KEY-----", "");
            sReadFile = sReadFile.replace("\n", "");
            sReadFile = sReadFile.replace("\r", "");
            sReadFile = sReadFile.replace(" ", "");
        }
        byte[] b = decodeBase64(sReadFile);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);

        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    /**
     * load public key from key string
     *
     * @param pubCer
     * @param isFile
     * @return
     * @throws IOException
     */
    private static RSAPublicKey loadPublicKey(String pubCer, boolean isFile)
        throws IOException {

        String sReadFile;
        if (isFile) {
            File file = new File(pubCer);
            sReadFile = fullyReadFile(file);
        } else {
            sReadFile = pubCer.trim();
        }
        if ((sReadFile.startsWith("-----BEGIN PUBLIC KEY-----"))
            && (sReadFile.endsWith("-----END PUBLIC KEY-----"))) {
            sReadFile = sReadFile.replace("-----BEGIN PUBLIC KEY-----", "");
            sReadFile = sReadFile.replace("-----END PUBLIC KEY-----", "");
            sReadFile = sReadFile.replace("\n", "");
            sReadFile = sReadFile.replace("\r", "");
            sReadFile = sReadFile.replace(" ", "");
        }

        RSAPublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(sReadFile));
            publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            log.info("LoadPublicKey : ", e);
        }
        return publicKey;
    }

    private static String fullyReadFile(File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            byte[] bytesOfFile = new byte[(int) file.length()];
            dis.readFully(bytesOfFile);
            String sRead = new String(bytesOfFile);
            return sRead.trim();
        }
    }

    private static String encodeBase64(byte[] dataToEncode) {
        return new String(Base64.getEncoder().encode(dataToEncode));
    }

    private static byte[] decodeBase64(String dataToDecode) {
        return Base64.getDecoder().decode(dataToDecode);
    }

    private static byte[] reverse(byte[] b) {
        int left = 0;
        int right = b.length - 1;

        while (left < right) {
            byte temp = b[left];
            b[left] = b[right];
            b[right] = temp;

            left++;
            right--;
        }
        return b;
    }

//    public static String nativeSign(String dataToSign, String privateKey, boolean isFile)
//        throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, CryptoException {
//        long startTime = System.currentTimeMillis();
//        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey, isFile);
//        AsymmetricKeyParameter asymmetricKeyParameter = PrivateKeyFactory
//            .createKey(rsaPrivateKey.getEncoded());
//        RSANativeSigner raANativeSigner = new RSANativeSigner(new SHA512Digest());
//        raANativeSigner.init(true, asymmetricKeyParameter);
//        byte[] bSigned = raANativeSigner.generateSignature(dataToSign.getBytes(StandardCharsets.UTF_8));
//        String sResult = encodeBase64(bSigned);
//        log.info("Sign message by RSA Native running time {} ms", System.currentTimeMillis() - startTime);
//        return sResult;
//    }
//
//    public static boolean nativeVerify(String dataToVerify, String signedData, String publicKey,
//        boolean isFile)
//        throws IOException {
//        long startTime = System.currentTimeMillis();
//        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey, isFile);
//        if (rsaPublicKey != null) {
//            AsymmetricKeyParameter asymmetricKeyParameter = PublicKeyFactory
//                .createKey(rsaPublicKey.getEncoded());
//            RSANativeSigner raANativeSigner = new RSANativeSigner(new SHA512Digest());
//            raANativeSigner.init(false, asymmetricKeyParameter);
//            byte[] bSign = decodeBase64(signedData);
//            boolean pass = raANativeSigner.verifySignature(bSign, dataToVerify.getBytes(StandardCharsets.UTF_8));
//            log.info("Verify signature by RSA Native running time {} ms",
//                System.currentTimeMillis() - startTime);
//            return pass;
//        }
//        return false;
//    }

}
