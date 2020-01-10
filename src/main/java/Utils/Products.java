package Utils;

import java.util.LinkedHashMap;

public class Products {
    //Saves main.Util.Products and its price
    private LinkedHashMap<String, Double> services;
    public int numCounter; //Number of main.Util.Products added
    public Products(){
        services = new LinkedHashMap<>();

        //Setting up products
        services.put("Consultation", 30.0);
        services.put("Special Acupuncture Treatment", 80.0);
        services.put("Medical Machine Therapy", 30.0);
        services.put("Tapping Treatment", 25.0);

        //Initializing the counter
        numCounter = services.size()+1;
    }

    //Returns products corresponding price
    public Double getProductPrice(String product){
        return services.get(product);
    }
}
