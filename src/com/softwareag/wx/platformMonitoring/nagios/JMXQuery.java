package com.softwareag.wx.platformMonitoring.nagios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * 
 * JMXQuery is used for local or remote request of JMX attributes It requires
 * JRE 1.5 to be used for compilation and execution. Look method main for
 * description how it can be invoked.
 * 
 * Based on the Nagios JMX Plugin "check_jmx"
 * (https://exchange.nagios.org/directory/Plugins/Java-Applications-and-Servers/
 * check_jmx/details)
 * 
 * @author Henning Waack, Software AG
 * 
 * 
 *         Example Java Run Command
 * 
 *         Get trigger status for trigger
 *         "wx.platformMonitoring_Test.pub.trigger.messaging:messagingTrigger"
 *         and check if it is enabled (triggerStatus.enabled=true). Do Debug
 *         Logging. -U service:jmx:rmi:///jndi/rmi://localhost:5002/jmxrmi -e
 *         true -A Folder -O
 *         WxPlatformMonitoring:folder=wx.platformMonitoring.pub.trigger -P
 *         wx.platformMonito Debug Logging. Get list of triggers
 *         "wx.platformMonitoring.pub.trigger:listTriggers" and check if
 *         nrDisabled is 0 (OK). If 1, then WARNING. If more than 2, then
 *         CRITICAL -U service:jmx:rmi:///jndi/rmi://localhost:5002/jmxrmi -e
 *         true -A Folder -O
 *         WxPlatformMonitoring:folder=wx.platformMonitoring.pub.trigger -P
 *         wx.platformMonitoring.pub.trigger:listTriggers -K nrDisabled -w 1 -c
 *         2 -l 1
 * 
 */
public class JMXQuery {

	private String url;
	private int verbatim;
	private JMXConnector connector;
	private MBeanServerConnection connection;
	private long warning, critical;
	private String expectedValue;
	private String attribute, info_attribute;
	private String attribute_key, info_key;
	private String object;
	private String operation;
	private Object[] operationParameters = new Object[0];
	private String[] operationParametersSignature = new String[0];
	private int logLevel = 0;

	private Object checkData;
	private Object infoData;

	private static final int LOG_LEVEL_INFO = 0;
	private static final int LOG_LEVEL_DEBUG = 1;

	private static enum STATUS {
		RETURN_OK() {
			public String toString() {
				return "JMX OK ";
			}

			public int toInt() {
				return 0;
			}
		},
		RETURN_WARNING() {
			public String toString() {
				return "JMX WARNING ";
			}

			public int toInt() {
				return 1;
			}
		},
		RETURN_CRITICAL() {
			public String toString() {
				return "JMX CRITICAL ";
			}

			public int toInt() {
				return 2;
			}
		},
		RETURN_UNKNOWN() {
			public String toString() {
				return "JMX UNKNOWN ";
			}

			public int toInt() {
				return 3;
			}
		};

		public abstract int toInt();
	}

	private void connect() throws IOException {
		JMXServiceURL jmxUrl = new JMXServiceURL(url);
		connector = JMXConnectorFactory.connect(jmxUrl);
		connection = connector.getMBeanServerConnection();
	}

	private void disconnect() throws IOException {
		if (connector != null) {
			connector.close();
			connector = null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JMXQuery query = new JMXQuery();

		try {
			query.parseCommandLineArguments(args);
			query.connect();
			query.execute();
			STATUS status = query.report(System.out);
			System.exit(status.toInt());
		} catch (Exception ex) {
			int status = query.report(ex, System.out);
			System.exit(status);
		} finally {
			try {
				query.disconnect();
			} catch (IOException e) {
				int status = query.report(e, System.out);
				System.exit(status);
			}
		}
	}

	private int report(Exception ex, PrintStream out) {
		if (ex instanceof ParseError) {
			out.print(STATUS.RETURN_UNKNOWN.toString());
			reportException(ex, out);
			out.println(" Usage: check_jmx -help ");
			return STATUS.RETURN_UNKNOWN.toInt();
		} else {
			out.print(STATUS.RETURN_CRITICAL.toString());
			reportException(ex, out);
			out.println();
			return STATUS.RETURN_CRITICAL.toInt();
		}
	}

	private void reportException(Exception ex, PrintStream out) {

		if (verbatim < 2)
			out.print(rootCause(ex).getMessage());
		else {
			out.print(ex.getMessage() + " connecting to " + object + " by URL " + url);
		}

		if (verbatim >= 3)
			ex.printStackTrace(out);

	}

	private static Throwable rootCause(Throwable ex) {
		if (ex.getCause() == null)
			return ex;
		return rootCause(ex.getCause());
	}

	private STATUS compareWithExpectedValue(Object checkData) {

		return this.expectedValue.toLowerCase().equals(checkData.toString().toLowerCase()) ? STATUS.RETURN_OK
				: STATUS.RETURN_CRITICAL;
	}

	private STATUS report(PrintStream out) {
		STATUS status;
		if (this.expectedValue != null) {
			logDebug("comparing (case insenstive!) reading (" + checkData.toString() + ") against expected value: "
					+ this.expectedValue);
			status = compareWithExpectedValue(checkData);
			logDebug("Reading equals expected value: " + status.toString());
		} else {
			logDebug("comparing reading (" + parseData(checkData) + ") against critical (" + critical
					+ ") and warning (" + warning + ") value");
			if (compare(critical, warning < critical)) {
				status = STATUS.RETURN_CRITICAL;
			} else if (compare(warning, warning < critical)) {
				status = STATUS.RETURN_WARNING;
			} else {
				status = STATUS.RETURN_OK;
			}
			logDebug("Status: " + status.toString());
		}
//		out.print(" Status: " + status.toString());
		if (infoData == null || verbatim >= 2) {
			if (attribute_key != null)
				out.print(attribute + '.' + attribute_key + '=' + checkData);
			else
				out.print(attribute + '=' + checkData);
		}
		if (infoData != null) {
			if (infoData instanceof CompositeDataSupport)
				report((CompositeDataSupport) infoData, out);
			else
				out.print(infoData.toString());
		}
		return status;
	}

	private void report(CompositeDataSupport data, PrintStream out) {
		CompositeType type = data.getCompositeType();
		out.print('{');
		for (Iterator it = type.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (data.containsKey(key))
				out.print(key + '=' + data.get(key));
			if (it.hasNext())
				out.print(';');
		}
		out.print('}');
	}

	private boolean compare(long level, boolean more) {
		long checkDataLong = parseData(checkData);
		if (more)
			return checkDataLong >= level;
		else
			return checkDataLong <= level;
	}

	private void parseCompositeData(CompositeDataSupport cds, String accessPath) throws ParseError {
		if (accessPath == null)
			throw new ParseError("Attribute key is null for composed data " + object);
		int index = accessPath.indexOf(".");
		if (index == -1) {
			checkData = cds.get(accessPath);
			return;
		}
		String key = accessPath.substring(0, index);
		String path = accessPath.substring(index + 1);
		CompositeDataSupport subData = (CompositeDataSupport) cds.get(key);
		parseCompositeData(subData, path);

	}

	private void execute() throws Exception {
		Object attr;
		logDebug("Getting object with ObjectName '" + object + "'");
		if (this.operation != null) {
			logDebug("Invoking operation '" + operation + "'.");
			for (int i = 0; i < operationParameters.length; i++) {
				logDebug("Operation parameter " + (i + 1) + ": '" + operationParameters[i] + "' of type '"
						+ operationParametersSignature[i] + "' to operation");
			}
			attr = connection.invoke(new ObjectName(object), operation, operationParameters,
					operationParametersSignature);
		} else {
			logDebug("Getting attribute '" + attribute + "'.");
			attr = connection.getAttribute(new ObjectName(object), attribute);
		}
		logDebug("Retrieved object for operation: " + attr);
		if (attr instanceof CompositeDataSupport) {
			logDebug("Extracting data with key '" + attribute_key + "' from result.");
			CompositeDataSupport cds = (CompositeDataSupport) attr;
			parseCompositeData(cds, attribute_key);
		} else {
			checkData = attr;
		}

		if (info_attribute != null) {
			Object info_attr = info_attribute.equals(attribute) ? attr
					: connection.getAttribute(new ObjectName(object), info_attribute);
			if (info_key != null && (info_attr instanceof CompositeDataSupport) && verbatim < 4) {
				CompositeDataSupport cds = (CompositeDataSupport) attr;
				infoData = cds.get(info_key);
			} else {
				infoData = info_attr;
			}
		}
	}

	private long parseData(Object o) {
		if (o instanceof Number)
			return ((Number) o).longValue();
		else
			return Long.parseLong(o.toString());
	}

	private void parseCommandLineArguments(String[] args) throws ParseError {
		try {
			for (int i = 0; i < args.length; i++) {
				String option = args[i];
				if (option.equals("-help")) {
					printHelp(System.out);
					System.exit(STATUS.RETURN_UNKNOWN.toInt());
				} else if (option.equals("-U")) {
					this.url = args[++i];
				} else if (option.equals("-O")) {
					this.object = args[++i];
				} else if (option.equals("-A")) {
					this.attribute = args[++i];
				} else if (option.equals("-I")) {
					this.info_attribute = args[++i];
				} else if (option.equals("-J")) {
					this.info_key = args[++i];
				} else if (option.equals("-K")) {
					this.attribute_key = args[++i];
				} else if (option.startsWith("-v")) {
					this.verbatim = option.length() - 1;
				} else if (option.equals("-w")) {
					this.warning = Long.parseLong(args[++i]);
				} else if (option.equals("-c")) {
					this.critical = Long.parseLong(args[++i]);
				} else if (option.equals("-operation")) {
					this.operation = args[++i];
				} else if (option.equals("-expected")) {
					this.expectedValue = args[++i];
				} else if (option.equals("-params")) {
					this.operationParameters = splitParameters(args[++i]);
				} else if (option.equals("-signature")) {
					this.operationParametersSignature = splitSignature(args[++i]);
				} else if (option.equals("-loglevel")) {
					this.logLevel = Integer.parseInt(args[++i]);
				}
			}

			if (url == null || object == null)
				throw new Exception("Required options not specified");
			if (attribute == null && operation == null)
				throw new Exception("Either Attribute or Operation must be provided");

		} catch (Exception e) {
			throw new ParseError(e);
		}

	}

	public String[] splitSignature(String args) throws ParseError {
		String[] argsArr = args.split(",");
		String[] signature = new String[argsArr.length];
		for (int i = 0; i < argsArr.length; i++) {
			String arg = argsArr[i].trim();
			if (arg.equalsIgnoreCase("String") || arg.equalsIgnoreCase("java.lang.String")) {
				signature[i] = "java.lang.String";
			} else if (arg.equalsIgnoreCase("int") || arg.equalsIgnoreCase("Integer")
					|| arg.equalsIgnoreCase("java.lang.Integer")) {
				signature[i] = "java.lang.Integer";
			} else {
				throw new ParseError("Only String and Integer are supported as signature fields");
			}
		}
		return signature;
	}

	public Object[] splitParameters(String args) throws ParseError {
		String[] argsArr = args.split(",");
		Object[] params = new Object[argsArr.length];
		for (int i = 0; i < argsArr.length; i++) {
			String param = argsArr[i].trim();
			// check if param is surrounded by "" --> String
			if (param.startsWith("\"") && param.endsWith("\"")) {
				params[i] = param.substring(1, param.length() - 1);
			} else {
				// else we assume it is an integer
				try {
					params[i] = Integer.valueOf(param);
				} catch (NumberFormatException e) {
					throw new ParseError("Parameter '" + param + "' at index " + i
							+ " is neither Stirng nor Integer. Strings must be surrounded by \".");
				}
			}
		}
		return params;
	}

	private void printHelp(PrintStream out) {
		InputStream is = getClass().getClassLoader()
				.getResourceAsStream("com/softwareag/wx/platformMonitoring/nagios/nagios_check_jmx_ext.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			while (true) {
				String s = reader.readLine();
				if (s == null)
					break;
				out.println(s);
			}
		} catch (IOException e) {
			out.println(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				out.println(e);
			}
		}
	}

	private void logDebug(String msg) {
		this.log(msg, LOG_LEVEL_DEBUG);
	}

	private void logInfo(String msg) {
		this.log(msg, LOG_LEVEL_INFO);
	}

	private void log(String msg, int level) {
		if (level <= this.logLevel) {
			System.out.println(msg);
		}
	}

}
