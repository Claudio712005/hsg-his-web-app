package br.com.hsg.service.impl.storage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class StorageGuard {

    public static final long DEFAULT_MAX_BYTES = 20L * 1024 * 1024;

    public static final String CT_PDF       = "application/pdf";
    public static final String CT_JPEG      = "image/jpeg";
    public static final String CT_PNG       = "image/png";
    public static final String CT_WEBP      = "image/webp";

    public static final Set<String> WHITELIST_CONTENT_TYPE;
    static {
        Set<String> s = new HashSet<>();
        s.add(CT_PDF);
        s.add(CT_JPEG);
        s.add(CT_PNG);
        s.add(CT_WEBP);
        WHITELIST_CONTENT_TYPE = Collections.unmodifiableSet(s);
    }

    private static final int MAX_FILENAME_LEN = 255;

    private StorageGuard() {}

    public static void validarContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Content-Type é obrigatório.");
        }
        String ct = contentType.trim().toLowerCase(Locale.ROOT);
        if (!WHITELIST_CONTENT_TYPE.contains(ct)) {
            throw new IllegalArgumentException("Content-Type não permitido: " + contentType);
        }
    }

    public static void validarTamanho(long size, long maxBytes) {
        if (size <= 0L) {
            throw new IllegalArgumentException("Tamanho do arquivo deve ser positivo.");
        }
        if (size > maxBytes) {
            throw new IllegalArgumentException(
                    "Arquivo excede o tamanho máximo permitido (" + maxBytes + " bytes).");
        }
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório.");
        }
        String base = filename.trim();
        int slash = Math.max(base.lastIndexOf('/'), base.lastIndexOf('\\'));
        if (slash >= 0) base = base.substring(slash + 1);
        base = base.replaceAll("[\\x00-\\x1f]", "");
        base = base.replaceAll("\\.\\.+", ".");
        base = base.replaceAll("[^A-Za-z0-9._-]", "_");
        while (base.startsWith(".")) base = base.substring(1);
        if (base.isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido após sanitização.");
        }
        if (base.length() > MAX_FILENAME_LEN) {
            base = base.substring(0, MAX_FILENAME_LEN);
        }
        return base;
    }

    public static void validarMagicBytes(byte[] head, String declaredContentType) {
        if (head == null || head.length < 4) {
            throw new IllegalArgumentException("Não foi possível ler o cabeçalho do arquivo.");
        }
        String ct = declaredContentType == null ? "" : declaredContentType.trim().toLowerCase(Locale.ROOT);
        boolean ok;
        if (CT_PDF.equals(ct)) {
            ok = head.length >= 5
                    && head[0] == 0x25 && head[1] == 0x50 && head[2] == 0x44
                    && head[3] == 0x46 && head[4] == 0x2D;
        } else if (CT_JPEG.equals(ct)) {
            ok = (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF;
        } else if (CT_PNG.equals(ct)) {
            ok = head.length >= 8
                    && (head[0] & 0xFF) == 0x89 && head[1] == 0x50 && head[2] == 0x4E && head[3] == 0x47
                    && head[4] == 0x0D && head[5] == 0x0A && head[6] == 0x1A && head[7] == 0x0A;
        } else if (CT_WEBP.equals(ct)) {
            ok = head.length >= 12
                    && head[0] == 0x52 && head[1] == 0x49 && head[2] == 0x46 && head[3] == 0x46
                    && head[8] == 0x57 && head[9] == 0x45 && head[10] == 0x42 && head[11] == 0x50;
        } else {
            throw new IllegalArgumentException("Content-Type sem verificação de assinatura: " + declaredContentType);
        }
        if (!ok) {
            throw new IllegalArgumentException(
                    "Cabeçalho do arquivo não corresponde ao Content-Type declarado (" + declaredContentType + ").");
        }
    }

    public static void validar(String filename, String contentType, long size, long maxBytes, byte[] head) {
        validarContentType(contentType);
        validarTamanho(size, maxBytes);
        sanitizeFilename(filename);
        validarMagicBytes(head, contentType);
    }
}
