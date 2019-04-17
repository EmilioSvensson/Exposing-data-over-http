package se.itu.systemet.rest;

import java.util.List;
import java.util.ArrayList;
import se.itu.systemet.domain.Product;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.*;
import org.json.*;


public class SystemetRestApiAccess implements ApiAccess {

private static final String SERVLET_URL = "http://localhost:8080/search/products/all?";


public List<Product> fetch(Query query){

Client client = new Client((SERVLET_URL + query.toQueryString()));

System.out.println(client.getJson());
//testar att ta bort för ökad prestanda
//funkade fan inte

ProductParser pp = new ProductParser();

List<Product> products = pp.parse(client.getJson());
System.out.println("Found " + pp.parse(client.getJson()).size() + " products");

return products;



  }
}
