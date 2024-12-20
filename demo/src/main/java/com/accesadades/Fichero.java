package com.accesadades;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*; 
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class Fichero {
    public static void crearXML(ResultSet rs){
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument (null,"trens", null);
            document.setXmlVersion("1.0");
            while (rs.next()) {
                String id = rs.getString("id");
                String nom = rs.getString("Nombre");
                String capacity = rs.getString("Capacitat");


                Element arrel = document.createElement ("tren");
                arrel.setAttribute("id",id);
                document.getDocumentElement().appendChild(arrel);
                CrearElement ("nombre",nom.trim(), arrel, document);
                CrearElement ("capacitat", capacity.trim(), arrel, document);
            }

            // Crear orden por llave desde la API
            Source source = new DOMSource (document);

            String timeXML = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss"));
            Path resourcePath = Paths.get("xmls/trenes_" + timeXML + ".xml");
            Files.createDirectories(resourcePath.getParent());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
            transformer.transform(source, new StreamResult(Files.newBufferedWriter(resourcePath)));
        
        } catch (Exception e ) { 
            System.err.println ("Error: " + e);}
    }
    public static void CrearElement (String dadaTren, String valor, Element arrel, Document document) {
        Element elem = document.createElement (dadaTren);
        Text text = document.createTextNode(valor);
        arrel.appendChild (elem);
        elem.appendChild (text);
    }
}
