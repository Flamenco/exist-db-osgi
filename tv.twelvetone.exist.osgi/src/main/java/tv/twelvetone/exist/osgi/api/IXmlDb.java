package tv.twelvetone.exist.osgi.api;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public interface IXmlDb {

	void startup(String exist_home) throws Exception;

	void shutdown() throws Exception;

	void test() throws Exception;

	Collection getCollection(String url, String username, String password) throws XMLDBException;

	Collection getOrCreateCollection(String collection, String username, String password) throws XMLDBException;

}
