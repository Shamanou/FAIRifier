package org.dtls.fairifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileSystemRdfSkeletonImp implements RdfSkeletonService{
    private static final Path SAVELOCATION = Paths.get(System.getProperty("user.home") + "/.local/share/openrefine/");
    private final static Logger logger = LoggerFactory.getLogger("FileSystemRdfSkeletonImp");

    @Override
    public List<String> listModels(String fileType) {
        File folder = SAVELOCATION.toFile();
        ArrayList<String> out = new ArrayList<String>();
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String name = listOfFiles[i].getName().split(".")[0];
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    SkeletonMetadata metadata = mapper.readValue(new File(listOfFiles[i].getParent() + File.separator + name + "metadata.skeleton.json"), SkeletonMetadata.class);
                    if (metadata.getFileType().equals(fileType)) {
                        out.add(new String(Files.readAllBytes(Paths.get(listOfFiles[i].getPath()))));
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return out;
    }

    @Override
    public List<String> listModels() {
        File folder = SAVELOCATION.toFile();
        ArrayList<String> out = new ArrayList<String>();
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                try {
                    out.add(new String(Files.readAllBytes(Paths.get(listOfFiles[i].getPath()))));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return out;
    }
    
    @Override
    public void saveModel(String json, String fileType, String projectId) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(SAVELOCATION.toString() + File.separator + projectId + ".skeleton.json"));
            out.write(json);
            out.close();
            ObjectMapper mapper = new ObjectMapper();
            SkeletonMetadata metadata = new SkeletonMetadata();
            metadata.setFileType(fileType);
            mapper.writeValue(new File(SAVELOCATION.toString() + File.separator  + projectId + ".metadata.skeleton.json"), metadata);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String loadModel(String projectId) {
        File folder = SAVELOCATION.toFile();
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String name = listOfFiles[i].getName().split(".")[0];
                if(name.equals(projectId)) {
                    try {
                        return new String(Files.readAllBytes(Paths.get(listOfFiles[i].getPath())));
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }
}
