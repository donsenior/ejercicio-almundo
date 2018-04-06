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

	private Empleados<Operador> operators = new Empleados<>();
	private Empleados<Supervisor> supervisors = new Empleados<>();
	private Empleados<Director> directors = new Empleados<>();

	public CallCenter(Collection<Operador> operators, Collection<Supervisor> supervisors,
			Collection<Director> directors) {
		this.operators.getAvailables().addAll(operators);
		this.supervisors.getAvailables().addAll(supervisors);
		this.directors.getAvailables().addAll(directors);
	}

	public CallData dispatch(CallData callData) {
		Optional<Empleado> optionalEmpleado = getAttendant();
		
		if (optionalEmpleado.isPresent()) {
			callData.setAttendant(optionalEmpleado);
			int callDuration = ThreadLocalRandom.current().nextInt(5000, 10001);
			LOGGER.debug("Llamada atendida por {} con legajo {}", optionalEmpleado.get().getRol(), optionalEmpleado.get().getLegajo());
			try {
				Thread.sleep(callDuration);
			} catch (InterruptedException e) {
				LOGGER.error("", e);
				Thread.currentThread().interrupt();
			}
			LOGGER.debug("Llamada atendida por {} con legajo {} finalizada. Duración: {}", optionalEmpleado.get().getRol(), optionalEmpleado.get().getLegajo(), callDuration);
			callData.setCallStatus(CallStatus.SUCCESS);
		} else {
			callData.setCallStatus(CallStatus.BUSY);
		}

		return callData;
	}

	private synchronized Optional<Empleado> getAttendant() {
		Optional<Empleados<? extends Empleado>> empleados = getAvailableEmpleados();

		if (empleados.isPresent()) {
			return getRandomEmpleado(empleados.get());
		}

		return Optional.empty();
	}

	private Optional<Empleado> getRandomEmpleado(Empleados<? extends Empleado> empleados) {
		int randomNum = ThreadLocalRandom.current().nextInt(0, empleados.getAvailables().size());
		
		Empleado empleado = empleados.answer(randomNum);
		
		return Optional.of(empleado);
	}

	private Optional<Empleados<? extends Empleado>> getAvailableEmpleados() {
		if (!operators.getAvailables().isEmpty()) {
			return Optional.of(operators);
		}

		if (!supervisors.getAvailables().isEmpty()) {
			return Optional.of(supervisors);
		}

		if (!directors.getAvailables().isEmpty()) {
			return Optional.of(directors);
		}

		return Optional.empty();
	}

}
