/*
 * $Log: JdbcException.java,v $
 * Revision 1.7  2011-11-30 13:51:43  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2006/12/12 09:58:41  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * fix version string
 *
 * Revision 1.4  2006/12/12 09:57:35  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * restore jdbc package
 *
 * Revision 1.2  2004/03/26 10:43:07  Johan Verrips <johan.verrips@ibissource.org>
 * added @version tag in javadoc
 *
 * Revision 1.1  2004/03/24 13:28:20  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * initial version
 *
 */
package nl.nn.adapterframework.jdbc;

import nl.nn.adapterframework.core.IbisException;

/**
 * Wrapper for JDBC related exceptions.
 * 
 * @version Id
 * @author Gerrit van Brakel
 * @since  4.1
 */
public class JdbcException extends IbisException {
	public static final String version = "$RCSfile: JdbcException.java,v $ $Revision: 1.7 $ $Date: 2011-11-30 13:51:43 $";

	public JdbcException() {
		super();
	}

	public JdbcException(String arg1) {
		super(arg1);
	}

	public JdbcException(String arg1, Throwable arg2) {
		super(arg1, arg2);
	}

	public JdbcException(Throwable arg1) {
		super(arg1);
	}

}