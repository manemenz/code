package assessment;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
This is not very fully/well optimized for a Page/Object model framework as I have limited experience in building a BDD framework from the ground up.
I apologize for the single class construction and instantiating WebElements within methods (as I know that is poor practice & standards).
My experience is with maintaining and building out existing frameworks to create regression/feature testing
I am, however, willing to learn more in-depth about how to construct full-fledged frameworks if need be.
I greatly appreciate the opportunity to submit this assessment and look forward to discussing any openings I may be suited for.

P.S. I really wanted to paginate through all result pages, but for the life of me, I could not nail down a consistent locator for the 'Next Page' object.
 */

public class BasePage {
    WebDriver driver;
    String url = "https://www.webstaurantstore.com";
    String product = "stainless work table";
    String keyword = "Table";

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    public void searchTest() throws InterruptedException {

        openBrowser(url);
        searchProduct(product);
        verifySearchPageDisplayed();
        verifyAndPaginateResults(keyword);
        addItemToCart();
        clickCartButton();
        clickRemoveItem();
        verifyEmptyCart();
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    public void openBrowser(String url) throws InterruptedException {

        driver.get(url);
        System.out.println(url + "has been launched");
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println(url + "has been maximized");
    }

    public void searchProduct(String product) {
        WebElement searchBar = driver.findElement(By.name("searchval"));
        WebElement searchButton = driver.findElement(By.xpath( "//*[@id=\"searchForm\"]/div/button"));

        // Click the search bar
        searchBar.click();
        // Enter product to search
        searchBar.sendKeys(product);
        // Click the search button
        searchButton.click();
    }

    public void verifySearchPageDisplayed() {
        WebElement resultsCountHeader = driver.findElement(By.xpath("/html/body/div[2]/div/div[1]/h1"));

        // Give the page a few seconds to load results
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // Verify the results page is displayed
        Assert.assertTrue(resultsCountHeader.isDisplayed());
    }

    public void verifyProductDetails(String keyword){
        List<WebElement> productDetails = driver.findElements(By.id("details"));

        // Verify product details for each returned item in the search results page
        for (int i = 0; i < productDetails.size(); ++i) {
            Assert.assertTrue(productDetails.get(i).getText().contains(keyword));
        }
    }

    public void verifyAndPaginateResults(String keyword) {
        int j = 0;
        WebElement nextPage = driver.findElement(By.xpath("//a[contains(@aria-label, 'go to page')]"));
        List<Object> numOfPages = new ArrayList<>();

        // TODO better handle iterating through pages
        while (j < 8) {
            // Verify the results all have the correct keyword
            verifyProductDetails(keyword);
            // Click the next page
            // TODO figure out better locator to click through all available pages
//            nextPage.click();
            // Give the site 3 seconds to load next page
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            ++j;
        }
    }

    public void addItemToCart() {
        List<WebElement> addToCartButton = driver.findElements(By.name("addToCartButton"));
        int numOfCarts = addToCartButton.size();

        // Add the last in stock item of the results to the cart
        addToCartButton.get(numOfCarts - 1).click();

    }

    public void clickCartButton() {
        WebElement cartButton = driver.findElement(By.id("cartItemCountSpan"));
        WebElement viewCart = driver.findElement(By.linkText("View Cart"));

        try {
            // Intercept the "View Cart" overlay is displayed - click if displayed
            if (viewCart.isDisplayed()) {
                viewCart.click();
            } else {
                // If the "View Cart overlay does not display - click the main "Cart" button
                cartButton.click();
            }
        } catch (Exception e) {
            System.out.println("Unable to click View Cart button");
        }
    }

    public void clickRemoveItem() {
        WebElement removeItem = driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div/div[2]/ul/li[2]/div/div[6]/button"));

        try {
            // Click the "Empty Cart" button to remove all items of the cart
            if (removeItem.isDisplayed()) {
                removeItem.click();
                // Unfortunately instantiate the "Confirm Empty Cart" object here
//                WebElement confirmRemoveItem = driver.findElement(By.cssSelector("#td > div:nth-child(35) > div > div > div > footer > button.bg-origin-box-border.bg-repeat-x.border-solid.border.box-border.cursor-pointer.inline-block.text-center.no-underline.hover\\:no-underline.antialiased.hover\\:bg-position-y-15.mr-2.rounded-normal.text-base.leading-normal.px-7.py-2-1\\/2.hover\\:bg-green-600.text-white.text-shadow-black-60.bg-green-primary.bg-linear-gradient-180-green.border-black-25.shadow-inset-black-17.align-middle.font-semibold"));  //driver.findElement(By.xpath("//*[@id=\"td\"]/div[11]/div/div/div/footer/button[1]"));
//                // Verify the "Confirm Empty Cart" pop-up is displayed - click it to empty cart
//                if (confirmRemoveItem.isDisplayed()) {
//                    confirmRemoveItem.click();
//                }
            }
        } catch(Exception e) {
            System.out.println("Unable to remove items from cart");
        }
    }

    public void verifyEmptyCart() {
        WebElement emptyCartHeader = driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/div[1]/div/div[1]/img"));

        // Give the page a few seconds to load
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        Assert.assertTrue(emptyCartHeader.isDisplayed());
    }

}