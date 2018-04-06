package ar.com.almundo.ejercicio;

import ar.com.almundo.ejercicio.entities.Call;
import ar.com.almundo.ejercicio.valueobjects.CallData;
import ar.com.almundo.ejercicio.valueobjects.CallStatus;

public class Dispatcher {

	private static final int MAX_CONCURRENT_CALLS = 10;

	private final CallCenter callCenter;

	public Dispatcher(CallCenter callCenter) {
		this.callCenter = callCenter;
	}

	private int currentCallsCount;

	public CallData dispatchCall(Call call) {
		CallData callData = new CallData(call, CallStatus.DISPATCH);

		synchronized (this) {
			checkInCall();
			if (!isDispatcherAvailable()) {
				callData.setCallStatus(CallStatus.DISPATCHER_NOT_AVAILABLE);
			}
		}

		if (CallStatus.DISPATCH.equals(callData.getCallStatus())) {
			doDispatch(callData);
		}

		checkOutCall();

		return callData;
	}

	private CallData doDispatch(CallData callData) {
		return callCenter.dispatch(callData);
	}

	private boolean isDispatcherAvailable() {
		return currentCallsCount <= MAX_CONCURRENT_CALLS;
	}

	private void checkInCall() {
		currentCallsCount++;
	}

	private synchronized void checkOutCall() {
		currentCallsCount--;
	}

}
