/*
 * $Log: LogHandler.java,v $
 * Revision 1.8  2011-11-30 13:51:45  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:49  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.6  2007/08/30 15:12:12  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * modified getRootLogger()
 *
 * Revision 1.5  2007/02/16 14:24:45  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * corrected version string
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.util.AppConstants;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * This handler updates the root log level and the value in the AppConstants named "log.logIntermediaryResults".
 *
 * @author  Johan Verrips IOS
 * @version Id
 */
public class LogHandler extends ActionBase {
	public static final String version = "$RCSfile: LogHandler.java,v $ $Revision: 1.8 $ $Date: 2011-11-30 13:51:45 $";
	
	 public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {


        // Initialize action
        initAction(request);

        String commandIssuedBy= " remoteHost ["+request.getRemoteHost()+"]";
		commandIssuedBy+=" remoteAddress ["+request.getRemoteAddr()+"]";
		commandIssuedBy+=" remoteUser ["+request.getRemoteUser()+"]";

		Logger lg=LogUtil.getRootLogger();

        DynaActionForm logForm = (DynaActionForm) form;
        String form_logLevel = (String) logForm.get("logLevel");
         boolean form_logIntermediaryResults=false;
         if (null!= logForm.get("logIntermediaryResults")) {
            form_logIntermediaryResults = ((Boolean) logForm.get("logIntermediaryResults")).booleanValue();
         }
        log.warn("*** logintermediary results="+form_logIntermediaryResults);
        String logIntermediaryResults="false";
        if (form_logIntermediaryResults) logIntermediaryResults="true";

        Level level=Level.toLevel(form_logLevel);


		log.warn("LogLevel changed from ["
			+lg.getLevel()
			+"]  to ["
			+level
			+"]  and logIntermediaryResults from ["
			+AppConstants.getInstance().getProperty("log.logIntermediaryResults")
			+ "] to ["
			+ ""+form_logIntermediaryResults
			+"] by"+commandIssuedBy);

        AppConstants.getInstance().put("log.logIntermediaryResults", logIntermediaryResults);
		lg.setLevel(level);


        return (mapping.findForward("success"));
        }
}