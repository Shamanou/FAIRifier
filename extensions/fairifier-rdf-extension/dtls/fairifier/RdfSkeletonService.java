package org.dtls.fairifier;

import java.util.List;

interface RdfSkeletonService {
    public List<String> listModels(String fileType);
    public List<String> listModels();
    public void saveModel(String json, String fileType, String projectId);
    public String loadModel(String ProjectId);
}