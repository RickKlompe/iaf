/*
 * $Log: MessagingSourceFactory.java,v $
 * Revision 1.3  2011-11-30 13:51:51  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2010/01/28 14:48:42  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * renamed 'Connection' classes to 'MessageSource'
 *
 * Revision 1.9  2008/07/24 12:20:00  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * added support for authenticated JMS
 *
 * Revision 1.8  2007/10/08 12:20:04  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * changed HashMap to Map where possible
 *
 * Revision 1.7  2007/05/23 09:13:51  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * improved logging
 *
 * Revision 1.6  2007/02/12 13:58:11  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * Logger from LogUtil
 *
 * Revision 1.5  2006/12/13 16:28:05  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * catch more exceptions
 *
 * Revision 1.4  2005/10/26 08:21:42  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * renamed createJmsConnection() into createConnection()
 *
 * Revision 1.3  2005/10/20 15:35:25  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * corrected version String
 *
 * Revision 1.2  2005/10/20 15:34:09  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * renamed JmsConnection into ConnectionBase
 *
 * Revision 1.1  2005/05/03 15:59:55  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * rework of shared connection code
 *
 */
package nl.nn.adapterframework.jms;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import nl.nn.adapterframework.core.IbisException;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.log4j.Logger;

/**
 * Factory for {@link MessagingSource}s, to share them for JMS Objects that can use the same. 
 * 
 * @author Gerrit van Brakel
 * @version Id
 */
public abstract class MessagingSourceFactory  {
	protected Logger log = LogUtil.getLogger(this);

	protected abstract Map getMessagingSourceMap();
	protected abstract Context createContext() throws NamingException;
	protected abstract ConnectionFactory createConnectionFactory(Context context, String id) throws IbisException, NamingException;
	
	protected MessagingSource createMessagingSource(String id, String authAlias) throws IbisException {
		Context context = getContext();
		ConnectionFactory connectionFactory = getConnectionFactory(context, id); 
		return new MessagingSource(id, context, connectionFactory, getMessagingSourceMap(), authAlias);
	}
	
	public synchronized MessagingSource getMessagingSource(String id, String authAlias) throws IbisException {
		Map messagingSourceMap = getMessagingSourceMap();
		MessagingSource result = (MessagingSource)messagingSourceMap.get(id);
		if (result == null) {
			result = createMessagingSource(id, authAlias);
			log.debug("created new MessagingSource-object for ["+id+"]");
		}
		result.increaseReferences();
		return result;
	}
	
	protected Context getContext() throws IbisException {
		try {
			return createContext();
		} catch (Throwable t) {
			throw new IbisException("could not obtain context", t);
		}
	}

	protected ConnectionFactory getConnectionFactory(Context context, String id) throws IbisException {
		try {
			return createConnectionFactory(context, id);
		} catch (Throwable t) {
			throw new IbisException("could not obtain connectionFactory ["+id+"]", t);
		}
	}
	
}