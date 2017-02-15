package com.kharkhanov2;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mordr on 10.02.2017.
 */
public class XMLSerializer {
    public void serializeToXML(Object obj, String xmlFilePath){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element objectElement = document.createElement("object");
            objectElement.setAttribute("type", obj.getClass().getSimpleName());
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field:fields){
                Element fieldElement = document.createElement("field");
                fieldElement.setAttribute("type", field.getType().getSimpleName());
                fieldElement.setAttribute("id", field.getName());
                field.setAccessible(true);
                Object value = field.get(obj);
                String valueStr = value.toString();
                fieldElement.setAttribute("value", valueStr);
                objectElement.appendChild(fieldElement);
            }
            document.appendChild(objectElement);

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);

            System.out.println("Object serialized: " + obj);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public List<Object> deserializeFromXML(String pathToXMLFile) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(pathToXMLFile));
            return processObject(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<Object> processObject(Node node) {
        NodeList list = node.getChildNodes();
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String teg = item.getNodeName();
            if (teg.equals("object")) {
                Object object = null;
                String typeName = item.getAttributes().getNamedItem("type").getNodeValue();
                try {
                    System.out.println("trying to load custom classloader for teg = " + teg + " with type attribute = " + typeName);
                    CustomClassLoader customClassLoader = new CustomClassLoader();
                    Class<?> objectClass = customClassLoader.loadClass(typeName);//Class.forName(typeName);
                    object = objectClass.newInstance();
                    processFields(item, object);
                    objects.add(object);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return objects;
    }
    private void processFields(Node node, Object object) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            String teg = item.getNodeName();
            if(teg.equals("field")) {
                String fieldName = item.getAttributes().getNamedItem("id").getNodeValue();
                String fieldTypeName = item.getAttributes().getNamedItem("type").getNodeValue();
                String fieldValue = item.getAttributes().getNamedItem("value").getNodeValue();
                try {
                    Field field = object.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    setFieldValue(field, object, fieldTypeName, fieldValue);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setFieldValue(Field field, Object object, String fieldTypeName, String fieldValue) {
        try {
            switch(fieldTypeName) {
                case "Integer":
                    field.set(object, Integer.parseInt(fieldValue));
                    break;
                case "int":
                    field.set(object, Integer.parseInt(fieldValue));
                    break;
                case "Float":
                    field.set(object, Float.parseFloat(fieldValue));
                    break;
                case "float":
                    field.set(object, Float.parseFloat(fieldValue));
                    break;
                case "Double":
                    field.set(object, Double.parseDouble(fieldValue));
                    break;
                case "double":
                    field.set(object, Double.parseDouble(fieldValue));
                    break;
                default:
                    field.set(object, fieldValue);
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}