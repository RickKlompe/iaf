/*
 * $Log: TracingHandler.java,v $
 * Revision 1.4  2011-11-30 13:51:46  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.2  2008/05/22 07:49:06  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * use inherited error() method
 *
 * Revision 1.1  2006/09/14 15:28:50  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * first version of TracingHandlers
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.util.TracingUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Handles various function for tracing: start, stop.
 * @author  Peter Leeuwenburgh
 */
public final class TracingHandler extends ActionBase {
	public static final String version="$RCSfile: TracingHandler.java,v $ $Revision: 1.4 $ $Date: 2011-11-30 13:51:46 $";

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
		String action = request.getParameter("action");
		if (null == action)
			action = mapping.getParameter();

		log.debug("action [" + action + "]");

		if (action.equals("stoptracing")) {
			try {
				TracingUtil.stopTracing();
			} catch (Throwable t) {
				warn("could not stop tracing",t);
			}
		} else {
			if (action.equals("starttracing")) {
				try {
					TracingUtil.startTracing();
				} catch (Throwable t) {
					warn("could not start tracing",t);
				}
			}
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		} // Forward control to the specified success URI
		log.debug("forward to success");
		return (mapping.findForward("success"));
	}
}