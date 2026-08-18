package com.basilisk.storage.provider;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstração para provedores de armazenamento de arquivos.
 * Implementações concretas definem onde e como os arquivos são persistidos
 * (filesystem local, S3, GCS, etc.).
 */
public interface StorageProvider {

    /**
     * Armazena o arquivo no diretório indicado.
     *
     * @param file      arquivo recebido via multipart
     * @param directory subdiretório lógico (ex: "avatars", "documents")
     * @return caminho/chave do arquivo armazenado
     */
    String store(MultipartFile file, String directory);

    /**
     * Carrega o conteúdo do arquivo.
     *
     * @param path caminho/chave retornado por {@link #store}
     * @return bytes do arquivo
     */
    byte[] load(String path);

    /**
     * Remove o arquivo do storage.
     *
     * @param path caminho/chave retornado por {@link #store}
     */
    void delete(String path);

    /**
     * Retorna a URL pública/acessível do arquivo.
     *
     * @param path caminho/chave retornado por {@link #store}
     * @return URL do arquivo
     */
    String getUrl(String path);
}
