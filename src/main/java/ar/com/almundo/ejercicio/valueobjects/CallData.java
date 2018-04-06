package ar.com.almundo.ejercicio.valueobjects;

import java.util.Optional;

import ar.com.almundo.ejercicio.entities.Call;
import ar.com.almundo.ejercicio.entities.Empleado;

public class CallData {

	private final Call call;
	private CallStatus callStatus;
	private Optional<Empleado> attendant;

	public CallData(Call call) {
		this.call = call;
	}

	public CallData(Call call, CallStatus status) {
		this(call);
		this.callStatus = status;
	}

	public CallStatus getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(CallStatus status) {
		this.callStatus = status;
	}

	public Optional<Empleado> getAttendant() {
		return attendant;
	}

	public void setAttendant(Optional<Empleado> attendant) {
		this.attendant = attendant;
	}

	public Call getCall() {
		return call;
	}

}
