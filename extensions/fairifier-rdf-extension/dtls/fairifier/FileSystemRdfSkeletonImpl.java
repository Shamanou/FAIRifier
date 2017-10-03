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
import java.util.Optional;

public class FileSystemRdfSkeletonImpl implements RdfSkeletonService {
    private static final Path SAVELOCATION = Paths.get(System.getProperty("user.home") + "/.local/share/openrefine/");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final RdfSkeletonTransformer rdfSkeletonJsonTransformerImpl = new  RdfSkeletonJsonTransformerImpl(); 
    
    @Override
    public List<RdfSkeletonTransformer> listModels(final String fileType)  throws IOException {
        return Files.list(SAVELOCATION)
                .filter(Files::isRegularFile)                
                .map(this::getStringAndFiletype)
                .filter(val -> val.getSkeletonMetadata() != null )
                .filter(val -> val.getSkeletonMetadata().getFileType().equals(fileType) )
                .collect(Collectors.toList());
    }

    @Override
    public List<RdfSkeletonTransformer> listModels()  throws IOException {
        return Files.list(SAVELOCATION)
                .filter(Files::isRegularFile)
                .map(this::getStringAndFiletype)
                .filter(val -> val.getSkeletonMetadata() != null )
                .collect(Collectors.toList());
    }
    
    @Override
    public void saveModel(String json, String fileType, String title,String projectId)  throws IOException {   
        Path fileLocation = Paths.get(SAVELOCATION.toString() + File.separator + projectId + ".metadata.skeleton.json");        
        try (BufferedWriter writer = Files.newBufferedWriter(fileLocation)) {
            SkeletonMetadata metadata = new SkeletonMetadata();
            metadata.setFileType(fileType);
            metadata.setTitle(title);
            metadata.setFileName(fileLocation.toString());
            metadata.setProjectId(projectId);
            rdfSkeletonJsonTransformerImpl.setModelMetadata(metadata);
            rdfSkeletonJsonTransformerImpl.transform(json);
            
            mapper.writeValue(writer, metadata);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }      
        json = rdfSkeletonJsonTransformerImpl.getModelAsJsonString();
        fileLocation = Paths.get(SAVELOCATION.toString() + File.separator + projectId + ".skeleton.json");
        Files.write(fileLocation, json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String loadModel(String projectId)  throws IOException {
        String json = new String(
                Files.readAllBytes(
                        Paths.get(SAVELOCATION.toString() + File.separator + projectId + ".skeleton.json" )
                )
        );        
        rdfSkeletonJsonTransformerImpl.transform(json);
        return rdfSkeletonJsonTransformerImpl.getModelAsJsonString();
    }
    
    private RdfSkeletonTransformer getStringAndFiletype(Path element){
        String name = element.getFileName().toString().split("\\.")[0];
        RdfSkeletonTransformer rdfSkeletonJsonTransformerImpl = new  RdfSkeletonJsonTransformerImpl();
        try {
            if (element.getFileName().toString().contains("metadata")) {                
                SkeletonMetadata metadata = mapper.readValue(new File(SAVELOCATION.toString() + File.separator + name + ".metadata.skeleton.json"), SkeletonMetadata.class);
                String json = new String(Files.readAllBytes(Paths.get(SAVELOCATION.toString() + File.separator + name + ".skeleton.json" )));
                
                rdfSkeletonJsonTransformerImpl.setModelMetadata(metadata);
                rdfSkeletonJsonTransformerImpl.transform(json);
                return rdfSkeletonJsonTransformerImpl;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return rdfSkeletonJsonTransformerImpl;
    }
}
