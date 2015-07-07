package tv.twelvetone.exist.osgi.internal;

import java.io.File;

import org.exist.xmldb.DatabaseInstanceManager;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import tv.twelvetone.exist.osgi.api.IXmlDb;

public class XmlDbUtil implements IXmlDb {

	public static String URI = "xmldb:exist:///";
	public static boolean initialized = false;
	private static XmlDbUtil instance;

	static public XmlDbUtil getInstance() {
		if (instance == null) {
			instance = new XmlDbUtil();
		}
		return instance;
	}

	@Override
	public void startup(String exist_home) throws Exception {
		if (initialized) {
			throw new InstantiationException("The database is already initialized.");
		}
		File file = new File(exist_home);
		if (!file.isDirectory()) {
			throw new InstantiationException("Database home directory '" + exist_home + "' was not found");
		}
		System.setProperty("exist.initdb", "true");
		System.setProperty("exist.home", exist_home);

		Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		initialized = true;
	}

	@Override
	public void shutdown() throws Exception {
		if (!initialized) {
			return;
		}
		Collection col;
		try {
			col = DatabaseManager.getCollection(XmlDbUtil.URI + "/db", "admin", "");
			DatabaseInstanceManager manager = (DatabaseInstanceManager) col.getService("DatabaseInstanceManager", "1.0");
			manager.shutdown();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		initialized = false;
	}

	public Collection getOrCreateCollection(String collectionUri, String username, String password) throws XMLDBException {
		return getOrCreateCollection(collectionUri, 0, username, password);
	}

	private Collection getOrCreateCollection(String collectionUri, int pathSegmentOffset, String username, String password) throws XMLDBException {

		Collection col = DatabaseManager.getCollection(URI + collectionUri, username, password);
		if (col == null) {
			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}

			String pathSegments[] = collectionUri.split("/");
			if (pathSegments.length > 0) {

				StringBuilder path = new StringBuilder();
				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}

				Collection start = DatabaseManager.getCollection(URI + path, username, password);
				if (start == null) {
					//collection does not exist, so create
					String parentPath = path.substring(0, path.lastIndexOf("/"));
					Collection parent = DatabaseManager.getCollection(URI + parentPath, username, password);
					CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parent.close();
				} else {
					start.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset, username, password);
		} else {
			return col;
		}
	}

	/**
	 * Creates resources under /db/test.
	 * 
	 * @throws XMLDBException
	 */
	@Override
	public void test() throws Exception {

		Collection col = getOrCreateCollection("/db/test", "admin", "");

		for (int i = 0; i < 10; i++) {
			XMLResource res = (XMLResource) col.createResource("" + i, "XMLResource");
			res.setContent("<items><item>" + i + "</item></items>");
			col.storeResource(res);
		}

		String resources[] = col.listResources();
		System.out.println("Resources:");
		for (int i = 0; i < resources.length; i++) {
			System.out.println(resources[i]);
			System.out.println(((XMLResource) col.getResource(resources[i])).getContent().toString());
		}
	}

	@Override
	public Collection getCollection(String url, String username, String password) throws XMLDBException {
		try {
			return DatabaseManager.getCollection(url, username, password);
		} catch (XMLDBException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}
