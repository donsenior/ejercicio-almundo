package ar.com.almundo.ejercicio;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.almundo.ejercicio.entities.Director;
import ar.com.almundo.ejercicio.entities.Empleado;
import ar.com.almundo.ejercicio.entities.Operador;
import ar.com.almundo.ejercicio.entities.Supervisor;
import ar.com.almundo.ejercicio.valueobjects.CallData;
import ar.com.almundo.ejercicio.valueobjects.CallStatus;
import ar.com.almundo.ejercicio.valueobjects.Empleados;

public class CallCenter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CallCenter.class);

	private final Empleados<Operador> operators;
	private final Empleados<Supervisor> supervisors;
	private final Empleados<Director> directors;

	public CallCenter(Collection<Operador> operators, Collection<Supervisor> supervisors,
			Collection<Director> directors) {
		this.operators = new Empleados<>(operators);
		this.supervisors = new Empleados<>(supervisors);
		this.directors = new Empleados<>(directors);
	}

	public CallData dispatch(CallData callData) {
		Optional<Empleados<? extends Empleado>> empleados;
		Optional<Empleado> optionalEmpleado;

		synchronized (this) {
			empleados = getAvailableEmpleados();
			optionalEmpleado = getAttendant(empleados);
		}

		if (optionalEmpleado.isPresent()) {
			callData.setAttendant(optionalEmpleado);
			emulateCall(callData, optionalEmpleado.get());
			callData.setCallStatus(CallStatus.SUCCESS);
			empleados.get().hang(optionalEmpleado.get());
		} else {
			callData.setCallStatus(CallStatus.BUSY);
		}

		return callData;
	}

	private void emulateCall(CallData callData, Empleado empleado) {
		int callDuration = ThreadLocalRandom.current().nextInt(5000, 10001);
		LOGGER.debug("Llamada {} atendida por {} con legajo {}", callData.getCall().getId(), empleado.getRol(),
				empleado.getLegajo());
		try {
			Thread.sleep(callDuration);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
			Thread.currentThread().interrupt();
		}
		LOGGER.debug("Llamada {} finalizada. Duración: {} milisegundos.", callData.getCall().getId(), callDuration);
	}

	private Optional<Empleado> getAttendant(Optional<Empleados<? extends Empleado>> empleados) {

		if (empleados.isPresent()) {
			return getRandomEmpleado(empleados.get());
		}

		return Optional.empty();
	}

	private Optional<Empleado> getRandomEmpleado(Empleados<? extends Empleado> empleados) {
		int randomNum = ThreadLocalRandom.current().nextInt(0, empleados.size());

		Empleado empleado = empleados.answer(randomNum);

		return Optional.of(empleado);
	}

	private Optional<Empleados<? extends Empleado>> getAvailableEmpleados() {
		if (!operators.isEmpty()) {
			return Optional.of(operators);
		}

		if (!supervisors.isEmpty()) {
			return Optional.of(supervisors);
		}

		if (!directors.isEmpty()) {
			return Optional.of(directors);
		}

		return Optional.empty();
	}

}
