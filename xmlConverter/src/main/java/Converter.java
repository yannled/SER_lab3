import com.sun.xml.internal.bind.v2.util.XmlFactory;
import org.jdom2.*;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Attr;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.jaxen.*;
import org.jaxen.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class Converter {
    private String oldxmlPath;
    private Document oldXml;
    private Document newXml;
    private String newXmlPath;

    public Converter(String oldxmlPath, String newXmlPath){
        this.oldxmlPath = oldxmlPath;
        this.newXmlPath = newXmlPath;
    }

    public void getOldXMLContent(){
        SAXBuilder builder = new SAXBuilder();
        oldXml = null;
        try
        {
            oldXml = builder.build(oldxmlPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void convertToNewXML(){
        XPathFactory xFactory = XPathFactory.instance();
        newXml = new Document();

        //Ajout XLS
        ProcessingInstruction piXLS = new ProcessingInstruction("xml-stylesheet");
        HashMap<String, String> piAttributes = new HashMap<String, String>();
        piAttributes.put("type", "text/xls");
        piAttributes.put("href", "projection.xls");
        piXLS.setData(piAttributes);
        newXml.addContent(piXLS);

        //Ajout docType
        DocType docType = new DocType("plex", "projections.dtd");
        newXml.addContent(docType);

        //Ajout element racine
        Element newRoot = new Element("plex");
        newXml.addContent(newRoot);


        Element oldRoot = oldXml.getRootElement();

        //Récupérer mots cles
        List<Element> motsCleList = oldRoot.getChildren("motsCle").get(0).getChildren();

        //Récupérer genres
        List<Element> genresList = oldRoot.getChildren("genres").get(0).getChildren();

        //Récupérer langages
        List<Element> langagesList = oldRoot.getChildren("langages").get(0).getChildren();

        //Récupérer critiques
        List<Element> critiquesList = oldRoot.getChildren("critiques").get(0).getChildren();

        //Récupérer acteurs
        List<Element> acteursList = oldRoot.getChildren("acteurs").get(0).getChildren();

        //Récupérer films
        List<Element> filmsList = oldRoot.getChildren("films").get(0).getChildren();

        //Récupérer projections
        List<Element> projectionsList = oldRoot.getChildren("projections").get(0).getChildren();

        Element projections = new Element("projections");
        newRoot.addContent(projections);

        Element films = new Element("films");
        newRoot.addContent(films);

        Element acteurs = new Element("acteurs");
        newRoot.addContent(acteurs);

        Element list_langages = new Element("list_langages");
        newRoot.addContent(list_langages);

        Element list_genres = new Element("list_genres");
        newRoot.addContent(list_genres);

        Element liste_mots_cles = new Element("liste_mots_cles");
        newRoot.addContent(liste_mots_cles);

        for(int count = 0; count < projectionsList
                .size();count++){

            Element projection = new Element("projection");

            String id = projectionsList.get(count).getChild("filmProj").getAttributeValue("filmId");
            Attribute film_id = new Attribute("film_id",id);
            projection.setAttribute(film_id);

            XPathExpression expr = xFactory.compile("//films/film[@filmId = '"+id+"']/titre", Filters.element());
            List<Element> ti = expr.evaluate(oldXml);
            Attribute titre = new Attribute("titre",ti.get(0).getText());
            projection.setAttribute(titre);

            Element salle = new Element("salle");
            salle.setText(projectionsList.get(count).getChild("numeroSalle").getText());

            Attribute taille = new Attribute("taille","");
            salle.setAttribute(taille);

            projection.addContent(salle);

            Element date_heure = new Element("date_heure");
            date_heure.setText(projectionsList.get(count).getChild("dateProjection").getText());

            Attribute format = new Attribute("format","");
            date_heure.setAttribute(format);

            projection.addContent(date_heure);

            newRoot.getChild("projections").addContent(projection);
        }

        for(int count = 0; count < filmsList
                .size();count++){
            Element film = new Element("film");
            film.setAttribute("no",filmsList.get(count).getAttributeValue("filmId"));

            Element titre = new Element("titre");
            titre.setText(filmsList.get(count).getChild("titre").getText());
            film.addContent(titre);

            Element duree = new Element("duree");
            duree.setText(filmsList.get(count).getChild("duree").getText());
            Attribute format = new Attribute("format","");
            duree.setAttribute(format);
            film.addContent(duree);

            Element synopsis = new Element("synopsys");
            synopsis.setText(filmsList.get(count).getChild("synopsis").getText());
            film.addContent(synopsis);

            Element photo = new Element("photo");
            photo.setAttribute("url", filmsList.get(count).getChild("photo").getAttributeValue("url"));
            film.addContent(photo);

            Element critiques = new Element("critiques");
            List<Element> crits = filmsList.get(count).getChildren("critiqueFilm");
            for(Element crit : crits) {
                Element critique = new Element("critique");
                critique.setText()
            }
            film.addContent(critiques);

            Element langages = new Element("langages");

            Element genres = new Element("genres");

            Element mots_cles = new Element("mots_cles");

            Element roles = new Element("roles");

            newRoot.getChild("films").addContent(film);
        }

        for(int count = 0; count < acteursList
                .size();count++){
            newRoot.getChild("acteurs").addContent(acteursList.get(count).detach());
        }

        for(int count = 0; count < langagesList
                .size();count++){
            newRoot.getChild("list_langages").addContent(langagesList.get(count).detach());
        }

        for(int count = 0; count < genresList
                .size();count++){
            newRoot.getChild("list_genres").addContent(genresList.get(count).detach());
        }

        for(int count = 0; count < motsCleList
                .size();count++){
            newRoot.getChild("liste_mots_cles").addContent(motsCleList.get(count).detach());
        }

    }

    public void CreateNewXML(){

        XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
        File file = new File(newXmlPath);
        try {
            outp.output(newXml, new FileOutputStream(file));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }


    }
}
