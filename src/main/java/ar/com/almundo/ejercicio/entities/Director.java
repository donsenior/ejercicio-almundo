package ar.com.almundo.ejercicio.entities;

import ar.com.almundo.ejercicio.valueobjects.Rol;

public class Director extends Empleado {

	public Director(int legajo) {
		super(legajo);
	}

	@Override
	public Rol getRol() {
		return Rol.DIRECTOR;
	}

}
