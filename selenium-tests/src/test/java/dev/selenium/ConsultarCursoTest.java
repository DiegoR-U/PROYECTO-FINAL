package dev.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultarCursoTest {

    private static final String URL = "http://localhost:8041/courses";

    private static final String COURSE_ID =
            "92684418-e727-4d77-93ce-6f023928a7eb";

    private static final String COURSE_NAME =
            "Curso Selenium Consulta";

    private static final String COURSE_DURATION =
            "5 days";

    private WebDriver driver;
    private WebDriverWait wait;

    // SETUP: Se ejecuta antes de cada prueba
    @BeforeEach
    public void iniciarNavegador() {

        FirefoxOptions options = new FirefoxOptions();

        options.setBinary(
                "/snap/firefox/current/usr/lib/firefox/firefox"
        );

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        System.setProperty(
                "webdriver.gecko.driver",
                "/usr/local/bin/geckodriver"
        );

        driver = new FirefoxDriver(options);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        driver.get(URL);
    }

    // TEARDOWN: Se ejecuta después de cada prueba
    @AfterEach
    public void cerrarNavegador() {

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // Método auxiliar para filtrar cursos por ID
    private void filtrarPorId(String id) {

        driver.findElement(By.id("add-field-button")).click();

        Select campo = new Select(
                wait.until(d -> d.findElement(By.id("field")))
        );

        campo.selectByValue("id");

        Select operador = new Select(
                driver.findElement(By.id("operator"))
        );

        operador.selectByValue("=");

        WebElement valor = driver.findElement(By.id("value"));
        valor.clear();
        valor.sendKeys(id);

        driver.findElement(By.id("filter-button")).click();
    }

    // CQ01: Consultar un curso existente
    @Test
    public void CQ01_consultarCursoExistente() {

        filtrarPorId(COURSE_ID);

        boolean cursoEncontrado = wait.until(d ->
                d.findElements(By.cssSelector("#courses-list tr"))
                        .stream()
                        .anyMatch(row ->
                                row.getText().contains(COURSE_ID)
                        )
        );

        assertTrue(
                cursoEncontrado,
                "El curso existente debería aparecer en los resultados."
        );

        System.out.println("CQ01 APROBADO: Curso encontrado.");
    }

    // CQ02: Consultar un curso inexistente
    @Test
    public void CQ02_consultarCursoInexistente() {

        String idInexistente =
                "00000000-0000-4000-8000-000000000001";

        filtrarPorId(idInexistente);

        boolean sinResultados = wait.until(d ->
                d.findElements(By.cssSelector("#courses-list tr"))
                        .isEmpty()
        );

        assertTrue(
                sinResultados,
                "No deberían aparecer cursos para un ID inexistente."
        );

        System.out.println(
                "CQ02 APROBADO: No se encontraron cursos."
        );
    }

    // CQ03: Verificar los datos del curso
    @Test
    public void CQ03_verificarDatosDelCurso() {

        filtrarPorId(COURSE_ID);

        WebElement fila = wait.until(d ->
                d.findElements(By.cssSelector("#courses-list tr"))
                        .stream()
                        .filter(row ->
                                row.getText().contains(COURSE_ID)
                        )
                        .findFirst()
                        .orElse(null)
        );

        assertNotNull(
                fila,
                "El curso debería aparecer en la tabla."
        );

        List<WebElement> columnas =
                fila.findElements(By.tagName("td"));

        assertTrue(
                columnas.size() >= 3,
                "La fila debería contener al menos tres columnas."
        );

        assertEquals(
                COURSE_ID,
                columnas.get(0).getText(),
                "El ID del curso no coincide."
        );

        assertEquals(
                COURSE_NAME,
                columnas.get(1).getText(),
                "El nombre del curso no coincide."
        );

        assertEquals(
                COURSE_DURATION,
                columnas.get(2).getText(),
                "La duración del curso no coincide."
        );

        System.out.println(
                "CQ03 APROBADO: Los datos son correctos."
        );
    }
}