package ar.com.almundo.ejercicio;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

import ar.com.almundo.ejercicio.entities.Call;
import ar.com.almundo.ejercicio.entities.Director;
import ar.com.almundo.ejercicio.entities.Operador;
import ar.com.almundo.ejercicio.entities.Supervisor;
import ar.com.almundo.ejercicio.valueobjects.CallData;

public class DispatcherTest {


	@Test
	public void testDispatchCall() throws InterruptedException {
		//dado
		Dispatcher dispatcher = new Dispatcher(buildCallCenter());
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		
		List<CallData> callResults = new ArrayList<>();
		//cuando
		
		IntStream.range(0, 11)
	      .forEach(count -> service.submit(() -> callResults.add(dispatcher.dispatchCall(new Call(count)))));
		
		service.awaitTermination(50000, TimeUnit.MILLISECONDS);
		
		//entonces
		assertTrue("El dispatcher deberia estar idle", dispatcher.isIdle());
		assertEquals(10, callResults.size());
	}

	private CallCenter buildCallCenter() {
		Operador[] operadores = { new Operador(1), new Operador(2), new Operador(3), new Operador(4) };
		Supervisor[] supervisores = { new Supervisor(5), new Supervisor(6), new Supervisor(7) };
		Director[] directores = { new Director(8), new Director(9) };
		
		return new CallCenter(asList(operadores), asList(supervisores), asList(directores));
	}

}
