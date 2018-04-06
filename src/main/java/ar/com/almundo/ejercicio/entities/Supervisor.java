package ar.com.almundo.ejercicio.entities;

import ar.com.almundo.ejercicio.valueobjects.Rol;

public class Supervisor extends Empleado {

	public Supervisor(int legajo) {
		super(legajo);
	}

	@Override
	public Rol getRol() {
		return Rol.SUPERVISOR;
	}

}
