/*
 * $Log: OutputfieldsPart.java,v $
 * Revision 1.5  2011-11-30 13:51:56  europe\m168309
 * adjusted/reversed "Upgraded from WebSphere v5.1 to WebSphere v6.1"
 *
 * Revision 1.1  2011/10/19 14:49:48  Peter Leeuwenburgh <peter.leeuwenburgh@ibissource.org>
 * Upgraded from WebSphere v5.1 to WebSphere v6.1
 *
 * Revision 1.3  2006/05/19 09:28:37  Peter Eijgermans <peter.eijgermans@ibissource.org>
 * Restore java files from batch package after unwanted deletion.
 *
 * Revision 1.1  2005/10/11 13:00:21  John Dekker <john.dekker@ibissource.org>
 * New ibis file related elements, such as DirectoryListener, MoveFilePie and 
 * BatchFileTransformerPipe
 *
 */
package nl.nn.adapterframework.batch;

/**
 * @author John Dekker
 */
public class OutputfieldsPart {
	public static final String version = "$RCSfile: OutputfieldsPart.java,v $  $Revision: 1.5 $ $Date: 2011-11-30 13:51:56 $";
	private String value;
	private String description;
	
	public String getValue() {
		return value;
	}

	public void setValue(String string) {
		value = string;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String string) {
		description = string;
	}

}