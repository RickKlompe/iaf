/*
 * $Log: TestService.java,v $
 * Revision 1.6  2011-11-30 13:51:46  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2007/10/08 13:41:35  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * changed ArrayList to List where possible
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.receivers.ServiceDispatcher;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Testing a service prefill.
 * 
 * @author Johan Verrips
 * @version Id
 */
public class TestService extends ActionBase {
	public static final String version = "$RCSfile: TestService.java,v $ $Revision: 1.6 $ $Date: 2011-11-30 13:51:46 $";
	
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
	    DynaActionForm serviceTestForm = getPersistentForm(mapping, form, request);
	
		Iterator it=ServiceDispatcher.getInstance().getRegisteredListenerNames();
		List services=new ArrayList();
		services.add("----- select a service -----");
		while (it.hasNext()) {
			services.add((String)it.next());
		}
		serviceTestForm.set("services", services);
		// Forward control to the specified success URI
	    log.debug("forward to success");
	    return (mapping.findForward("success"));
	    
	}
}