package com.basilisk.storage.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basilisk.storage")
public class StorageProperties {

    /** Tipo de provider ativo. Padrão: LOCAL. */
    private ProviderType provider = ProviderType.LOCAL;

    /** Tamanho máximo de arquivo em bytes. Padrão: 10 MB. */
    private long maxFileSize = 10L * 1024 * 1024;

    /** MIME types permitidos, separados por vírgula. */
    private String allowedTypes = "image/jpeg,image/png,image/gif,image/webp,application/pdf,text/csv";

    private Local local = new Local();

    private S3 s3 = new S3();

    public ProviderType getProvider() { return provider; }
    public void setProvider(ProviderType provider) { this.provider = provider; }

    public long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }

    public String getAllowedTypes() { return allowedTypes; }
    public void setAllowedTypes(String allowedTypes) { this.allowedTypes = allowedTypes; }

    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    public S3 getS3() { return s3; }
    public void setS3(S3 s3) { this.s3 = s3; }

    public enum ProviderType {
        LOCAL, S3
    }

    public static class Local {

        /** Diretório base para armazenamento local. Padrão: ./uploads */
        private String baseDir = "./uploads";

        public String getBaseDir() { return baseDir; }
        public void setBaseDir(String baseDir) { this.baseDir = baseDir; }
    }

    public static class S3 {

        private String bucket;
        private String region;
        private String accessKey;
        private String secretKey;

        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    }
}
