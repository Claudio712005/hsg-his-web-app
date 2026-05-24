package br.com.hsg.service.crypto;

import javax.ejb.Stateless;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

@Stateless
public class CarteirinhaCryptoService {

    private static final Logger LOG = Logger.getLogger(CarteirinhaCryptoService.class.getName());
    private static final String ENV_KEY = "CARTEIRINHA_ENCRYPTION_KEY";
    private static final String DEV_FALLBACK = "HSG_DEV_CART_PLACEHOLDER_32CHARS_";

    public String hash(String numero) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] raw = md.digest(numero.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular hash da carteirinha.", e);
        }
    }

    public String encrypt(String numero) {
        try {
            byte[] key    = resolverChave();
            byte[] iv     = new byte[16];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] enc     = cipher.doFinal(numero.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[16 + enc.length];
            System.arraycopy(iv, 0, payload, 0, 16);
            System.arraycopy(enc, 0, payload, 16, enc.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar carteirinha.", e);
        }
    }

    public String decrypt(String payloadBase64) {
        try {
            byte[] key     = resolverChave();
            byte[] payload = Base64.getDecoder().decode(payloadBase64);
            byte[] iv      = Arrays.copyOfRange(payload, 0, 16);
            byte[] enc     = Arrays.copyOfRange(payload, 16, payload.length);
            Cipher cipher  = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            return new String(cipher.doFinal(enc), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar carteirinha.", e);
        }
    }

    public String mascarar(String numero) {
        if (numero == null) return null;
        String limpo = numero.trim();
        if (limpo.length() <= 4) {
            return "****";
        }
        return "****" + limpo.substring(limpo.length() - 4);
    }

    private byte[] resolverChave() {
        String k = System.getenv(ENV_KEY);
        if (k == null || k.trim().isEmpty()) {
            LOG.warning("[CarteirinhaCryptoService] CARTEIRINHA_ENCRYPTION_KEY ausente — usando chave de DEV. Nunca use em produção!");
            k = DEV_FALLBACK;
        }
        return Arrays.copyOf(k.getBytes(StandardCharsets.UTF_8), 32);
    }
}
