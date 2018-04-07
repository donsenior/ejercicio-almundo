package ar.com.almundo.ejercicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.almundo.ejercicio.entities.Call;
import ar.com.almundo.ejercicio.valueobjects.CallData;
import ar.com.almundo.ejercicio.valueobjects.CallStatus;

public class Dispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

	private static final int MAX_CONCURRENT_CALLS = 10;

	private final CallCenter callCenter;

	public Dispatcher(CallCenter callCenter) {
		this.callCenter = callCenter;
	}

	private int currentCallsCount;

	public CallData dispatchCall(Call call) {
		CallData callData = new CallData(call, CallStatus.DISPATCH);

		synchronized (this) {
			checkInCall(callData);
			if (!isAvailable()) {
				callData.setCallStatus(CallStatus.DISPATCHER_NOT_AVAILABLE);
			}
		}

		if (CallStatus.DISPATCH.equals(callData.getCallStatus())) {
			callCenter.dispatch(callData);
		}

		checkOutCall(callData);

		return callData;
	}

	public synchronized boolean isIdle() {
		return currentCallsCount == 0;
	}

	private boolean isAvailable() {
		return currentCallsCount <= MAX_CONCURRENT_CALLS;
	}

	private void checkInCall(CallData callData) {
		currentCallsCount++;
		LOGGER.info("Checking in call {}. Current calls count {}.", callData.getCall().getId(), currentCallsCount);
	}

	private synchronized void checkOutCall(CallData callData) {
		currentCallsCount--;
		LOGGER.info("Checking out call {}. Status {}. Current calls count {}.", callData.getCall().getId(),
				callData.getCallStatus(), currentCallsCount);
	}

}
