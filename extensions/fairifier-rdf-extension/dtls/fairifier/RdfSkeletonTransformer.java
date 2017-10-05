
package org.dtls.fairifier;

import java.io.IOException;
import java.nio.file.Path;

public interface RdfSkeletonTransformer {

    public Model read(Path path)
            throws IOException;

    public void write(Model model, Path path)
            throws IOException;
}
