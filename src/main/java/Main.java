import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "new_data.json");
        List<Employee> list1 = parseXML("data.xml");
        String json1 = listToJson(list1);
        writeString(json1, "new_data1.json");
        String json2 = readString("new_data1.json");
        List<Employee> list3 = jsonToList(json2);
        System.out.println(list3);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        String json;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return json = gson.toJson(list, listType);
    }

    private static void writeString(String json, String pathName) {
        try (FileWriter file = new FileWriter(pathName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  static ArrayList<Employee> parseXML(String pathName) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Employee> employees = new ArrayList<>();
        Employee employee;
        long id = 0;
        String firstName  = null;
        String lastName  = null;
        String country  = null;
        int age = 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(pathName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                NodeList nodeList1 = node_.getChildNodes();

                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node1 = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node1.getNodeType()) {
                        Element element = (Element) node1;
                        NodeList nodeList2 = element.getChildNodes();
                        if (node1.getNodeName().equals("id")) id = Integer.parseInt(nodeList2.item(0).getNodeValue());
                        if (node1.getNodeName().equals("firstName")) firstName = nodeList2.item(0).getNodeValue();
                        if (node1.getNodeName().equals("lastName")) lastName = nodeList2.item(0).getNodeValue();
                        if (node1.getNodeName().equals("country")) country = nodeList2.item(0).getNodeValue();
                        if (node1.getNodeName().equals("age")) age = Integer.parseInt(nodeList2.item(0).getNodeValue());
                    }
                }
                employee = new Employee(id, firstName, lastName, country, age);
                employees.add(employee);
            }
        }
        return employees;
    }

    private static String readString(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                return  line;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Employee> jsonToList(String json) {
        JSONParser parser = new JSONParser();
        ArrayList<Employee> list = new ArrayList<>();
        JSONArray jsonArray = null;

        try {
            jsonArray = (JSONArray) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
        if (jsonArray != null) {
            for (Object o : jsonArray) {
                list.add(gson.fromJson(o.toString(), Employee.class));
            }
        }
        return list;
    }
}
