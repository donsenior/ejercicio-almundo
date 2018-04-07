package ar.com.almundo.ejercicio;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.almundo.ejercicio.entities.Call;
import ar.com.almundo.ejercicio.entities.Director;
import ar.com.almundo.ejercicio.entities.Operador;
import ar.com.almundo.ejercicio.entities.Supervisor;
import ar.com.almundo.ejercicio.valueobjects.CallData;
import ar.com.almundo.ejercicio.valueobjects.CallStatus;
import ar.com.almundo.ejercicio.valueobjects.Rol;

public class DispatcherTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherTest.class);

	@Before
	public void setUp() {
		LOGGER.info("---------- Inicio del test -------------");
	}

	@After
	public void tearDown() {
		LOGGER.info("---------- Fin del test -------------");
	}

	@Test
	public void testDispatch10Calls9Employees() throws InterruptedException {
		// dado un call center con 9 empleados que recibe 10 llamadas concurrentes
		Dispatcher dispatcher = new Dispatcher(buildCallCenterWithNineEmployees());

		ExecutorService service = Executors.newFixedThreadPool(10); // 10 llamadas concurrentes.

		List<CallData> callResults = new ArrayList<>();

		// cuando
		IntStream.range(1, 11)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10001, TimeUnit.MILLISECONDS);

		// entonces
		List<CallData> successCalls = filterByStatus(callResults, CallStatus.SUCCESS);
		List<CallData> busyCalls = filterByStatus(callResults, CallStatus.BUSY);
		List<CallData> notTakenCalls = filterByStatus(callResults, CallStatus.DISPATCHER_NOT_AVAILABLE);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		// Se recibieron 10 llamados
		assertEquals(10, callResults.size());

		// Nueve fueron exitosas ya que hay 9 empleados
		assertEquals(9, successCalls.size());

		// Una llamada debe dar ocupado ya que falta un empleado para poder atender a
		// todas las llamadas que el dispatcher maneja.
		assertEquals(1, busyCalls.size());

		// Todas las llamadas fueron despachadas correctamente ya que el dispatcher
		// soporta 10 llamadas concurrentes
		assertEquals(0, notTakenCalls.size());
	}

	@Test
	public void testDispatch11Calls11Employees() throws InterruptedException {
		// dado un call center con 11 empleados que recibe 11 llamados concurrentes (más
		// de los que el dispatcher puede manejar)
		Dispatcher dispatcher = new Dispatcher(buildCallCenterWithElevenEmployees());

		ExecutorService service = Executors.newFixedThreadPool(11); // 11 llamadas concurrentes.

		List<CallData> callResults = new ArrayList<>();

		// cuando
		IntStream.range(1, 12)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10001, TimeUnit.MILLISECONDS);

		// entonces
		List<CallData> successCalls = filterByStatus(callResults, CallStatus.SUCCESS);
		List<CallData> busyCalls = filterByStatus(callResults, CallStatus.BUSY);
		List<CallData> notTakenCalls = filterByStatus(callResults, CallStatus.DISPATCHER_NOT_AVAILABLE);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		// Se recibieron 11 llamados
		assertEquals(11, callResults.size());
		
		// Hubo 10 llamadas exitosas ya que hay más empleados que los que el dispatcher
		// puede manejar
		assertEquals(10, successCalls.size());
		
		// Ninguna llamada dio ocupado, por el mismo motivo de antes, sobran
		// empleados...
		assertEquals(0, busyCalls.size());
		
		// Una llamada no pudo ser despachada ya que solo se reciben hasta 10
		assertEquals(1, notTakenCalls.size());
	}

	@Test
	public void testDispatchCallToOperatorsAndOneSupervisor() throws InterruptedException {
		// dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenterWithNineEmployees());

		ExecutorService service = Executors.newFixedThreadPool(5);

		List<CallData> callResults = new ArrayList<>();

		// cuando hay solo 5 llamadas
		IntStream.range(1, 6)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10001, TimeUnit.MILLISECONDS);

		// entonces
		List<CallData> operatorsCalls = filterByRol(callResults, Rol.OPERADOR);
		List<CallData> supervisorsCalls = filterByRol(callResults, Rol.SUPERVISOR);
		List<CallData> directorCalls = filterByRol(callResults, Rol.DIRECTOR);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());

		// Se recibieron 5 llamados
		assertEquals(5, callResults.size());
		
		// cuatro fueron atendidas por operadores...
		assertEquals(4, operatorsCalls.size());
		
		// una fue atendida por un supervisor
		assertEquals(1, supervisorsCalls.size());
		
		// ningún director tuvo que atender llamadas.
		assertEquals(0, directorCalls.size());
	}

	@Test
	public void testDispatchCallToOperatorsOnly() throws InterruptedException {
		// dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenterWithNineEmployees());

		ExecutorService service = Executors.newFixedThreadPool(4);

		List<CallData> callResults = new ArrayList<>();

		// cuando hay solo 4 llamadas
		IntStream.range(1, 5)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10001, TimeUnit.MILLISECONDS);

		// entonces
		List<CallData> operatorsCalls = filterByRol(callResults, Rol.OPERADOR);
		List<CallData> supervisorsCalls = filterByRol(callResults, Rol.SUPERVISOR);
		List<CallData> directorCalls = filterByRol(callResults, Rol.DIRECTOR);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		// Se recibieron 4 llamados
		assertEquals(4, callResults.size());
		
		// solo los operadores atendieron.
		assertEquals(4, operatorsCalls.size());
		assertEquals(0, supervisorsCalls.size());
		assertEquals(0, directorCalls.size());

	}

	/*
	 * Se construye un call center con 9 empleados: 4 operadores, 3 supervisores y 2
	 * directores,
	 * 
	 */
	private CallCenter buildCallCenterWithNineEmployees() {
		Operador[] operadores = { new Operador(1), new Operador(2), new Operador(3), new Operador(4) };
		Supervisor[] supervisores = { new Supervisor(5), new Supervisor(6), new Supervisor(7) };
		Director[] directores = { new Director(8), new Director(9) };

		return new CallCenter(asList(operadores), asList(supervisores), asList(directores));
	}

	/*
	 * Se construye un call center con 11 empleados: 6 operadores, 3 supervisores y
	 * 2 directores,
	 * 
	 */
	private CallCenter buildCallCenterWithElevenEmployees() {
		Operador[] operadores = { new Operador(1), new Operador(2), new Operador(3), new Operador(4), new Operador(5),
				new Operador(6) };
		Supervisor[] supervisores = { new Supervisor(7), new Supervisor(8), new Supervisor(9) };
		Director[] directores = { new Director(10), new Director(11) };

		return new CallCenter(asList(operadores), asList(supervisores), asList(directores));
	}

	private List<CallData> filterByRol(List<CallData> callResults, Rol rol) {
		List<CallData> operatorsCalls = callResults.stream().filter(c -> c.getAttendant().get().getRol().equals(rol))
				.collect(Collectors.toList());
		return operatorsCalls;
	}

	private List<CallData> filterByStatus(List<CallData> callResults, CallStatus callStatus) {
		List<CallData> successCalls = callResults.stream().filter(c -> c.getCallStatus().equals(callStatus))
				.collect(Collectors.toList());
		return successCalls;
	}

}
