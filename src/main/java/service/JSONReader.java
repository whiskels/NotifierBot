package service;


import bot.Bot;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import security.Token;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JSONReader {
    private final String URL;
    private List<Customer> customerList;

    /*
     * Constructor initializes URL
     */
    public JSONReader() {
        this.URL = Token.getUrl();
    }

    /*
     * Reads JSON data from URL and creates Customer list
     */
    public void update() {
        JSONObject json = null;
        try {
            json = readJsonFromUrl(URL);
            createCustomerList(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Reads all data from reader
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /*
     * Reads JSONObject from given URL
     */
    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    /*
     * Creates customer list based on JSONArray of objects
     */
    private void createCustomerList(JSONObject json) throws IOException {
        customerList = new ArrayList<>();
        JSONArray content = (JSONArray) json.get("content");

        Iterator<Object> it = content.iterator();
        while (it.hasNext()) {
            StringReader reader = new StringReader(it.next().toString());

            ObjectMapper mapper = new ObjectMapper();

            Customer customer = mapper.readValue(reader, Customer.class);
            customer.calculateOverallDebt();

            customerList.add(customer);
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
