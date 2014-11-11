import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Scadgek on 11/4/2014.
 */
public class Config {
    private int lambda;
    private int maxAge;
    private double alpha;
    private double beta;
    private double epsW;
    private double epsN;

    public int getLambda() {
        return lambda;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getEpsW() {
        return epsW;
    }

    public double getEpsN() {
        return epsN;
    }

    public Config(String filepath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new FileInputStream(filepath));

        NodeList nodeList = document.getDocumentElement().getChildNodes();

        this.lambda = Integer.valueOf(nodeList.item(1).getChildNodes().item(0).getNodeValue());
        this.maxAge = Integer.valueOf(nodeList.item(3).getChildNodes().item(0).getNodeValue());
        this.alpha = Double.valueOf(nodeList.item(5).getChildNodes().item(0).getNodeValue());
        this.beta = Double.valueOf(nodeList.item(7).getChildNodes().item(0).getNodeValue());
        this.epsW = Double.valueOf(nodeList.item(9).getChildNodes().item(0).getNodeValue());
        this.epsN = Double.valueOf(nodeList.item(11).getChildNodes().item(0).getNodeValue());
    }
}
