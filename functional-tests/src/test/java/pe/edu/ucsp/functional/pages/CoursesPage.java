package pe.edu.ucsp.functional.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object de la pagina "Cursos" del Backoffice (http://localhost:8041/courses).
 * Agrupa los localizadores de Selenium para que los casos de prueba solo describan acciones.
 */
public final class CoursesPage {

    private static final By ID_INPUT       = By.name("id");
    private static final By NAME_INPUT     = By.name("name");
    private static final By DURATION_INPUT = By.name("duration");
    private static final By CREATE_BUTTON  = By.cssSelector("form[action='/courses'] button[type='submit']");
    private static final By ERROR_MESSAGES = By.cssSelector("form[action='/courses'] p.text-red-500");
    private static final By COUNTER        = By.xpath("//p[contains(., 'cuenta con')]/b");
    private static final By PAGE_HEADING   = By.tagName("h1");

    private final WebDriver driver;
    private final String    baseUrl;

    public CoursesPage(WebDriver driver, String baseUrl) {
        this.driver  = driver;
        this.baseUrl = baseUrl;
    }

    public CoursesPage open() {
        driver.get(baseUrl + "/courses");
        return this;
    }

    public String title() {
        return driver.getTitle();
    }

    public String heading() {
        return driver.findElement(PAGE_HEADING).getText().trim();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public int coursesCounter() {
        return Integer.parseInt(driver.findElement(COUNTER).getText().trim().replace(",", "").replace(".", ""));
    }

    public String idValue() {
        return driver.findElement(ID_INPUT).getAttribute("value");
    }

    public String nameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String durationValue() {
        return driver.findElement(DURATION_INPUT).getAttribute("value");
    }

    /** Llena el formulario "Crear curso". Un valor null deja el campo tal como esta. */
    public CoursesPage fillForm(String id, String name, String duration) {
        type(ID_INPUT, id);
        type(NAME_INPUT, name);
        type(DURATION_INPUT, duration);
        return this;
    }

    /** Presiona "Crear curso!" y espera a que el servidor redirija y cargue la pagina de nuevo. */
    public CoursesPage submit() {
        WebElement button = driver.findElement(CREATE_BUTTON);
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.stalenessOf(button));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(CREATE_BUTTON));
        return this;
    }

    public List<String> errorMessages() {
        return driver.findElements(ERROR_MESSAGES).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /** Mensajes de error que aparecen debajo del campo indicado (id, name o duration). */
    public List<String> errorsFor(String field) {
        By fieldErrors = By.xpath("//input[@name='" + field + "']/following-sibling::p[contains(@class,'text-red-500')]");
        return driver.findElements(fieldErrors).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /** true si debajo del campo aparece un error que contiene el texto indicado. */
    public boolean hasErrorFor(String field, String text) {
        return errorsFor(field).stream().anyMatch(message -> message.contains(text));
    }

    private void type(By locator, String value) {
        if (value == null) {
            return;
        }
        WebElement input = driver.findElement(locator);
        input.clear();
        input.sendKeys(value);
    }
}
