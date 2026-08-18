package com.basilisk.storage.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "basilisk.storage")
public class StorageProperties {

    /** Tipo de provider ativo. Padrão: LOCAL. */
    private ProviderType provider = ProviderType.LOCAL;

    private Local local = new Local();

    public ProviderType getProvider() { return provider; }
    public void setProvider(ProviderType provider) { this.provider = provider; }

    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    public enum ProviderType {
        LOCAL
    }

    public static class Local {

        /** Diretório base para armazenamento local. Padrão: ./uploads */
        private String baseDir = "./uploads";

        public String getBaseDir() { return baseDir; }
        public void setBaseDir(String baseDir) { this.baseDir = baseDir; }
    }
}
