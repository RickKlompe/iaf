/*
 * $Log: ShowTracingConfiguration.java,v $
 * Revision 1.5  2011-11-30 13:51:46  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2008/10/24 14:42:31  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * adapters are shown case insensitive sorted
 *
 * Revision 1.2  2007/07/19 15:17:21  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * list Adapters in order of configuration
 *
 * Revision 1.1  2006/09/14 15:29:44  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * first version of TracingHandlers
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.core.Adapter;
import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.pipes.AbstractPipe;
import nl.nn.adapterframework.receivers.ReceiverBase;
import nl.nn.adapterframework.util.XmlBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <code>Action</code> to retrieve the Tracing configuration.
 * @author  Peter Leeuwenburgh
 * @version Id
 */
public final class ShowTracingConfiguration extends ActionBase {
	public static final String version="$RCSfile: ShowTracingConfiguration.java,v $ $Revision: 1.5 $ $Date: 2011-11-30 13:51:46 $";

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException, ServletException {

		// Initialize action
		initAction(request);

		if (null == config) {
			return (mapping.findForward("noconfig"));
		}

		XmlBuilder tracingConfigurationXML =
			new XmlBuilder("tracingConfiguration");

		XmlBuilder adapters = new XmlBuilder("registeredAdapters");
		for(int j=0; j<config.getRegisteredAdapters().size(); j++) {
			Adapter adapter = (Adapter)config.getRegisteredAdapter(j);

			XmlBuilder adapterXML = new XmlBuilder("adapter");
			adapterXML.addAttribute("name", adapter.getName());
			adapterXML.addAttribute("nameUC",StringUtils.upperCase(adapter.getName()));
			Iterator recIt = adapter.getReceiverIterator();
			if (recIt.hasNext()) {
				XmlBuilder receiversXML = new XmlBuilder("receivers");
				while (recIt.hasNext()) {
					ReceiverBase receiver = (ReceiverBase) recIt.next();
					XmlBuilder receiverXML = new XmlBuilder("receiver");
					receiversXML.addSubElement(receiverXML);
					receiverXML.addAttribute("name", receiver.getName());
					receiverXML.addAttribute("beforeEvent",	Integer.toString(receiver.getBeforeEvent()));
					receiverXML.addAttribute("afterEvent",	Integer.toString(receiver.getAfterEvent()));
					receiverXML.addAttribute("exceptionEvent", Integer.toString(receiver.getExceptionEvent()));
				}
				adapterXML.addSubElement(receiversXML);
			}

			XmlBuilder pipelineXML = new XmlBuilder("pipeline");
			
			List pipeList = adapter.getPipeLine().getPipes();
			if (pipeList.size()>0) {
				for (int i=0; i<pipeList.size(); i++) {
					IPipe pipe = adapter.getPipeLine().getPipe(i);
	
					XmlBuilder pipeXML = new XmlBuilder("pipe");
					pipeXML.addAttribute("name", pipe.getName());
					if (pipe instanceof AbstractPipe) {
						AbstractPipe ap = (AbstractPipe)pipe;
						pipeXML.addAttribute("beforeEvent",	Integer.toString(ap.getBeforeEvent()));
						pipeXML.addAttribute("afterEvent",  Integer.toString(ap.getAfterEvent()));
						pipeXML.addAttribute("exceptionEvent",	Integer.toString(ap.getExceptionEvent()));
		
					}
					pipelineXML.addSubElement(pipeXML);
				}
				adapterXML.addSubElement(pipelineXML);
			}
			adapters.addSubElement(adapterXML);
		}

		tracingConfigurationXML.addSubElement(adapters);

		request.setAttribute(
			"tracingConfiguration",
			tracingConfigurationXML.toXML());

		// Forward control to the specified success URI
		log.debug("forward to success");
		return (mapping.findForward("success"));
	}
}