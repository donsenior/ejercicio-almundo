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
	public void testDispatchAllCalls() throws InterruptedException {
		// dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenter());

		ExecutorService service = Executors.newFixedThreadPool(10);

		List<CallData> callResults = new ArrayList<>();
		// cuando

		IntStream.range(1, 11)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10000, TimeUnit.MILLISECONDS);
		
		List<CallData> successCalls = filterByStatus(callResults, CallStatus.SUCCESS);
		List<CallData> busyCalls = filterByStatus(callResults, CallStatus.BUSY);

		// entonces
		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		assertEquals(10, callResults.size());
		assertEquals(9, successCalls.size());
		assertEquals(1, busyCalls.size());
	}

	@Test
	public void testDispatchCallToOperatorsAndOneSupervisor() throws InterruptedException {
		// dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenter());

		ExecutorService service = Executors.newFixedThreadPool(10);

		List<CallData> callResults = new ArrayList<>();
		// cuando

		IntStream.range(1, 6)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10000, TimeUnit.MILLISECONDS);

		List<CallData> operatorsCalls = filterByRol(callResults, Rol.OPERADOR);
		List<CallData> supervisorsCalls = filterByRol(callResults, Rol.SUPERVISOR);
		List<CallData> directorCalls = filterByRol(callResults, Rol.DIRECTOR);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		assertEquals(4, operatorsCalls.size());
		assertEquals(1, supervisorsCalls.size());
		assertEquals(0, directorCalls.size());
	}

	@Test
	public void testDispatchCallToOperatorsOnly() throws InterruptedException {
		// dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenter());

		ExecutorService service = Executors.newFixedThreadPool(10);

		List<CallData> callResults = new ArrayList<>();
		// cuando

		IntStream.range(1, 5)
				.forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));

		service.shutdown();
		service.awaitTermination(10000, TimeUnit.MILLISECONDS);

		// entonces
		List<CallData> operatorsCalls = filterByRol(callResults, Rol.OPERADOR);
		List<CallData> supervisorsCalls = filterByRol(callResults, Rol.SUPERVISOR);
		List<CallData> directorCalls = filterByRol(callResults, Rol.DIRECTOR);

		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		assertEquals(4, operatorsCalls.size());
		assertEquals(0, supervisorsCalls.size());
		assertEquals(0, directorCalls.size());
		
	}

	private CallCenter buildCallCenter() {
		Operador[] operadores = { new Operador(1), new Operador(2), new Operador(3), new Operador(4) };
		Supervisor[] supervisores = { new Supervisor(5), new Supervisor(6), new Supervisor(7) };
		Director[] directores = { new Director(8), new Director(9) };

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
