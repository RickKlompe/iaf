/*
 * $Log: EditMonitorExecute.java,v $
 * Revision 1.7  2011-11-30 13:51:46  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:50  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.5  2008/08/14 14:53:51  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * fixed exit determination
 *
 * Revision 1.4  2008/08/13 13:46:57  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * some bugfixing
 *
 * Revision 1.3  2008/08/07 11:32:29  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * rework
 *
 * Revision 1.2  2008/07/24 12:42:10  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * rework of monitoring
 *
 * Revision 1.1  2008/07/17 16:21:49  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * work in progess
 *
 */
package nl.nn.adapterframework.webcontrol.action;

import javax.servlet.http.HttpServletResponse;

import nl.nn.adapterframework.monitoring.Monitor;
import nl.nn.adapterframework.monitoring.MonitorManager;
import nl.nn.adapterframework.monitoring.Trigger;

import org.apache.struts.action.DynaActionForm;



/**
 * Edit a Monitor - process the form.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9
 * @version Id
 */
public class EditMonitorExecute extends EditMonitor {

	public String determineExitForward(DynaActionForm monitorForm) {
		return (String)monitorForm.get("return");
	}

	public String performAction(DynaActionForm monitorForm, String action, int index, int triggerIndex, HttpServletResponse response) {
		
		MonitorManager mm = MonitorManager.getInstance();
		if (index>=0) {
			Monitor monitor = mm.getMonitor(index);
			if (action.equals("createTrigger")) {
				int triggerCount=monitor.getTriggers().size();
				Trigger trigger = new Trigger();
				switch (triggerCount) {
					case 0: trigger.setAlarm(true);
					case 1: trigger.setAlarm(false);
					default: trigger.setAlarm(true);
				}
				monitor.registerTrigger(trigger);				
			} else 
			if (action.equals("deleteTrigger")) {
				monitor.getTriggers().remove(triggerIndex);
				return determineExitForward(monitorForm);
			} else   
			if (action.equals("OK") || action.equals("Apply")) {
				Monitor formMonitor = (Monitor)monitorForm.get("monitor");
				monitor.setName(formMonitor.getName());
				monitor.setTypeEnum(formMonitor.getTypeEnum());
				monitor.setDestinations(formMonitor.getDestinations());
				if (action.equals("OK")) {
					return determineExitForward(monitorForm);
				} 
			}
		}
		return "self";
	}
}