
package org.dtls.fairifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RdfSkeletonJsonTransformerImpl implements RdfSkeletonTransformer {

    private static final ObjectMapper mapper = new ObjectMapper();
    private Model model;

    @Override
    public Model read(Path path)
            throws IOException {
        String projectId = path.getFileName().toString().split("\\.")[0];

        this.model = new Model();
        String json = new String(Files.readAllBytes(path));
        this.model.setJson(json);
        Path metadataPath = Paths
                .get(path.getParent().toString() + File.separator + projectId + ".metadata.skeleton.json");
        SkeletonMetadata metadata = mapper.readValue(metadataPath.toFile(), SkeletonMetadata.class);
        this.model.setMetadata(metadata);
        return this.model;
    }

    @Override
    public void write(Model model, Path path)
            throws IOException {
        String projectId = path.getFileName().toString().split("\\.")[0];
        Path metadataPath = Paths
                .get(path.getParent().toString() + File.separator + projectId + ".metadata.skeleton.json");
        try (BufferedWriter writer = Files.newBufferedWriter(metadataPath)) {
            mapper.writeValue(writer, model.getMetadata());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Files.write(path, model.getJson().getBytes(StandardCharsets.UTF_8));
    }
}
