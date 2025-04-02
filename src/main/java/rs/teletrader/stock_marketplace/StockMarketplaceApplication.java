package rs.teletrader.stock_marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockMarketplaceApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockMarketplaceApplication.class, args);
	}
}
