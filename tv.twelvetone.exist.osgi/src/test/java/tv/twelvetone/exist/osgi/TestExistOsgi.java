package tv.twelvetone.exist.osgi;

import org.junit.Test;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;

import tv.twelvetone.exist.osgi.internal.XmlDbUtil;

public class TestExistOsgi {

	@Test
	public void test() throws Exception {
		try {
			System.setProperty("exist_home", "test-exist-home");
			System.setProperty("log4j.configuration", "file:test-exist-home/log4j.xml");
			
			String exist_home = System.getProperty("exist_home");
			XmlDbUtil.getInstance().startup(exist_home);

			Collection col = XmlDbUtil.getInstance().getOrCreateCollection("/db/test", "admin", "");

			for (int i = 0; i < 10; i++) {
				XMLResource res = (XMLResource) col.createResource("" + i, "XMLResource");
				res.setContent("<item>" + i + "</item>");
				col.storeResource(res);
			}

			String resources[] = col.listResources();
			System.out.println("Resources in /db/test:");
			for (int i = 0; i < resources.length; i++) {
				System.out.println(((XMLResource) col.getResource(resources[i])).getContent().toString());
			}
		} finally {
			try {
				XmlDbUtil.getInstance().shutdown();
			} catch (Exception e) {
			}
		}
	}

}
