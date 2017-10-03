package org.dtls.fairifier;


public class RdfSkeletonJsonTransformerImpl implements RdfSkeletonTransformer{
    private Model model = new Model();
    
    @Override
    public void transform(String model) {
        this.model.setJson(model);
    }

    @Override
    public String getModelAsJsonString() {
        return this.model.getJson();
    }

    @Override
    public void setModelMetadata(SkeletonMetadata metadata) {
        this.model.setMetadata(metadata);
    }

    @Override
    public SkeletonMetadata getSkeletonMetadata() {
        return this.model.getMetadata();
    }
}
