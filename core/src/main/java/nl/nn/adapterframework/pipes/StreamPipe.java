/*
   Copyright 2016 Nationale-Nederlanden

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.ParameterException;
import nl.nn.adapterframework.core.PipeForward;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.http.HttpSender;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.soap.SoapWrapper;
import nl.nn.adapterframework.util.Misc;

/**
 * Stream an input stream to an output stream.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setForwardName(String) forwardName}</td><td>name of forward returned upon completion</td><td>"success"</td></tr>
 * <tr><td>{@link #setExtractFirstStringPart(boolean) extractFirstStringPart}</td><td>(only used for parameter <code>httpRequest</code>) when true the first part is not put in a session key but returned to the pipeline (as the result of this pipe)</td><td>false</td></tr>
 * <tr><td>{@link #setMultipartXmlSessionKey(String) multipartXmlSessionKey}</td><td>(only used when <code>extractFirstStringPart=true</code>) the session key to put the xml in with info about the stored parts</td><td><code>multipartXml</code></td></tr>
 * <tr><td>{@link #setCheckAntiVirus(boolean) checkAntiVirus}</td><td>(only used for parameter <code>httpRequest</code>) when true parts are checked for antivirus scan returncode. These antivirus scan parts have been added by another application (so the antivirus scan is NOT performed in this pipe). For each file part an antivirus scan part have been added by this other application (directly after this file part)</td><td>false</td></tr>
 * <tr><td>{@link #setAntiVirusPartName(String) antiVirusPartName}</td><td>(only used for parameter <code>httpRequest</code> and when <code>checkAntiVirus=true</code>) name of antivirus scan parts</td><td><code>antivirus_rc</code></td></tr>
 * <tr><td>{@link #setAntiVirusPassedMessage(String) antiVirusPassedMessage}</td><td>(only used for parameter <code>httpRequest</code> and when <code>checkAntiVirus=true</code>) message of antivirus scan parts which indicates the antivirus scan passed</td><td><code>Pass</code></td></tr>
 * <tr><td>{@link #setAntiVirusFailureAsSoapFault(boolean) antiVirusFailureAsSoapFault}</td><td>(only used for parameter <code>httpRequest</code> and when <code>checkAntiVirus=true</code>) when true and the antiVirusFailed forward is specified and the antivirus scan did not pass, a SOAP Fault is returned instead of only a plain error message</td><td>false</td></tr>
 * </table>
 * </p>
 * <p><b>Parameters:</b></p>
 * <table border="1">
 * <tr><th>name</th><th>default</th></tr>
 * <tr><td>inputStream</td><td>the input stream object to use instead of an input stream object taken from pipe input</td></tr>
 * <tr><td>outputStream</td><td>the output stream object to use unless httpResponse parameter is specified</td></tr>
 * <tr><td>httpResponse</td><td>an HttpServletResponse object to stream to (the output stream is retrieved by calling getOutputStream() on the HttpServletResponse object)</td></tr>
 * <tr><td>httpRequest</td><td>an HttpServletRequest object to stream from. Each part is put in a session key and the result of this pipe is a xml with info about these parts and the name of the session key</td></tr>
 * <tr><td>contentType</td><td>the Content-Type header to set in case httpResponse was specified</td></tr>
 * <tr><td>contentDisposition</td><td>the Content-Disposition header to set in case httpResponse was specified</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * <tr><td>"antiVirusFailed"</td><td>if <code>checkAntiVirus=true</code> and an antivirus part is present of which the value differs from <code>antiVirusPassedMessage</code>. If not specified, a PipeRunException is thrown in that situation</td></tr>
 * </table>
 * </p>
 * @author Jaco de Groot
 */
public class StreamPipe extends FixedForwardPipe {
	public static final String ANTIVIRUS_FAILED_FORWARD = "antiVirusFailed";

	private boolean extractFirstStringPart = false;
	private String multipartXmlSessionKey = "multipartXml";
	private boolean checkAntiVirus = false;
	private String antiVirusPartName = "antivirus_rc";
	private String antiVirusPassedMessage = "Pass";
	private boolean antiVirusFailureAsSoapFault = false;

	@Override
	public PipeRunResult doPipe(Object input, IPipeLineSession session)
			throws PipeRunException {
		Object result = input;
		String inputString;
		if (input instanceof String) {
			inputString = (String) input;
		} else {
			inputString = "";
		}
		ParameterResolutionContext prc = new ParameterResolutionContext(
				inputString, session, isNamespaceAware());
		Map parameters = null;
		ParameterList parameterList = getParameterList();
		if (parameterList != null) {
			try {
				parameters = prc.getValueMap(parameterList);
			} catch (ParameterException e) {
				throw new PipeRunException(this, "Could not resolve parameters",
						e);
			}
		}
		InputStream inputStream = null;
		OutputStream outputStream = null;
		HttpServletRequest httpRequest = null;
		HttpServletResponse httpResponse = null;
		String contentType = null;
		String contentDisposition = null;
		if (parameters != null) {
			if (parameters.get("inputStream") != null) {
				inputStream = (InputStream) parameters.get("inputStream");
			}
			if (parameters.get("outputStream") != null) {
				outputStream = (OutputStream) parameters.get("outputStream");
			}
			if (parameters.get("httpRequest") != null) {
				httpRequest = (HttpServletRequest) parameters
						.get("httpRequest");
			}
			if (parameters.get("httpResponse") != null) {
				httpResponse = (HttpServletResponse) parameters
						.get("httpResponse");
			}
			if (parameters.get("contentType") != null) {
				contentType = (String) parameters.get("contentType");
			}
			if (parameters.get("contentDisposition") != null) {
				contentDisposition = (String) parameters
						.get("contentDisposition");
			}
		}
		if (inputStream == null && input instanceof InputStream) {
			inputStream = (InputStream) input;
		}
		try {
			if (httpResponse != null) {
				HttpSender.streamResponseBody(inputStream, contentType,
						contentDisposition, httpResponse, log,
						getLogPrefix(session));
			} else if (httpRequest != null) {
				StringBuilder partsString = new StringBuilder("<parts>");
				String firstStringPart = null;
				if (ServletFileUpload.isMultipartContent(httpRequest)) {
					log.debug(getLogPrefix(session)
							+ "request with content type ["
							+ httpRequest.getContentType() + "] and length ["
							+ httpRequest.getContentLength()
							+ "] contains multipart content");
					DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
					ServletFileUpload servletFileUpload = new ServletFileUpload(
							diskFileItemFactory);
					List<FileItem> items = servletFileUpload
							.parseRequest(httpRequest);
					int fileCounter = 0;
					int stringCounter = 0;
					log.debug(getLogPrefix(session)
							+ "multipart request items size [" + items.size()
							+ "]");
					String lastProcessedFileName = null;
					for (FileItem item : items) {
						if (item.isFormField()) {
							// Process regular form field (input
							// type="text|radio|checkbox|etc", select, etc).
							String fieldValue = item.getString();
							String fieldName = item.getFieldName();
							if (isCheckAntiVirus() && fieldName
									.equalsIgnoreCase(getAntiVirusPartName())) {
								log.debug(getLogPrefix(session)
										+ "found antivirus part [" + fieldName
										+ "] with value [" + fieldValue + "]");
								if (!fieldValue.equalsIgnoreCase(
										getAntiVirusPassedMessage())) {
									String errorMessage = "multipart contains file ["
											+ lastProcessedFileName
											+ "] with antivirus status ["
											+ fieldValue + "]";
									PipeForward antiVirusFailedForward = findForward(
											ANTIVIRUS_FAILED_FORWARD);
									if (antiVirusFailedForward == null) {
										throw new PipeRunException(this,
												errorMessage);
									} else {
										if (antiVirusFailureAsSoapFault) {
											errorMessage = createSoapFaultMessage(
													errorMessage);
										}
										return new PipeRunResult(
												antiVirusFailedForward,
												errorMessage);
									}
								}
							} else {
								if (isExtractFirstStringPart()
										&& firstStringPart == null
										&& !(isCheckAntiVirus()
												&& fieldName.equalsIgnoreCase(
														getAntiVirusPartName()))) {
									log.debug(getLogPrefix(session)
											+ "extracting first string part ["
											+ fieldValue + "]");
									firstStringPart = fieldValue;
								} else {
									String sessionKeyName = "part_string"
											+ (++stringCounter > 1
													? stringCounter : "");
									addSessionKey(session, sessionKeyName,
											fieldValue);
									partsString
											.append("<part type=\"string\" name=\""
													+ fieldName
													+ "\" sessionKey=\""
													+ sessionKeyName
													+ "\" size=\""
													+ fieldValue.length()
													+ "\"/>");
								}
							}
						} else {
							// Process form file field (input type="file").
							String sessionKeyName = "part_file"
									+ (++fileCounter > 1 ? fileCounter : "");
							String fileName = FilenameUtils
									.getName(item.getName());
							InputStream is = item.getInputStream();
							int size = is.available();
							String mimeType = item.getContentType();
							if (size > 0) {
								addSessionKey(session, sessionKeyName, is,
										fileName);
							} else {
								addSessionKey(session, sessionKeyName, null);
							}
							partsString.append("<part type=\"file\" name=\""
									+ fileName + "\" sessionKey=\""
									+ sessionKeyName + "\" size=\"" + size
									+ "\" mimeType=\"" + mimeType + "\"/>");
							lastProcessedFileName = fileName;
						}
					}
				} else {
					log.debug(getLogPrefix(session)
							+ "request with content type ["
							+ httpRequest.getContentType() + "] and length ["
							+ httpRequest.getContentLength()
							+ "] does NOT contain multipart content");
				}
				partsString.append("</parts>");
				if (isExtractFirstStringPart()) {
					result = adjustFirstStringPart(firstStringPart, session);
					session.put(getMultipartXmlSessionKey(),
							partsString.toString());
				} else {
					result = partsString.toString();
				}
			} else {
				Misc.streamToStream(inputStream, outputStream);
			}
		} catch (IOException e) {
			throw new PipeRunException(this,
					"IOException streaming input to output", e);
		} catch (FileUploadException e) {
			throw new PipeRunException(this,
					"FileUploadException getting multiparts from httpServletRequest",
					e);
		}
		return new PipeRunResult(getForward(), result);
	}

	protected String adjustFirstStringPart(String firstStringPart,
			IPipeLineSession session) throws PipeRunException {
		if (firstStringPart == null) {
			return "";
		} else {
			return firstStringPart;
		}
	}

	private String createSoapFaultMessage(String errorMessage)
			throws PipeRunException {
		try {
			return SoapWrapper.getInstance()
					.createSoapFaultMessage(errorMessage);
		} catch (ConfigurationException e) {
			throw new PipeRunException(this,
					"Could not get soapWrapper instance", e);
		}
	}

	private void addSessionKey(IPipeLineSession session, String key,
			Object value) {
		addSessionKey(session, key, value, null);
	}

	private void addSessionKey(IPipeLineSession session, String key,
			Object value, String name) {
		String message = getLogPrefix(session) + "setting sessionKey [" + key
				+ "] to ";
		if (value instanceof InputStream) {
			message = message + "input stream of file [" + name + "]";
		} else {
			message = message + "[" + value + "]";
		}
		log.debug(message);
		session.put(key, value);
	}

	public void setExtractFirstStringPart(boolean b) {
		extractFirstStringPart = b;
	}

	public boolean isExtractFirstStringPart() {
		return extractFirstStringPart;
	}

	public String getMultipartXmlSessionKey() {
		return multipartXmlSessionKey;
	}

	public void setMultipartXmlSessionKey(String multipartXmlSessionKey) {
		this.multipartXmlSessionKey = multipartXmlSessionKey;
	}

	public void setCheckAntiVirus(boolean b) {
		checkAntiVirus = b;
	}

	public boolean isCheckAntiVirus() {
		return checkAntiVirus;
	}

	public String getAntiVirusPartName() {
		return antiVirusPartName;
	}

	public void setAntiVirusPartName(String antiVirusPartName) {
		this.antiVirusPartName = antiVirusPartName;
	}

	public String getAntiVirusPassedMessage() {
		return antiVirusPassedMessage;
	}

	public void setAntiVirusPassedMessage(String antiVirusPassedMessage) {
		this.antiVirusPassedMessage = antiVirusPassedMessage;
	}

	public void setAntiVirusFailureAsSoapFault(boolean b) {
		antiVirusFailureAsSoapFault = b;
	}

	public boolean getAntiVirusFailureAsSoapFault() {
		return antiVirusFailureAsSoapFault;
	}
}
