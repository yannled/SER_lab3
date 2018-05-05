public class xmlConverter {
    public static void main(final String... args){

        Converter converter = new Converter("/home/zutt/Documents/sync/Heig/Ser/SER_lab3/xmlConverter/src/xml.xml","/home/zutt/Documents/sync/Heig/Ser/SER_lab3/xmlConverter/src/projections.xml");
        converter.getOldXMLContent();
        converter.convertToNewXML();
        converter.CreateNewXML();
    }
}
