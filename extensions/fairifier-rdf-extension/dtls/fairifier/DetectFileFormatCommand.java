package org.dtls.fairifier;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
 * A command which detects the file format, should be configured in the controller and called from
 * the javascript front-end.
 * 
 */
public class DetectFileFormatCommand extends Command {
    RDFParser rdfParserttl = new TurtleParser();
    RDFParser rdfParserntriples = new NTriplesParser();
    RDFParser rdfParserrdfxml = new RDFXMLParser();
    RDFParser rdfParserJsonld = new JSONLDParser();
    RDFParser rdfParserNQuads = new NQuadsParser();
    RDFParser rdfParserJson = new RDFJSONParser();
    RDFParser rdfParserTrix = new TriXParser();
    RDFParser rdfParserTrig = new TriGParser();

    RDFParser[] parsers;

    /**
     * This method takes a request containing rdf and tries to iterate over different parsers to see
     * which one is able to parse the data. If it is able to be parsed a format string is returned
     * by passing it to the response object in JSON-format.
     * 
     * When there is no format detected the parser defaults to rdf/xml.
     * 
     * Other formats that can be parsed are Turtle and Ntripples. NOTE: Sesame supports more formats
     * but the currently used version gave problems with other parsers.
     * 
     * @param req a request object
     * @param res a response object
     * @see com.google.refine.commands.Command#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        this.parsers = new RDFParser[] {this.rdfParserntriples, this.rdfParserttl,
                this.rdfParserrdfxml, this.rdfParserJsonld, this.rdfParserNQuads,
                this.rdfParserJson, this.rdfParserTrix, this.rdfParserTrig};
        FileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        String baseuri = null;
        String filename = null;
        InputStream[] in = new InputStream[this.parsers.length];
        String format = null;
        String url = null;

        try {
            items = upload.parseRequest(req);
            for (FileItem item : items) {
                if (item.getFieldName().equals("baseuri")) {
                    baseuri = item.getString();
                } else if (item.getFieldName().equals("file_source")) {
                    url = item.getString();
                } else if (item.getFieldName().equals("file_upload")) {
                    filename = item.getName();
                    for (int i = 0; i < in.length; i++) {
                        StringWriter writer = new StringWriter();
                        in[i] = new BufferedInputStream(item.getInputStream());

                    }
                }
            }
        } catch (FileUploadException ex) {
            respondException(res, ex);
        }
        StringBuffer response = new StringBuffer();;

        if ((url != null) && !url.trim().equals("") && !url.trim().equals("url")) {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            BufferedReader inr = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = inr.readLine()) != null) {
                response.append(inputLine);
            }
            inr.close();
            for (int i = 0; i < in.length; i++) {
                in[i] = new BufferedInputStream(
                        new ByteArrayInputStream(response.toString().getBytes("UTF8")));
            }
        }
        for (int i = 0; i < parsers.length; i++) {
            try {
                if (baseuri == null) {
                    baseuri = "";
                }
                parsers[i].parse(in[i], baseuri);
                format = parsers[i].getRDFFormat().getDefaultMIMEType();
                break;
            } catch (Exception e) {
                System.out.println(
                        parsers[i].getRDFFormat().getDefaultMIMEType() + " : " + e.toString());
                continue;
            }
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
}
