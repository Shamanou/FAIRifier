package org.dtls.fairifier;


public interface RdfSkeletonTransformer {
    public void transform(String model);
    public String getModelAsJsonString();
    public SkeletonMetadata getSkeletonMetadata();
    public void setModelMetadata(SkeletonMetadata metadata );
}
