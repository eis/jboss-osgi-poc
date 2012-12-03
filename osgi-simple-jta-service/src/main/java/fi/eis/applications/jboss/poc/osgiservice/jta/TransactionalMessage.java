package fi.eis.applications.jboss.poc.osgiservice.jta;

import javax.transaction.Status;
import javax.transaction.Synchronization;

class TransactionalMessage implements Synchronization {
	private String volatileMessage;
	private String message;

	public void beforeCompletion() {
	}

	public void afterCompletion(int status) {
		if (status == Status.STATUS_COMMITTED)
			message = volatileMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.volatileMessage = message;
	}
}
