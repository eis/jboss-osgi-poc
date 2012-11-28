package fi.eis.applications.jboss.poc.osgiservice.api;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

/**
 * 
 * @url https://github.com/tdiesler/jbosgi/blob/d7caf3126fb35b823d083b238e7d974b06865396/testsuite/jbossas/api/src/main/java/org/jboss/test/osgi/jbossas/example/payment/BundleContextProvider.java
 */

public abstract class BundleContextProvider {

    public static BundleContext getBundleContext() {
        ClassLoader classLoader = BundleContextProvider.class.getClassLoader();
        Bundle bundle = ((BundleReference) classLoader).getBundle();
        BundleContext context = bundle.getBundleContext();
        return context.getBundle(0).getBundleContext();
    };
}
