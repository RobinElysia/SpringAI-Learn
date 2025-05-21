package springai.ai.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Component
@SuppressWarnings({"all"})
public class ReadSelecter {
    @Autowired
    private MyTextReader myTextReader;
    @Autowired
    private MyHTMLReader myHTMLReader;
    @Autowired
    private MyPDFReader myPDFReader;
    @Autowired
    private MyPDFGReader myPDFGReader;
    @Autowired
    private MyMDReader myMDReader;
    @Autowired
    private MyTikaReader myTakiReader;

    public void ReadSelecter(Resource resource){
        if (resource.getFilename().endsWith(".txt")){
            myTextReader.readText(resource);
        }else if (resource.getFilename().endsWith(".html")){
            myHTMLReader.readHTML(resource);
        }else if (resource.getFilename().endsWith(".pdf")){
            myPDFReader.readPDF(resource);
        }else if (resource.getFilename().endsWith(".md")){
            myMDReader.readMD(resource);
        }else if (resource.getFilename().endsWith(".pdfg")){
            myPDFGReader.readPDFG(resource);
        }else {
            myTakiReader.readTaki(resource);
        }
    }
}
