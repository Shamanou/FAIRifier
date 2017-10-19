
package org.dtls.fairifier;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpHeaders;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.jsonld.JSONLDParser;
import org.eclipse.rdf4j.rio.nquads.NQuadsParser;
import org.eclipse.rdf4j.rio.ntriples.NTriplesParser;
import org.eclipse.rdf4j.rio.rdfjson.RDFJSONParser;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLParser;
import org.eclipse.rdf4j.rio.trig.TriGParser;
import org.eclipse.rdf4j.rio.trix.TriXParser;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;
import org.json.JSONWriter;

import com.google.refine.commands.Command;

/**
 * Enables automatic file format detection
 * 
 * @author Shamanou van Leeuwen
 * @date May 24 2017
 *
 */

/**
 * A command which detects the file format, should be configured in the
 * controller and called from the javascript front-end.
 * 
 */
public class DetectFileFormatCommand extends Command {

    private final static ArrayList<RDFParser> parsers = new ArrayList<RDFParser>() {

        {
            add(new TurtleParser());
            add(new NTriplesParser());
            add(new RDFXMLParser());
            add(new JSONLDParser());
            add(new NQuadsParser());
            add(new RDFJSONParser());
            add(new TriXParser());
            add(new TriGParser());
        }
    };

    /**
     * This method takes a request containing RDF and tries to iterate over
     * different parsers to see which one is able to parse the data. If it is able
     * to be parsed a format string is returned by passing it to the response object
     * in JSON-format.
     * 
     * URLs are parsed by checking the content-type of the HTTP response.
     * 
     * @param req
     *            a request object
     * @param res
     *            a response object
     * @see com.google.refine.commands.Command#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        FileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        final ServletFileUpload upload = new ServletFileUpload(factory);
        String baseuri = null;
        InputStream[] in = new InputStream[parsers.size()];
        String format = null;
        String url = null;

        try {
            List<FileItem> items = upload.parseRequest(req);
            for (final FileItem item : items) {
                if (item.getFieldName().equals("baseuri")) {
                    baseuri = item.getString();
                } else if (item.getFieldName().equals("file_source")) {
                    url = item.getString();
                } else if (item.getFieldName().equals("file_upload")) {
                    for (int i = 0; i < in.length; i++) {
                        StringWriter writer = new StringWriter();
                        in[i] = new BufferedInputStream(item.getInputStream());
                    }
                }
            }
        } catch (FileUploadException ex) {
            respondException(res, ex);
        }

        if ((url != null) && !url.trim().equals("") && !url.trim().equals("url")) {

            try {
                format = getFormatFromUrl(parsers, url, in);
            } catch (IOException e) {
                respondException(res, e);
            }
        } else {
            format = parseFullFile(parsers, in, baseuri);
        }
        try {
            res.setCharacterEncoding("UTF-8");
            res.setHeader("Content-Type", "application/json");
            JSONWriter writer = new JSONWriter(res.getWriter());
            writer.object();
            writer.key("RDFFormat");
            writer.value(format);
            writer.endObject();
        } catch (Exception e) {
            respondException(res, e);
        }
    }

    private String parseFullFile(ArrayList<RDFParser> parsers, InputStream[] in, String baseuri) {
        String format = null;
        for (int i = 0; i < parsers.size(); i++) {
            try {
                if (baseuri == null) {
                    baseuri = "";
                }
                parsers.get(i).parse(in[i], baseuri);
                format = parsers.get(i).getRDFFormat().getDefaultMIMEType();
                break;
            } catch (Exception e) {
                continue;
            }
        }
        return format;
    }

    private String getFormatFromUrl(ArrayList<RDFParser> parsers, String url, InputStream[] i)
            throws IOException, MalformedURLException {
        StringBuffer response = new StringBuffer();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        List<RDFFormat> rdfFormats = parsers.stream().map(val -> val.getRDFFormat()).collect(Collectors.toList());
        List<String> acceptHeaders = RDFFormat.getAcceptParams(rdfFormats, false, RDFFormat.RDFXML);

        for (String acceptHeader : acceptHeaders) {
            con.setRequestProperty(HttpHeaders.ACCEPT, acceptHeader);
        }
        con.connect();
        if (con.getResponseCode() == 200) {
            return con.getContentType();
        }
        return RDFFormat.RDFXML.getDefaultMIMEType();
    }
}
