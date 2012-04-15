package fr.free.jnizet.shivadep;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class Browser {

    private static Browser browser = new Browser();

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private WebDriver webDriver;

    public static synchronized Browser getInstance() throws InterruptedException, ExecutionException {
        if (browser.webDriver == null) {
            browser.webDriver = browser.executor.submit(new Callable<WebDriver>() {
                @Override
                public WebDriver call() throws Exception {
                    WebDriver wd = new FirefoxDriver();
                    return wd;
                }
            }).get();
        }
        return browser;
    }


    private Browser() {
    }

    public boolean authenticate(final String user, final String password) throws InterruptedException, ExecutionException {
        Callable<Boolean> task = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                webDriver.get("https://shiva.objetdirect.com/app.php/login");
                WebElement userInput = webDriver.findElement(By.id("username"));
                userInput.sendKeys(user);
                WebElement passwordInput = webDriver.findElement(By.id("password"));
                passwordInput.sendKeys(password);
                WebElement submitElement = webDriver.findElement(By.id("login_button"));
                submitElement.click();
                return webDriver.getTitle().contains("Welcome") || webDriver.getTitle().contains("Bienvenue");
            }
        };
        Future<Boolean> future = executor.submit(task);
        return future.get();
    }


    public List<Option> loadProjects() throws InterruptedException, ExecutionException {
        Callable<List<Option>> task = new Callable<List<Option>>() {
            @Override
            public List<Option> call() throws Exception {
                webDriver.get("https://shiva.objetdirect.com/app.php/depense/new");
                WebElement projectRadio = webDriver.findElement(By.id("association_projet"));
                projectRadio.click();
                WebElement projectSelect = webDriver.findElement(By.id("depense_eotp"));

                List<WebElement> allOptions = projectSelect.findElements(By.tagName("option"));
                List<Option> result = new ArrayList<Option>();
                for (WebElement option : allOptions) {
                    String value = option.getAttribute("value");
                    String label = option.getText();
                    if (!value.isEmpty()) {
                        result.add(new Option(value, label));
                    }
                }
                return result;
            }
        };
        Future<List<Option>> future = executor.submit(task);
        return future.get();
    }


    public void createExpense(final Date date, final String description, final String projet, final int km) throws InterruptedException, ExecutionException {
        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                webDriver.get("https://shiva.objetdirect.com/app.php/depense/new");
                WebElement typeOfExpenseSelect = webDriver.findElement(By.id("depense_type_de_frais"));
                selectOptionWithValue(typeOfExpenseSelect, "14");

                WebElement descriptionTextInput = webDriver.findElement(By.id("depense_objet"));
                descriptionTextInput.sendKeys(description);

                WebElement dateTextInput = webDriver.findElement(By.id("depense_date_engagement"));
                dateTextInput.sendKeys(new SimpleDateFormat("dd/MM/yyyy").format(date));

                WebElement projectRadio = webDriver.findElement(By.id("association_projet"));
                projectRadio.click();

                WebElement projectSelect = webDriver.findElement(By.id("depense_eotp"));
                selectOptionWithValue(projectSelect, projet);

                WebElement kmTextInput = webDriver.findElement(By.id("depense_quantite"));
                kmTextInput.sendKeys(Integer.toString(km));

                WebElement submitButton = webDriver.findElement(By.id("edit"));
                submitButton.click();

                Alert alert = webDriver.switchTo().alert();
                alert.accept();

                return null;
            }
        };
        Future<Void> future = executor.submit(task);
        future.get();
    }

    private void selectOptionWithValue(WebElement selectElement, String value) {
        Select select = new Select(selectElement);
        select.selectByValue(value);
    }
}
