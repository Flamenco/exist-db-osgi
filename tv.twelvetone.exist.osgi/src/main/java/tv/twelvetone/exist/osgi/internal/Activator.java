package tv.twelvetone.exist.osgi.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import tv.twelvetone.exist.osgi.api.IXmlDb;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(IXmlDb.class, XmlDbUtil.getInstance(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
