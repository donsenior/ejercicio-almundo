package ar.com.almundo.ejercicio.valueobjects;

import java.util.List;
import java.util.Vector;

import ar.com.almundo.ejercicio.entities.Empleado;

public class Empleados<T extends Empleado> {

	private final List<T> availables = new Vector<>();
	private final List<T> unavailables = new Vector<>();

	public List<T> getAvailables() {
		return availables;
	}

	public List<T> getUnavailables() {
		return unavailables;
	}
	
	public Empleado answer(int index) {
		T empleado = availables.remove(index);
		unavailables.add(empleado);
		return empleado;
	}

}
