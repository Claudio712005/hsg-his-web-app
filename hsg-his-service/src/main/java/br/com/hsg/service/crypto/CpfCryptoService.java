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
public class CpfCryptoService {

    private static final Logger LOG = Logger.getLogger(CpfCryptoService.class.getName());
    private static final String ENV_KEY = "CPF_ENCRYPTION_KEY";
    private static final String DEV_FALLBACK = "HSG_DEV_ONLY_PLACEHOLDER_32CHAR_";

    public String hash(String cpfDigits) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] raw = md.digest(cpfDigits.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : raw) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular hash do CPF.", e);
        }
    }

    public String encrypt(String cpfDigits) {
        try {
            byte[] key    = resolverChave();
            byte[] iv     = new byte[16];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] enc     = cipher.doFinal(cpfDigits.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[16 + enc.length];
            System.arraycopy(iv, 0, payload, 0, 16);
            System.arraycopy(enc, 0, payload, 16, enc.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar CPF.", e);
        }
    }

    private byte[] resolverChave() {
        String k = System.getenv(ENV_KEY);
        if (k == null || k.trim().isEmpty()) {
            LOG.warning("[CpfCryptoService] CPF_ENCRYPTION_KEY ausente — usando chave de DEV. Nunca use em produção!");
            k = DEV_FALLBACK;
        }
        return Arrays.copyOf(k.getBytes(StandardCharsets.UTF_8), 32);
    }
}
