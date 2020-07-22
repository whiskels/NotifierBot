package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Customer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JSONReader {
    private static final Logger log = LoggerFactory.getLogger(JSONReader.class);
    private final String URL;
    private List<Customer> customerList;

    /*
     * Constructor initializes URL
     */
    public JSONReader() {
        this.URL = System.getenv("JSON_URL");
    }

    /*
     * Reads all data from reader
     */
    private String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        try {
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException e) {
            log.error("Exception while trying to read data - {}", e.getMessage());
        }
        return sb.toString();
    }

    /*
     * Reads JSON data from URL and creates Customer list
     */
    public void update() {
        JSONObject json = readJsonFromUrl(URL);
        if (json != null) {
            createCustomerList(json);
        }
    }

    /*
     * Reads JSONObject from given URL
     */
    private JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException | JSONException e) {
            log.error("Exception while trying to get JSON data from URL - {}", e.getMessage());
            return null;
        }
    }

    /*
     * Creates customer list based on JSONArray of objects
     */
    private void createCustomerList(JSONObject json) {
        customerList = new ArrayList<>();
        JSONArray content = (JSONArray) json.get("content");

        try {

            for (Object o : content) {
                StringReader reader = new StringReader(o.toString());

                ObjectMapper mapper = new ObjectMapper();

                Customer customer = mapper.readValue(reader, Customer.class);
                customer.calculateOverallDebt();

                customerList.add(customer);
            }
        } catch (IOException e) {
            log.error("Exception while reading value from reader - {}", e.getMessage());
        }

        customerList = customerList.stream()
                .filter(customer -> customer.getOverallDebt() > 10)
                .sorted(Comparator.comparingDouble(Customer::getOverallDebt).reversed())
                .collect(Collectors.toList());
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }
}
