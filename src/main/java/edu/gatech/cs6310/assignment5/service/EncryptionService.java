package edu.gatech.cs6310.assignment5.service;

import lombok.SneakyThrows;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import javax.persistence.AttributeConverter;

@Service
//@Configuration
public class EncryptionService implements AttributeConverter<Object, String>{

    @Value("${aes.encryption.key}")
    private String encryptionKey;
    @Value("${aes.encryption.iv}")
    private String ivString;
    private IvParameterSpec iv;
    private final String encryptionAlgo="AES/CBC/PKCS5Padding";
    private Key key;
    private  Cipher cipher;


    private Key getKey() {
        if (key == null)
            key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        return key;
    }

    public IvParameterSpec generateIv() {

        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivString.getBytes());
        return ivParameterSpec;
    }

    private Cipher getCipher() throws GeneralSecurityException {
        if (cipher == null)
            cipher = Cipher.getInstance(encryptionAlgo);
        return cipher;
    }

    private void initCipher(int encryptMode) throws GeneralSecurityException {
        getCipher().init(encryptMode, getKey(), generateIv());
    }

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute==null)
            return null;
        byte[] encryptedString = SerializationUtils.serialize(attribute);;
        initCipher(Cipher.ENCRYPT_MODE);
        encryptedString = getCipher().doFinal(encryptedString);
        String output = Base64.getEncoder().encodeToString(encryptedString);

        return output;
    }

    @SneakyThrows
    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData==null)
            return null;

        initCipher(Cipher.DECRYPT_MODE);
        byte[] decryptedString = getCipher().doFinal(Base64.getDecoder().decode(dbData));
        Object output = SerializationUtils.deserialize(decryptedString);

        return output;
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }



}
