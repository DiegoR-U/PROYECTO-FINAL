package pe.edu.ucsp.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import pe.edu.ucsp.functional.pages.CoursesPage;

/**
 * Pruebas funcionales (caja negra) de la funcionalidad "Crear curso" del Backoffice
 * y del contador de cursos que se muestra en la misma pagina.
 *
 * Tecnicas: clases de equivalencia, valores limite y tabla de decision sobre los campos
 * id (UUID), name y duration.
 */
@DisplayName("Funcionalidad: Crear curso (Backoffice)")
class CrearCursoTest {

    private static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:8041");
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private WebDriver   driver;
    private CoursesPage page;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        page = new CoursesPage(driver, BASE_URL).open();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------- Visualizacion de la pagina ----------

    @Test
    @DisplayName("CP01 - La pagina de cursos carga con su titulo y encabezado")
    void cp01_pagina_de_cursos_carga() {
        assertEquals("Courses", page.title());
        assertEquals("Cursos", page.heading());
    }

    @Test
    @DisplayName("CP02 - El formulario propone un identificador UUID valido")
    void cp02_formulario_propone_uuid_valido() {
        assertTrue(page.idValue().matches(UUID_REGEX), "Id propuesto: " + page.idValue());
    }

    @Test
    @DisplayName("CP03 - El contador de cursos se muestra como un entero no negativo")
    void cp03_contador_se_muestra() {
        assertTrue(page.coursesCounter() >= 0);
    }

    // ---------- Clase valida ----------

    @Test
    @DisplayName("CP04 - Crear curso con datos validos no muestra errores")
    void cp04_crear_curso_valido() {
        page.fillForm(UUID.randomUUID().toString(), "DDD en Java", "8 days").submit();

        assertTrue(page.errorMessages().isEmpty(), "Errores: " + page.errorMessages());
        assertTrue(page.currentUrl().endsWith("/courses"));
        assertEquals("", page.nameValue(), "El formulario debe quedar limpio tras crear");
    }

    @Test
    @DisplayName("CP05 - Crear un curso valido incrementa el contador en 1")
    void cp05_crear_curso_incrementa_contador() {
        int before = page.coursesCounter();

        page.fillForm(UUID.randomUUID().toString(), "Testing con Selenium", "5 days").submit();

        assertEquals(before + 1, page.coursesCounter());
    }

    @Test
    @DisplayName("CP06 - Valor limite: nombre y duracion de 1 caracter se aceptan")
    void cp06_valor_limite_un_caracter() {
        page.fillForm(UUID.randomUUID().toString(), "A", "1").submit();

        assertTrue(page.errorMessages().isEmpty(), "Errores: " + page.errorMessages());
    }

    // ---------- Clases invalidas ----------

    @Test
    @DisplayName("CP07 - Nombre vacio muestra error")
    void cp07_nombre_vacio() {
        page.fillForm(UUID.randomUUID().toString(), "", "8 days").submit();

        assertTrue(page.hasErrorFor("name", "should not be empty"), "Errores: " + page.errorsFor("name"));
    }

    @Test
    @DisplayName("CP08 - Duracion vacia muestra error")
    void cp08_duracion_vacia() {
        page.fillForm(UUID.randomUUID().toString(), "DDD en Java", "").submit();

        assertTrue(page.hasErrorFor("duration", "should not be empty"), "Errores: " + page.errorsFor("duration"));
    }

    @Test
    @DisplayName("CP09 - Identificador vacio muestra error de vacio y de UUID")
    void cp09_id_vacio() {
        page.fillForm("", "DDD en Java", "8 days").submit();

        assertTrue(page.hasErrorFor("id", "should not be empty"), "Errores: " + page.errorsFor("id"));
        assertTrue(page.hasErrorFor("id", "is not a valid uuid"), "Errores: " + page.errorsFor("id"));
    }

    @Test
    @DisplayName("CP10 - Identificador con formato no UUID muestra error")
    void cp10_id_formato_invalido() {
        page.fillForm("abc-123", "DDD en Java", "8 days").submit();

        assertTrue(page.hasErrorFor("id", "is not a valid uuid"), "Errores: " + page.errorsFor("id"));
    }

    @Test
    @DisplayName("CP11 - Identificador UUID en mayusculas se rechaza")
    void cp11_id_uuid_mayusculas() {
        page.fillForm(UUID.randomUUID().toString().toUpperCase(), "DDD en Java", "8 days").submit();

        assertTrue(page.hasErrorFor("id", "is not a valid uuid"), "Errores: " + page.errorsFor("id"));
    }

    @Test
    @DisplayName("CP12 - Tabla de decision: todos los campos vacios muestran 4 errores")
    void cp12_todos_los_campos_vacios() {
        page.fillForm("", "", "").submit();

        assertEquals(4, page.errorMessages().size(), "Errores: " + page.errorMessages());
    }

    @Test
    @DisplayName("CP13 - Un curso invalido no incrementa el contador")
    void cp13_curso_invalido_no_incrementa_contador() {
        int before = page.coursesCounter();

        page.fillForm("abc-123", "DDD en Java", "8 days").submit();

        assertEquals(before, page.coursesCounter());
    }

    @Test
    @DisplayName("CP14 - Tras un error se conservan los valores ingresados")
    void cp14_conserva_valores_tras_error() {
        page.fillForm("abc-123", "DDD en Java", "8 days").submit();

        assertEquals("abc-123", page.idValue());
        assertEquals("DDD en Java", page.nameValue());
        assertEquals("8 days", page.durationValue());
    }
}
