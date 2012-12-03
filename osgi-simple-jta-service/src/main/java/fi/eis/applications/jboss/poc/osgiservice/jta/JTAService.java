package fi.eis.applications.jboss.poc.osgiservice.jta;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import fi.eis.applications.jboss.poc.osgiservice.api.ConvertService;

public class JTAService implements ConvertService {

	private BundleContext context = FrameworkUtil.getBundle(this.getClass())
			.getBundleContext();

	private Logger log = Logger.getLogger(JTAService.class);

	/**
	 * Example from
	 * @url https://github.com/jbosgi/jbosgi/blob/master/testsuite/example/src/test/java/org/jboss/test/osgi/example/jta/TransactionTestCase.java
	 */
	@Override
	public String convertMessage(String message) {
		TransactionalMessage txObj = new TransactionalMessage();

		ServiceReference userTxRef = context
				.getServiceReference(UserTransaction.class.getName());
		log.debug("userTxRef=" + userTxRef);

		UserTransaction userTx = (UserTransaction) context
				.getService(userTxRef);

		log.debug("userTx=" + userTx);

		try {
			userTx.begin();
			ServiceReference tmRef = context
					.getServiceReference(TransactionManager.class.getName());
			log.debug("tmRef=" + tmRef);

			TransactionManager tm = (TransactionManager) context
					.getService(tmRef);
			log.debug("tm=" + tm);

			Transaction tx = tm.getTransaction();
			log.debug("tx=" + tx);

			tx.registerSynchronization(txObj);

			txObj.setMessage(message + " - pls donate $1.000.000");
			log.debug("message=" + txObj.getMessage());

			userTx.commit();
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (HeuristicMixedException e) {
			throw new IllegalStateException(e);
		} catch (HeuristicRollbackException e) {
			throw new IllegalStateException(e);
		} catch (NotSupportedException e) {
			throw new IllegalStateException(e);
		} catch (SystemException e) {
			throw new IllegalStateException(e);
		} catch (RollbackException e) {
			throw new IllegalStateException(e);
		}

		return txObj.getMessage();
	}

}
