package com.devmello.music.util.files;

import com.devmello.music.MusicPlugin;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meteordevelopment.meteorclient.MeteorClient;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class Secrets {

    // Get the UUID of the current logged in minecraft account
    private static final UUID uuid = MeteorClient.mc.getSession().getUuidOrNull();

    private static final SecretKey key = getKeyFromUUID();

    private static JsonObject secrets = new JsonObject();

    private static File secretsFile = new File(MusicPlugin.FOLDER, "secrets.json");

    public static SecretKey getKeyFromUUID() {
        byte[] keyBytes = new byte[16];
        System.arraycopy(uuid.toString().getBytes(), 0, keyBytes, 0, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plaintext) throws Exception {
        // Generate a random IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Create the cipher object
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Encrypt the data
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data for easier decryption
        byte[] encryptedIvAndText = new byte[16 + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, 16);
        System.arraycopy(encrypted, 0, encryptedIvAndText, 16, encrypted.length);

        // Encode to Base64 to get a string representation
        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    public static String decrypt(String encryptedStr) throws Exception {

        // Decode from Base64
        byte[] encryptedIvAndText = Base64.getDecoder().decode(encryptedStr);

        // Extract IV and encrypted text
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[encryptedIvAndText.length - 16];
        System.arraycopy(encryptedIvAndText, 0, iv, 0, 16);
        System.arraycopy(encryptedIvAndText, 16, encrypted, 0, encrypted.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Create the cipher object
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // Decrypt the data
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static boolean create() {
        try {
            return secretsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete() {
        return secretsFile.delete();
    }

    public static boolean exists() {
        return secretsFile.exists();
    }

    public static boolean load() {
        if (!exists()) return false;
        try (FileReader reader = new FileReader(secretsFile)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                secrets = element.getAsJsonObject();
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean save() {
        Gson gson = new Gson();
        try {
            return writeToFile(secretsFile, gson.toJson(secrets));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeToFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean set(String key, String value, boolean save) {
        try {
            if (save) {
                if (set(key, value)) return save();
                return false;
            } else {
                return set(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean set(String key, String value) {
        try {
            secrets.addProperty(key, encrypt(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String get(String key) {
        try {
            return decrypt(secrets.get(key).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
