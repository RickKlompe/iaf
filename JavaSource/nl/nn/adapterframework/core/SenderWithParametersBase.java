/*
 * $Log: SenderWithParametersBase.java,v $
 * Revision 1.8  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:46  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2010/12/13 13:14:07  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * removed unused code
 * added documentation
 *
 * Revision 1.5  2009/12/04 18:23:34  Jaco de Groot <jaco.de.groot@ibissource.org>
 * Added ibisDebugger.senderAbort and ibisDebugger.pipeRollback
 *
 * Revision 1.4  2007/02/26 16:53:38  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * add throws clause to open and close
 *
 * Revision 1.3  2007/02/12 13:44:09  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * Logger from LogUtil
 *
 * Revision 1.2  2005/08/30 15:55:43  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * added log and getLogPrefix()
 *
 * Revision 1.1  2005/06/20 08:58:13  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * introduction of SenderWithParametersBase
 *
 *
 */
package nl.nn.adapterframework.core;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.senders.SenderBase;

/**
 * Provides a base class for senders with parameters.
 * 
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Sender</td><td>&nbsp;</td></tr>
 * </table>
 * 
 * @author Gerrit van Brakel
 * @since  4.3
 * @version Id
 */
public abstract class SenderWithParametersBase extends SenderBase implements ISenderWithParameters {
	public static final String version="$RCSfile: SenderWithParametersBase.java,v $ $Revision: 1.8 $ $Date: 2011-11-30 13:51:55 $";
	
	protected ParameterList paramList = null;

	public void configure() throws ConfigurationException {
		if (paramList!=null) {
			paramList.configure();
		}
	}

	public String sendMessage(String correlationID, String message) throws SenderException, TimeOutException  {
		return sendMessage(correlationID,message,null);
	}

	public void addParameter(Parameter p) {
		if (paramList==null) {
			paramList=new ParameterList();
		}
		paramList.add(p);
	}

}