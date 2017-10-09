
package org.dtls.fairifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.deri.grefine.rdf.utils.HttpUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.google.refine.commands.Command;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.Metadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;

/**
 * 
 * @author Shamanou van Leeuwen
 * @date 7-11-2016
 *
 */

public class PostFairDataToFairDataPoint extends Command {

    private static SimpleValueFactory f;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        f = SimpleValueFactory.getInstance();

        String uuid_catalog = UUID.randomUUID().toString();
        String uuid_dataset = UUID.randomUUID().toString();
        String uuid_distribution = UUID.randomUUID().toString();

        try {
            StringBuffer jb = new StringBuffer();
            String line = null;
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            JSONObject fdp = new JSONObject(jb.toString()).getJSONObject("metadata");
            JSONObject catalog = fdp.getJSONObject("catalog");
            JSONObject dataset = fdp.getJSONObject("dataset");
            JSONObject distribution = fdp.getJSONObject("distribution");
            JSONObject uploadConfiguration = fdp.getJSONObject("uploadConfiguration");

            CatalogMetadata catalogMetadata = null;
            String catalogPost = null;
            if (!catalog.getBoolean("_exists")) {
                String name = catalog.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                        + catalog.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");

                catalogMetadata = getMetadata(CatalogMetadata.class, catalog,
                        fdp.getString("baseUri") + "/catalog/" + name + "_" + uuid_catalog);

                if (catalog.has("http://xmlns.com/foaf/0.1/homepage")) {
                    catalogMetadata.setHomepage(f.createIRI(catalog.getString("http://xmlns.com/foaf/0.1/homepage")));
                }
                ArrayList<IRI> catalogThemes = new ArrayList<IRI>();
                catalogThemes.add(f.createIRI(catalog.getString("http://www.w3.org/ns/dcat#themeTaxonomy")));
                catalogMetadata.setThemeTaxonomys(catalogThemes);

                if (catalog.has("http://purl.org/dc/terms/description")) {
                    catalogMetadata
                            .setDescription(f.createLiteral(catalog.getString("http://purl.org/dc/terms/description")));
                }
                if (catalog.has("http://purl.org/dc/terms/language")) {
                    catalogMetadata.setLanguage(f.createIRI(catalog.getString("http://purl.org/dc/terms/language")));
                }
                if (catalog.has("http://xmlns.com/foaf/0.1/homepage")) {
                    catalogMetadata.setHomepage(f.createIRI(catalog.getString("http://xmlns.com/foaf/0.1/homepage")));
                }
                catalogPost = pushMetadataToFdp(uuid_catalog, catalogMetadata, "catalog", fdp.getString("baseUri"));

            }
            DatasetMetadata datasetMetadata = null;
            String datasetPost = null;
            if (!dataset.getBoolean("_exists")) {
                String name = dataset.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                        + dataset.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");

                datasetMetadata = getMetadata(DatasetMetadata.class, dataset,
                        fdp.getString("baseUri") + "/dataset/" + name + "_" + uuid_dataset);
                if (dataset.has("http://www.w3.org/ns/dcat#landingPage")) {
                    datasetMetadata
                            .setLandingPage(f.createIRI(dataset.getString("http://www.w3.org/ns/dcat#landingPage")));
                }
                if (dataset.has("http://purl.org/dc/terms/language")) {
                    datasetMetadata.setLanguage(f.createIRI(dataset.getString("http://purl.org/dc/terms/language")));
                }

                ArrayList<IRI> datasetThemes = new ArrayList<IRI>();
                for (int i = 0; i < dataset.getJSONArray("http://www.w3.org/ns/dcat#theme").length(); i++) {
                    datasetThemes
                            .add(f.createIRI(dataset.getJSONArray("http://www.w3.org/ns/dcat#theme").getString(i)));
                }
                datasetMetadata.setThemes(datasetThemes);

                if (dataset.has("http://www.w3.org/ns/dcat#keyword")) {
                    ArrayList<Literal> keyWords = new ArrayList<Literal>();
                    for (int i = 0; i < dataset.getJSONArray("http://www.w3.org/ns/dcat#keyword").length(); i++) {
                        keyWords.add(f
                                .createLiteral(dataset.getJSONArray("http://www.w3.org/ns/dcat#keyword").getString(i)));
                    }
                    datasetMetadata.setKeywords(keyWords);
                }
                if (dataset.has("http://www.w3.org/ns/dcat#contactPoint")) {
                    datasetMetadata
                            .setContactPoint(f.createIRI(dataset.getString("http://www.w3.org/ns/dcat#contactPoint")));
                }
                if (dataset.has("http://purl.org/dc/terms/language")) {
                    datasetMetadata.setLanguage(
                            f.createIRI(dataset.getJSONArray("http://purl.org/dc/terms/language").getString(0)));
                }
                if (dataset.has("http://purl.org/dc/terms/description")) {
                    datasetMetadata
                            .setDescription(f.createLiteral(dataset.getString("http://purl.org/dc/terms/description")));
                }

                name = dataset.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                        + dataset.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");
                if (catalog.getBoolean("_exists")) {
                    datasetMetadata.setParentURI(f.createIRI(
                            catalog.getString("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIdentifier")));
                } else {
                    name = catalog.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                            + catalog.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");

                    datasetMetadata.setParentURI(
                            f.createIRI(fdp.getString("baseUri") + "/catalog/" + name + "_" + uuid_catalog));
                }
                datasetPost = pushMetadataToFdp(uuid_dataset, datasetMetadata, "dataset", fdp.getString("baseUri"));
            }

            String name = "FAIRdistribution_"
                    + distribution.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                    + distribution.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");
            DistributionMetadata distributionMetadata = getMetadata(DistributionMetadata.class, distribution,
                    fdp.getString("baseUri") + "/distribution/" + name + "_" + uuid_distribution);
            distributionMetadata.setMediaType(f.createLiteral("text/turtle"));

            String data = new JSONObject(jb.toString()).getString("data");
            distributionMetadata.setMediaType(f.createLiteral("text/turtle"));
            distributionMetadata.setByteSize(f.createLiteral(data.getBytes("UTF-8").length));

            if (fdp.getString("uploadtype").equals("ftp")) {
                distributionMetadata.setDownloadURL(f.createIRI("ftp://" + uploadConfiguration.getString("host")
                        + uploadConfiguration.getString("directory") + name + ".ttl"));
            } else if (fdp.getString("uploadtype").equals("virtuoso")) {
                distributionMetadata.setDownloadURL(f.createIRI(uploadConfiguration.getString("host")
                        + uploadConfiguration.getString("directory") + name + ".ttl"));
            }
            if (dataset.getBoolean("_exists")) {
                distributionMetadata.setParentURI(f.createIRI(
                        dataset.getString("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIdentifier")));
            } else {
                name = dataset.getString("http://purl.org/dc/terms/title").replace(" ", "_") + "_"
                        + dataset.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");

                distributionMetadata
                        .setParentURI(f.createIRI(fdp.getString("baseUri") + "/dataset/" + name + "_" + uuid_dataset));
            }

            if (distribution.has("http://purl.org/dc/terms/license")) {
                distributionMetadata
                        .setLicense(f.createIRI(distribution.getString("http://purl.org/dc/terms/license")));
            }
            if (distribution.has("http://purl.org/dc/terms/description")) {
                distributionMetadata.setDescription(
                        f.createLiteral(distribution.getString("http://purl.org/dc/terms/description")));
            }

            String distributionPost = pushMetadataToFdp(uuid_distribution, distributionMetadata, "distribution",
                    fdp.getString("baseUri"));

            PushFairDataToResourceAdapter adapter = new PushFairDataToResourceAdapter();
            Resource r = null;

            name = "FAIRdistribution_" + distribution.getString("http://purl.org/dc/terms/title").replace(" ", "_")
                    + "_" + distribution.getString("http://purl.org/dc/terms/hasVersion").replace(" ", "_");

            if (fdp.getString("uploadtype").equals("ftp")) {
                r = new FtpResource(uploadConfiguration.getString("host"), uploadConfiguration.getString("username"),
                        uploadConfiguration.getString("password"), uploadConfiguration.getString("directory"),
                        name + ".ttl");
            } else if (fdp.getString("uploadtype").equals("virtuoso")) {
                r = new VirtuosoResource(uploadConfiguration.getString("host"), name + ".ttl",
                        uploadConfiguration.getString("username"), uploadConfiguration.getString("password"),
                        uploadConfiguration.getString("directory"));
            }
            r.setFairData(data);
            adapter.setResource(r);
            adapter.push();

            res.setCharacterEncoding("UTF-8");
            res.setHeader("Content-Type", "application/json");
            JSONWriter writer = new JSONWriter(res.getWriter());
            writer.object();
            writer.key("code");
            writer.value("ok");
            if (!catalog.getBoolean("_exists")) {
                writer.key("catalogPost");
                writer.value(catalogPost);
            }
            if (!dataset.getBoolean("_exists")) {
                writer.key("datasetPost");
                writer.value(datasetPost);
            }
            writer.key("distributionPost");
            writer.value(distributionPost);
            writer.endObject();

        } catch (

        Exception ex) {
            respondException(res, ex);
        }
    }

    private <T extends Metadata> T getMetadata(Class<T> type, JSONObject args, String uri)
            throws JSONException, InstantiationException, IllegalAccessException {
        T metadata = type.newInstance();
        metadata.setVersion(f.createLiteral(args.getString("http://purl.org/dc/terms/hasVersion")));
        metadata.setUri(f.createIRI(uri));
        Date date = new Date();
        metadata.setIssued(f.createLiteral(date));
        metadata.setModified(f.createLiteral(date));
        metadata.setTitle(f.createLiteral(args.getString("http://purl.org/dc/terms/title")));
        if (args.has("http://purl.org/dc/terms/rights")) {
            metadata.setRights(f.createIRI(args.getString("http://purl.org/dc/terms/rights")));
        }
        if (!metadata.getClass().equals(DistributionMetadata.class)) {
            Agent agent = new Agent();
            agent.setUri(f.createIRI(args.getJSONObject("http://purl.org/dc/terms/publisher").getString("url")));
            agent.setName(f.createLiteral(args.getJSONObject("http://purl.org/dc/terms/publisher").getString("url")));
            metadata.setPublisher(agent);
        }
        return metadata;
    }

    private String pushMetadataToFdp(String uuid, Metadata metadata, String metadataType, String baseUri)
            throws MetadataException, JSONException, IOException {

        String metadataLayerStr = MetadataUtils.getString(metadata, RDFFormat.TURTLE, false)
                .replaceAll("\\<" + metadata.getUri().toString() + "\\>", "<>");
        String metadataId = baseUri + "/" + metadataType + "?id=" + metadata.getTitle().getLabel().replace(" ", "_")
                + "_" + metadata.getVersion().getLabel().replace(" ", "_") + "_" + uuid;

        return IOUtils.toString(HttpUtils.post(metadataId, metadataLayerStr, "text/turtle").getContent(), "UTF-8");
    }
}
