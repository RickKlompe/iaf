/*
 * $Log: PipeStartException.java,v $
 * Revision 1.6  2011-11-30 13:51:55  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.2  2011/10/19 14:59:25  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * do not print versions anymore
 *
 * Revision 1.1  2011/10/19 14:49:46  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.4  2004/03/30 07:29:59  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * updated javadoc
 *
 */
package nl.nn.adapterframework.core;

/**
 * Exception that indicates that the starting of a {@link IPipe Pipe}
 * did not succeed.<br/>

 * @version Id
 * @author Johan Verrips IOS
 * @see nl.nn.adapterframework.pipes.AbstractPipe#start()
 */
public class PipeStartException extends IbisException{
	
	private String pipeNameInError=null;
/**
 * PipeStartException constructor comment.
 */
public PipeStartException() {
	super();
}
/**
 * PipeStartException constructor comment.
 */
public PipeStartException(String msg) {
	super(msg);
}
public PipeStartException(String msg, Throwable e) {
	super(msg, e);
}
public PipeStartException(Throwable e) {
	super(e);
}
/**
 * The name of the pipe in error.
 * @return java.lang.String Name of the pipe in error
 */
public java.lang.String getPipeNameInError() {
	return pipeNameInError;
}
/**
 * The name of the pipe in error. 
 * @param newPipeNameInError Name of the pipe in error
 */
public void setPipeNameInError(java.lang.String newPipeNameInError) {
	pipeNameInError = newPipeNameInError;
}
}