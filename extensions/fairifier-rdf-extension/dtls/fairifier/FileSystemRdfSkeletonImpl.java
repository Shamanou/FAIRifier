package org.dtls.fairifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Optional;

public class FileSystemRdfSkeletonImpl implements RdfSkeletonService {
    private static final Path SAVELOCATION = Paths.get(System.getProperty("user.home") + "/.local/share/openrefine/");
    private static final ObjectMapper mapper = new ObjectMapper();

    
    @Override
    public List<String> listModels(final String fileType)  throws IOException {
        List<String[]> files = Files.list(SAVELOCATION).filter(Files::isRegularFile).map(this::getStringAndFiletype).collect(Collectors.toList());;
        ArrayList<String> outList = new ArrayList<String>();
        for (String[] o : files) {
            if (o[0] != null) { 
                if (o[1].equals(fileType)) {
                    outList.add(o[0]);
                }
            }
        }
        
        System.out.println(outList.size());
        return outList;
    }

    @Override
    public List<String> listModels()  throws IOException {
        ArrayList<String> outList = new ArrayList<String>();
        List<String[]> files = Files.list(SAVELOCATION).filter(Files::isRegularFile).map(this::getStringAndFiletype).collect(Collectors.toList());;
        for (String[] o : files) {
            if (o != null) { 
                outList.add(o[0]);
            }
        }
        return outList;
    }
    
    @Override
    public void saveModel(String json, String fileType, String projectId)  throws IOException {
        Path fileLocation = Paths.get(SAVELOCATION.toString() + File.separator + projectId + ".skeleton.json");
        Files.write(fileLocation, json.getBytes(StandardCharsets.UTF_8));
        try (BufferedWriter writer = Files.newBufferedWriter(fileLocation)) {
            SkeletonMetadata metadata = new SkeletonMetadata();
            metadata.setFileType(fileType);
            mapper.writeValue(writer, metadata);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String loadModel(String projectId)  throws IOException {
        java.util.Optional<String[]> selected = Files.list(SAVELOCATION)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String filename = path.getFileName().toString();
                    String[] tokens = filename.split("\\.");
                    return tokens.length == 3 && tokens[0].equals(projectId);
                })
                .map(this::getStringAndFiletype)
                .findFirst();
        return selected.get()[1];
    }
    
    public String[] getStringAndFiletype(Path element){
        String name = element.getFileName().toString().split("\\.")[0];
        try {
            if (new File(element.getParent() + File.separator + element.getFileName().toString()).isFile() && element.getFileName().toString().contains("metadata")) {
                SkeletonMetadata metadata = mapper.readValue(new File(element.getParent().toString() + File.separator + name + ".metadata.skeleton.json"), SkeletonMetadata.class);
                String[] out = { name, metadata.getFileType() };
                return out;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return null;
    }
}
