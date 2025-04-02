package rs.teletrader.stock_marketplace.Configuration.AppStartup;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SharesGenerator {

    public static void generate() {
        int numUsers = 81;
        int numCompanies = 20;
        JSONArray sharesData = new JSONArray();
        Random random = new Random();

        for (int companyId = 1; companyId <= numCompanies; companyId++) {
            double basePrice = 20 + (random.nextDouble() * 180);
            int numShares = random.nextInt(101) + 100;
            List<Integer> shuffledUsers = new ArrayList<>();
            for (int i = 1; i <= numUsers; i++) {
                shuffledUsers.add(i);
            }
            Collections.shuffle(shuffledUsers);
            int maxShares = Math.min(numShares, numUsers);
            for (int i = 0; i < maxShares; i++) {
                int userId = shuffledUsers.get(i);
                int sharesCount = random.nextInt(5) + 1;
                double price = basePrice + (random.nextDouble() * 4) - 2;

                JSONObject share = new JSONObject();
                share.put("user", new JSONObject().put("id", userId));
                share.put("company", new JSONObject().put("id", companyId));
                share.put("sharesCount", sharesCount);
                share.put("price", price);

                System.out.println(share.toString());
                sharesData.put(share);
            }
        }

    
    }
}
