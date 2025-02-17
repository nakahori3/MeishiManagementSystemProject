package com.example.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Security;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.*;

public class PGPUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] encrypt(byte[] data, String key) throws PGPException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteArrayOutputStream);
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
                new JcePBEDataEncryptorBuilder(PGPEncryptedData.CAST5).setProvider("BC"));
        encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(key.getBytes(StandardCharsets.UTF_8))
                .setProvider("BC"));

        try (OutputStream encryptionStream = encryptedDataGenerator.open(armoredOutputStream, data.length)) {
            encryptionStream.write(data);
        }

        armoredOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decrypt(byte[] data, String key) throws PGPException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory(byteArrayInputStream, new JcaKeyFingerprintCalculator());
        PGPEncryptedDataList encryptedDataList = (PGPEncryptedDataList) pgpObjectFactory.nextObject();
        PGPPublicKeyEncryptedData encryptedData = (PGPPublicKeyEncryptedData) encryptedDataList.get(0);
        PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(key.toCharArray());

        InputStream decryptedData = encryptedData.getDataStream(decryptor);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ch;
        while ((ch = decryptedData.read()) >= 0) {
            byteArrayOutputStream.write(ch);
        }

        decryptedData.close();
        return byteArrayOutputStream.toByteArray();
    }
}
