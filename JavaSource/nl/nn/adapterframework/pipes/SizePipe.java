/*
 * $Log: SizePipe.java,v $
 * Revision 1.3  2011-11-30 13:51:50  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:45  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.1  2006/08/22 12:56:32  Gerrit van Brakel <gerrit.van.brakel@ibissource.org>
 * first version
 *
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;

/**
 * Returns the number of bytes or characters in the input.
 *
 * @author Jaco de Groot (***@dynasol.nl)
 * @version Id
 *
 */
public class SizePipe extends FixedForwardPipe {
	public static final String version="$RCSfile: SizePipe.java,v $ $Revision: 1.3 $ $Date: 2011-11-30 13:51:50 $";

	/**
	 * @see nl.nn.adapterframework.core.IPipe#doPipe(Object, PipeLineSession)
	 */
	public PipeRunResult doPipe(Object input, PipeLineSession session) throws PipeRunException {
		try {
			int size = -1;
			if (input instanceof String) {
				size = ((String)input).length();
			} else if (input instanceof byte[]) {
				size = ((byte[])input).length;
			}
			return new PipeRunResult(getForward(), "" + size);
		} catch(Exception e) {
			throw new PipeRunException(this, "Error while transforming input", e);
		}
	}

}