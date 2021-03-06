package SeleniumTest.SeleniumCodingChallenges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Day2Challenge {

	static WebDriver driver;

	public static void main(String[] args) {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();

		driver.get("https://www.noon.com/uae-en/");
		System.out.println(driver.getTitle());
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		List<Product> productsFetched = getProductsFromCategory("Recommended For You");

		System.out.println("Products Fetched: " + productsFetched.size());

		for (Product product : productsFetched) {
			System.out.println("Product Name: " + product.name);
			System.out.println("Product Price: " + product.price);
			System.out.println("");
		}

		driver.quit();

	}

	public static List<Product> getProductsFromCategory(String categoryName) {
		String categoryXpath = "//h3[contains(text(), '" + categoryName + "')]";
		String productsXpath = "//h3[contains(text(), '" + categoryName
				+ "')]/../../following-sibling::div//div[@class='swiper-wrapper']/div";
		String nextButtonXpath = "//h3[contains(text(), '" + categoryName
				+ "')]/../../following-sibling::div/div[contains(@class, 'swiper-button-next')]";
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		
		List<WebElement> checkCategory = driver.findElements(By.xpath(categoryXpath));
		while(checkCategory.size() == 0) {
			js.executeScript("window.scrollBy(0, 500)");
			checkCategory = driver.findElements(By.xpath(categoryXpath));
		}

		
		WebElement element = driver.findElement(By.xpath(categoryXpath));
		js.executeScript("arguments[0].scrollIntoView(true)", element);
		
		Boolean nextBtnVisible = driver.findElement(By.xpath(nextButtonXpath)).isDisplayed();

		List<WebElement> productsList = driver.findElements(By.xpath(productsXpath));
		List<String> productsFetched = new ArrayList<String>();
		
		List<Product> productsFound = new ArrayList<Product>();

		while (nextBtnVisible) {
			for (int i = 1; i <= productsList.size(); i++) {
				String productXpath = "//h3[contains(text(), '" + categoryName +"')]/../../following-sibling::div//div[@class='swiper-wrapper']/div["
						+ i + "]//a//div[@data-qa='product-name']/div";
				String productPriceXpath = "//h3[contains(text(), '" + categoryName +"')]/../../following-sibling::div//div[@class='swiper-wrapper']/div["
						+ i + "]//a//strong[@data-qa='productPrice']";
				String productName = driver.findElement(By.xpath(productXpath)).getText().trim();
				String productPrice = driver.findElement(By.xpath(productPriceXpath)).getText().trim();
				if (!productName.isEmpty()) {
					Product prd = new Product(productName, Double.parseDouble(productPrice));
					productsFound.add(prd);
					productsFetched.add(productName);
				}
			}
			driver.findElement(By.xpath(nextButtonXpath)).click();
			nextBtnVisible = driver.findElement(By.xpath(nextButtonXpath)).isDisplayed();
		}

		return productsFound;
	}

}
