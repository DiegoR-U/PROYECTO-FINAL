package pe.edu.ucsp.functional;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/** Test Suite (xUnit) que agrupa las pruebas funcionales de "Crear curso". */
@Suite
@SuiteDisplayName("Suite de pruebas funcionales - Crear curso")
@SelectClasses({ CrearCursoTest.class })
public class CrearCursoSuite {
}
