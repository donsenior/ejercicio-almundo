package ar.com.almundo.ejercicio.entities;

import ar.com.almundo.ejercicio.valueobjects.Rol;

public abstract class Empleado {

	private final int legajo;

	public Empleado(int legajo) {
		this.legajo = legajo;
	}

	public int getLegajo() {
		return legajo;
	}

	public abstract Rol getRol();

}
