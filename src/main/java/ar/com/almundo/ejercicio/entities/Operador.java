package ar.com.almundo.ejercicio.entities;

import ar.com.almundo.ejercicio.valueobjects.Rol;

public class Operador extends Empleado {

	public Operador(int legajo) {
		super(legajo);
	}

	@Override
	public Rol getRol() {
		return Rol.OPERADOR;
	}

}
