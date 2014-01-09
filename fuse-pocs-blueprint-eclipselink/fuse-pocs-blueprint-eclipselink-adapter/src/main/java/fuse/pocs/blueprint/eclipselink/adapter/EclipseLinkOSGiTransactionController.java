package fuse.pocs.blueprint.eclipselink.adapter;

import org.eclipse.persistence.transaction.JTATransactionController;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import javax.transaction.TransactionManager;

/**
 *
 */
public class EclipseLinkOSGiTransactionController extends JTATransactionController {

    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        Bundle bundle = FrameworkUtil.getBundle(EclipseLinkOSGiTransactionController.class);
        BundleContext ctx = bundle.getBundleContext();

        if (ctx != null) {
            ServiceReference ref = ctx.getServiceReference(TransactionManager.class.getName());

            if (ref != null) {
                TransactionManager manager = (TransactionManager) ctx.getService(ref);
                return manager;
            }
        }

        return super.acquireTransactionManager();
    }

}

