
package org.dtls.fairifier;

import java.io.IOException;
import java.util.List;

/**
 * 
 * This service communicates with the API backend of openrefine and allows
 * direct access to rdf skeletons.
 * 
 * @author Shamanou van Leeuwen
 *
 */

interface RdfSkeletonService {

    public List<Model> listModels(String fileType)
            throws IOException;

    public List<Model> listModels()
            throws IOException;

    public void saveModel(String json, String fileType, String title, String projectId)
            throws IOException;

    public Model loadModel(String projectId)
            throws IOException;
}
