package org.dtls.fairifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
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

public class FileSystemRdfSkeletonImpl implements RdfSkeletonService {
    private static final Path SAVELOCATION = Paths.get(System.getProperty("user.home") + "/.local/share/openrefine/");
    
    @Override
    public List<String> listModels(final String fileType)  throws IOException {
        Stream<Path> files = Files.list(SAVELOCATION);
        
        ArrayList<String> outList = new ArrayList<String>();
        List<String[]> out = files.map(element -> getStringAndFiletype(element)).collect(Collectors.toList());
        for (String[] o : out) {
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
        Stream<Path> files = Files.list(SAVELOCATION);
        ArrayList<String> outList = new ArrayList<String>();
        List<String[]> out = files.map(element -> getStringAndFiletype(element)).collect(Collectors.toList());
        for (String[] o : out) {
            if (o != null) { 
                outList.add(o[0]);
            }
        }
        return outList;
    }
    
    @Override
    public void saveModel(String json, String fileType, String projectId)  throws IOException {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(SAVELOCATION.toString() + File.separator + projectId + ".skeleton.json"));
            out.write(json);
            out.close();
            ObjectMapper mapper = new ObjectMapper();
            SkeletonMetadata metadata = new SkeletonMetadata();
            metadata.setFileType(fileType);
            mapper.writeValue(new File(SAVELOCATION.toString() + File.separator  + projectId + ".metadata.skeleton.json"), metadata);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String loadModel(String projectId)  throws IOException {
        Stream<Path> files = Files.list(SAVELOCATION);
        List<String[]> out = files.map(element -> getStringAndFiletype(element)).collect(Collectors.toList());
        for (String[] o : out) {
            if (o != null) { 
                if(o[0].equals(projectId)) {
                    try {
                        return new String(Files.readAllBytes(Paths.get(SAVELOCATION.toString() + File.separator + o[0] + ".skeleton.json")));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }
        return null;
    }
    
    public String[] getStringAndFiletype(Path element){
        String name = element.getFileName().toString().split("\\.")[0];
        try {
            ObjectMapper mapper = new ObjectMapper();
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
