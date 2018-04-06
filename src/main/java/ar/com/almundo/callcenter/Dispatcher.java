package ar.com.almundo.callcenter;

import ar.com.almundo.callcenter.entities.Call;
import ar.com.almundo.callcenter.valueobjects.CallResult;

public class Dispatcher {

	private static final int MAX_CONCURRENT_CALLS = 10;

	private int currentCallsCount;

	public CallResult dispatchCall(Call call) {
		CallResult callResult = CallResult.DISPATCH;

		synchronized (this) {
			checkInCall();
			if (!isDispatcheAvailable()) {
				callResult = CallResult.BUSY;
			}
		}

		if (CallResult.DISPATCH.equals(callResult)) {
			callResult = doDispatch(call);
		}

		checkOutCall();
		return callResult;
	}

	private CallResult doDispatch(Call call) {
		return null;
	}

	private boolean isDispatcheAvailable() {
		return currentCallsCount < MAX_CONCURRENT_CALLS;
	}

	private void checkInCall() {
		currentCallsCount++;
	}

	private synchronized void checkOutCall() {
		currentCallsCount--;
	}

}
