package ar.com.almundo.ejercicio.valueobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ar.com.almundo.ejercicio.entities.Empleado;

public class Empleados<T extends Empleado> {

	private final List<T> availables = new ArrayList<>();
	private final List<T> unavailables = new ArrayList<>();

	public Empleados(Collection<T> empleados) {
		availables.addAll(empleados);
	}

	public Empleado answer(int index) {
		T empleado = availables.remove(index);
		unavailables.add(empleado);
		return empleado;
	}

	public void hang(Empleado empleado) {
		int index = unavailables.indexOf(empleado);
		T available = unavailables.remove(index);
		availables.add(available);
	}

	public boolean isEmpty() {
		return availables.isEmpty();
	}

	public int size() {
		return availables.size();
	}

}
