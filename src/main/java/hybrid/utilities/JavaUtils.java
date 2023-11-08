package hybrid.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class JavaUtils {
	String stri;
	String fileName;
	static String failureReason;
	public static AndroidDriver mdriver;
	public static HashMap<String, String> configProperties = new HashMap<String, String>();
//	Properties velocityProps;
	public static String assertionMessage, timeTaken;
	public static Map<String, String> dataMap;
	public static HashMap<String, String> userDefinedData = new HashMap<String, String>();
	public static HashMap<byte[], String> imageByte = new HashMap<byte[], String>();

	static String sectionName = null;

	public static HashMap<String, String> readConfigProperties() {
		Set<Entry<String, String>> dataSet;
		Ini ini;
		try {
			ini = new Ini(new File("./config.ini"));

			Ini.Section section = ini.get("Common");
			dataSet = section.entrySet();

			sectionName = section.get("configName");
			section = ini.get(sectionName);

			dataSet.addAll(section.entrySet());
			for (Map.Entry<String, String> set : dataSet) {
				configProperties.put(set.getKey().toString(), set.getValue().toString());
			}
			return configProperties;
		} catch (InvalidFileFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addConfigToIni(String key, String value) {
		try {
			Ini ini = new Ini(new File("./config.ini"));
			ini.put("Common", key, value);
			ini.store();
		} catch (InvalidFileFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void addTestDataIni(String key, String value) {
		try {
			File file = new File("./testData.ini");
			if (!(file.exists())) {
				file.createNewFile();
				Ini ini = new Ini(file);
			}
			Ini ini = new Ini(file);
			ini.put("Common", key, value);
			ini.store();
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int randBetween(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}

	public HashMap<String, String> readDataSet() {
		Set<Entry<String, String>> dataSet;
		Ini ini;
		try {
			ini = new Ini(new File("./dataSet.ini"));

			Ini.Section section = ini.get("Common");
			dataSet = section.entrySet();

			sectionName = section.get("ReadData");
			section = ini.get(sectionName);

			dataSet.addAll(section.entrySet());
			for (Map.Entry<String, String> set : dataSet) {
				userDefinedData.put(set.getKey().toString(), set.getValue().toString());
			}
			return userDefinedData;
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void readDeviceId() {

		// reading deviceID
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Runtime.getRuntime().exec("adb devices").getInputStream()));) {

			String udid = "";
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length == 2)
					udid = arr[0];
				line = br.readLine();
			}
			if (udid.equals("")) {
				System.err.println("Device not Connected Properly. Please reconnect.......");
				// System.exit(0);
			}

			else {
				System.err.println("******* Device Connected Successfully !! *******");
				System.err.println("DeviceID : " + udid);
				addConfigToIni("deviceName", udid);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String randomString(int length) {
		// Log.info("getting a random string");
		final String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is
		// being used or not
		final Set<String> identifiers = new HashSet<String>();

		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			for (int i = 0; i < length; i++) {
				builder.append(randomString.charAt(rand.nextInt(randomString.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}

	public static AndroidDriver launchApp(String deviceName) throws IOException {

		// dataMap = readDeviceConfig(deviceName);
		String server = JavaUtils.configProperties.get("appiumServer");
		String port = dataMap.get("PORT");
		// super.startAppiumServer(server, port);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (deviceName.equalsIgnoreCase("Emulator1")) {
			DefaultExecutor executor = new DefaultExecutor();
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

			CommandLine launchEmul = new CommandLine("C:/Program Files/Genymobile/Genymotion/player");
			launchEmul.addArgument("--vm-name");
			launchEmul.addArgument("\"" + dataMap.get("DEVICENAME") + "\"");
			executor.setExitValue(1);
			executor.execute(launchEmul, resultHandler);
		}
		try {
			Thread.sleep(60000);
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.UDID, dataMap.get("UDID"));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, dataMap.get("DEVICENAME"));
			capabilities.setCapability(AndroidMobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, dataMap.get("ANDROIDVERSION"));
			capabilities.setCapability("browserName", "Android");
			capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.google.android.calculator");
			capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".ui.activities.LoginActivity");
			Thread.sleep(3000);
			mdriver = new AndroidDriver(new URL("http://" + server + ":" + port + "/wd/hub"), capabilities);
			Log.info("Launched the Calculator app successfully");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return mdriver;
	}

	public static AppiumDriver launchApp() throws InvalidFileFormatException, IOException {

		DesiredCapabilities capabilities = new DesiredCapabilities();

		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("autoGrantPermissions", true);
		capabilities.setCapability("clearSystemFiles", true);
		capabilities.setCapability("automationName", "UiAutomator2");
		String apkPath = null;
		try {
			File file = new File(JavaUtils.configProperties.get("apkPath"));
			apkPath = file.getAbsolutePath();
		} catch (Exception e) {
			System.out.println("APK path not valid");
			e.printStackTrace();
		}

		capabilities.setCapability("app", apkPath);
		mdriver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);

		return mdriver;

	}

	public void startAppiumServer() throws IOException, InterruptedException {
		
		int portToKill = 4723; // Change this to the port you want to kill

		try {
		    // Kill the process using the specified port
		    ProcessBuilder taskkillBuilder = new ProcessBuilder("cmd.exe", "/c", "taskkill /F /IM node.exe /T");
		    taskkillBuilder.environment().put("LISTENING_PORT", Integer.toString(portToKill));
		    Process taskkillProcess = taskkillBuilder.start();
		    taskkillProcess.waitFor();
		}catch(Exception e) {
			System.out.println("Appium service startup failed");
		}
		
		
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start /B appium -p 4723 --relaxed-security");
		builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		builder.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process process = builder.start();
		process.waitFor();
	}

	public static void startServer() {
		AppiumDriverLocalService service = AppiumDriverLocalService
				.buildService(new AppiumServiceBuilder().withArgument(() -> "--log-no-colors")
						.withArgument(() -> "--log-timestamp").withArgument(() -> "--log-level=error"));
		service.start();
	}

	public String randomPANNumber() {
		Log.info("getting a random PAN Number");
		int min = 1000;
		int max = 9999;
		long number = ThreadLocalRandom.current().nextLong(min, max + 1);
		String GSTNumber = "AYGPW" + number + "C";
		return GSTNumber;
	}

	public String getTodaysDateInMillis() {
		Calendar cal1 = Calendar.getInstance();
		String date = String.valueOf(cal1.getTimeInMillis());
		return date;
	}

	public String getRepaymentDateandTime(int daysToAdd) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, daysToAdd);
		long day = cal1.getTimeInMillis();
		String time = String.valueOf(day);
		System.out.println(day);
		return time;

	}

	public void startBatchFile() throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p1 = runtime.exec("cmd /c start startAppiumLocally.bat");
			p1.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String randomStringOnlyLower(int length) {
		// Log.info("getting a random string");
		final String randomString = "abcdefghijklmnopqrstuvwxyz";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is
		// being used or not
		final Set<String> identifiers = new HashSet<String>();

		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			for (int i = 0; i < length; i++) {
				builder.append(randomString.charAt(rand.nextInt(randomString.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}

	public String[] getrandomDate(String pattern) {
		/*
		 * String before = "31September1991"; Format formatter = new
		 * SimpleDateFormat("dd MMMM yyyy"); String dobj = formatter.format(new Date());
		 * System.out.println(dobj);
		 */

		GregorianCalendar gc = new GregorianCalendar();
		int year = randBetween(1970, 1990);

		gc.set(gc.YEAR, year);

		int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

		gc.set(gc.DAY_OF_YEAR, dayOfYear);

		// String dob= (gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" +
		// gc.get(gc.DAY_OF_MONTH));
		Format formatter = null;
		String dob = new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime());
		/*
		 * if (pattern.equals("dd MMMM yyyy")) { formatter = new
		 * SimpleDateFormat(pattern); } else if (pattern.equals("dd/MM/yyyy")) {
		 * formatter = new SimpleDateFormat(pattern); }
		 */
		formatter = new SimpleDateFormat(pattern);

		String date = formatter.format(gc.getTime());

		return new String[] { dob, date };

	}

	public static String getvalueFromIni(String name) {
		Ini ini;
		try {
			ini = new Ini(new File("./config.ini"));
			return ini.get("Common", name);
		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		return null;
	}

	public static String getvalueFromTestDataIni(String name) {
		Ini ini;
		try {
			ini = new Ini(new File("./testData.ini"));
			return ini.get("Common", name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addMobConfigToIni(String sheetName, String sectionName)

			throws EncryptedDocumentException, InvalidFormatException, IOException {
		Ini ini = new Ini(new File("./data.ini"));
		FileInputStream file = new FileInputStream(configProperties.get("testData"));
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();
		Row Headers = it.next();
		while (it.hasNext()) {
			Row record = it.next();
			if (record.getCell(2).toString().equalsIgnoreCase("RANDOM")) {
				ini.put(sectionName, record.getCell(0).toString(), generateRandomNo(10));

			} else {

				record.getCell(2).setCellType(CellType.STRING);
				ini.put(sectionName, record.getCell(0).toString(), record.getCell(2).toString());

			}
		}
		ini.store();
	}

	public String setvalueToIni(String user, String value) {
		Ini ini;
		try {
			ini = new Ini(new File("./testData.ini"));
			ini.put("Common", user, value);
			ini.store();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String checkExecutionStatus(String workbook, String sheetName, String testCaseID)
			throws EncryptedDocumentException, FileNotFoundException, InvalidFormatException, IOException {

		HashMap<String, String> testRow = readExcelData(workbook, sheetName, testCaseID);

		/*
		 * Checks the execution status of the current testCaseID which is set in the
		 * Excel - TestData sheet if marked 'Yes' testCase would execute , else testCase
		 * would skip
		 */
		if (testRow.get("Execution Status").toLowerCase().equalsIgnoreCase("no")) {
			throw new SkipException(
					"Skipping the test flow with ID " + testCaseID + " as it marked 'NO' in the Execution Excel Sheet");
		}

		Reporter.log("\nExecuting the " + testRow.get("Test Description") + " : " + testCaseID, true);
		return testCaseID;
	}

	/* Returns the values in column1 of the TestData in an ArrayList */
	public ArrayList<String> returnRowsUniqueValueBasedOnClassName(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;
		ArrayList<String> allValues = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			int i = 1;
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "".trim();
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.add(record.getCell(0).toString() + "".trim());
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	/*
	 * return List of HashMap with data read from excel sheet
	 */
	public List<HashMap<String, String>> returnRowsUniqueValueBasedOnTestTypeList(String workbookName, String sheetName,
			String testType) {

		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<HashMap<String, String>> allValues = new ArrayList<HashMap<String, String>>();
		try {
			FileInputStream file = new FileInputStream(configProperties.get(workbookName));
			if (file != null) {
				System.out.println(file);
			}
			System.out.println(configProperties.get(workbookName));
			String key, value;
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			boolean flag = false;
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();

				if ((record.getCell(3).toString().trim() + "").equalsIgnoreCase("yes")) {
					if (testType.equalsIgnoreCase("no-check")) {
						flag = true;
					} else if ((record.getCell(1).toString().trim() + "").equalsIgnoreCase(testType)) {
						flag = true;
					}

				}
				if (flag == true) {
					for (i = 0; i < headers.getLastCellNum(); i++) {
						if ((null != record.getCell(i))
								&& (record.getCell(i).getCellType() == record.getCell(i).getCellType().NUMERIC)) {
							if (DateUtil.isCellDateFormatted(record.getCell(i))) {

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();

							} else {
								record.getCell(i).setCellType(CellType.STRING);

								value = record.getCell(i).toString().trim();
							}
							key = headers.getCell(i).toString().trim();

						} else {

							key = (headers.getCell(i) + "".toString()).trim() + "";
							value = (null != record.getCell(i)) ? (record.getCell(i) + "".toString()).trim() + "" : "";

						}
						dataMap.put(key, value);
					}
					allValues.add(dataMap);
					dataMap = new HashMap<String, String>();
				}
				flag = false;
			}

			return allValues;

		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

	}

	//

	public HashMap<Integer, String[]> returnRowsUniqueValueBasedOnClassNameList(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;

		HashMap<Integer, String[]> allValues = new HashMap<Integer, String[]>();
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "";
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.put(i, new String[] { record.getCell(0).toString(), record.getCell(5).toString(),
							record.getCell(6).toString(), record.getCell(7).toString() });
					i++;
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}
	//

	/*
	 * Returns the ArrayList to Two-Dimensional Object array for dataProvider
	 * Iteration
	 */
	public Object[][] returnAllUniqueValues(String sheetName, Class<?> className) {

		ArrayList<String> listValues = returnRowsUniqueValueBasedOnClassName(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][1];
		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);
		}
		return allValues;
	}

	public Object[][] returnAllUniqueValuesInArray(String sheetName, Class<?> className) {

		HashMap<Integer, String[]> listValues = returnRowsUniqueValueBasedOnClassNameList(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i] = new Object[listValues.get(i).length];
			allValues[i] = listValues.get(i);
		}

		return allValues;
	}

	public Object[][] returnAllUniqueValuesInMap(String workbookName, String sheetName, String testType) {

		List<HashMap<String, String>> listValues = returnRowsUniqueValueBasedOnTestTypeList(workbookName, sheetName,
				testType);

		Object[][] allValues = new Object[listValues.size()][1];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);

		}
		return allValues;
	}

	/*
	 * Puts all the excels rows from startRowValue to endRowValue and returns
	 * Two-Dimensional Object array for dataProvider Iteration
	 */
	public Object[][] returnRowsUniqueValueInArray(String sheetName, String startRowValue, String endRowValue) {

		Object[][] values = new String[3][1];
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(0).toString();
				if (cellValue.equalsIgnoreCase(startRowValue)) {
					int j = 0;

					while (!(record.getCell(0).toString().equalsIgnoreCase(endRowValue))) {
						values[j][0] = record.getCell(0).toString();
						j++;
						record = it.next();
					}
					values[j][0] = record.getCell(0).toString();
					break;
				}
				break;
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

		return values;
	}

	public HashMap<String, String> readExcelData(String workbook, String sheetname, String uniqueValue) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream(configProperties.get(workbook));
			HashMap<String, String> dataMap = new HashMap<String, String>();
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetname);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(0).toString().trim();
//				System.out.println(record.getLastCellNum());
				if (cellValue.equalsIgnoreCase(uniqueValue)) {

					for (int i = 0; i < record.getLastCellNum(); i++) {
						// System.out.println(record.getCell(i));

						if ((null != record.getCell(i))
								&& (record.getCell(i).getCellType() == record.getCell(i).getCellType().NUMERIC)) {
							if (DateUtil.isCellDateFormatted(record.getCell(i))) {

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();

							} else {
								record.getCell(i).setCellType(CellType.NUMERIC);

								value = record.getCell(i).toString().trim();
							}
							key = headers.getCell(i).toString().trim();

						} else {

							key = headers.getCell(i).toString().trim();
							// System.out.println(key);
							value = (null != record.getCell(i)) ? (record.getCell(i) + "".toString()).trim() + "" : "";
							// System.out.println(value);
						}

						dataMap.put(key, value);
					}
					break;
				}
			}
			return dataMap;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	public HashMap<String, String> readExcelDataHeaders(String sheetname) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			HashMap<String, String> dataMap = new HashMap<String, String>();
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetname);
			Iterator<Row> it = sheet.rowIterator();

			while (it.hasNext()) {

				Row headers = it.next();
				for (int i = 0; i < headers.getLastCellNum(); i++) {
					key = headers.getCell(i).toString();
					value = headers.getCell(i).toString();
					dataMap.put(key, value);
				}
				break;
			}
			return dataMap;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	public String getTodaysDate(String format) {

		Format formatter = new SimpleDateFormat(format);
		String todaysDate = formatter.format(new Date());
		System.out.println(todaysDate);
		return todaysDate;

	}

	public String generateRandomNumber(int number) {

		Random ran = new Random();
		int x = ran.nextInt(number);

		String randomNo = "1528900" + String.valueOf(x);

		return randomNo;
	}

	public String getTodaysDateAndTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		Date tdy = cal.getTime();
		String today = df.format(tdy);

		return today;
	}

	public String getRequiredDateandTime(int daysToAdd) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, daysToAdd);
		Date day = cal1.getTime();
		String reqDate = df.format(day);

		return reqDate;
	}

	public String getTomorrowsDate(int Day) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, Day);
		String date = String.valueOf(cal1.getTimeInMillis());
		return date;
	}

	public void printHeaders(Map<String, String> headers) {

		Reporter.log("\nHeaders used are : ", true);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			Reporter.log(entry.getKey() + " : " + entry.getValue(), true);
		}
	}

	public Map<String, String> readHeadersFromExcel(String sheetname, String headersToCall) {

		Workbook wb;
		try {

			HashMap<String, String> headers = new HashMap<String, String>();

			String key, value;
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");

			wb = WorkbookFactory.create(file);

			Sheet sheet = wb.getSheet(sheetname);

			for (Row currentRow : sheet) {
				if (currentRow.getCell(0).getStringCellValue().toLowerCase().equals(headersToCall)) {
					Row headerKeyRow = sheet.getRow(currentRow.getRowNum() + 1);
					Row headerValueRow = sheet.getRow(currentRow.getRowNum() + 2);
					for (int i = 0; i < (headerKeyRow.getLastCellNum() - headerKeyRow.getFirstCellNum()); i++) {
						key = headerKeyRow.getCell(i).getStringCellValue();
						if (headerValueRow.getCell(i)
								.getCellType() == headerValueRow.getCell(i).getCellType().NUMERIC) {
							headerValueRow.getCell(i).setCellType(CellType.STRING);
						}
						value = headerValueRow.getCell(i).getStringCellValue();
						headers.put(key, value);
					}
					return headers;
				}
			}
			return headers;

		} catch (NullPointerException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (IOException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	/*
	 * Returns a random number for mobile number using utils from apache commons
	 */
	public String generateRandomStan() {

		return RandomStringUtils.randomAlphanumeric(6);

	}

	public String generateRandomMobileNumber() {

		String randomNumbers = RandomStringUtils.randomNumeric(5);

		String phNo = 97123 + randomNumbers;

		return phNo;

	}

	
	public String generateRandomNumber1(int num) {

		String Numbers = RandomStringUtils.randomNumeric(num);

		String No = Numbers;

		return No;

	}

	/*
	 * Returns a random number for stan using utils from apache commons
	 */
	public String generateRandomClientRefNumber() {

		return RandomStringUtils.randomNumeric(12);

	}

	public String generateRandomAlphaString(int count) {
		return RandomStringUtils.randomAlphabetic(count);
	}

	public String generateRandomAlphaNumericString(int count) {
		String s = RandomStringUtils.randomAlphanumeric(10);
		String alphaNum = s.toUpperCase();
		return alphaNum;
	}

	public String generateRandomAlphaNumericStringCount(int count) {
		String s = RandomStringUtils.randomAlphanumeric(count);
		String alphaNum = s.toUpperCase();
		return alphaNum;
	}

	public String generateRandomAlphaNumericStringAllLowerCase(int count) {
		String s = RandomStringUtils.randomAlphanumeric(10);
		String alphaNum = s.toLowerCase();
		return alphaNum;
	}

	/*
	 * Returns a random number for stan
	 */
	public String generateRandomNo(int count) {
		return "8" + RandomStringUtils.randomNumeric(count - 1);
	}

	/*
	 * Returns a random number for stan
	 */
	public String returnRandomNumber() {

		Random rand = new Random();
		BigInteger upperLimit = new BigInteger("999999999999999");
		BigInteger result;
		do {
			result = new BigInteger(upperLimit.bitLength(), rand); // (2^4-1) =
																	// 15 is the
																	// maximum
																	// value
		} while (result.compareTo(upperLimit) >= 0); // exclusive of 13

		return result.toString();
	}

	/*
	 * Writes the API Execution details by creating new sheet for every run to Excel
	 * Report File, Iterates through the cells for a particular testcaseID and
	 * populates the data
	 */

	public void writeExecutionStatusToExcelFlowmapper(String[] APIExecutionDetails)
			throws InvalidFormatException, IOException {
		try {
			int rowToUpdate = 0;
			File file = new File(configProperties.get("testReport"));
			if (!(file.exists())) {
				file.createNewFile();
				Workbook workbook = new HSSFWorkbook();
				Sheet worksheet = workbook.createSheet(configProperties.get("reportSheetName"));
				Row headers = worksheet.createRow(0);

				headers.createCell(0).setCellValue("BUILD NUMBER");
				// headers.createCell(1).setCellValue("API NAME");
				headers.createCell(1).setCellValue("TCID");
				headers.createCell(2).setCellValue("TEST DESCRIPTION");
				headers.createCell(3).setCellValue("RESULT");
				headers.createCell(4).setCellValue("(WARNING) REASON OF FAILURE");
				headers.createCell(5).setCellValue("RESPONSE TIME");
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
			}
			FileInputStream fileIn = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fileIn);
			Sheet worksheet = workbook.getSheet(configProperties.get("reportSheetName"));
			rowToUpdate = worksheet.getLastRowNum() + 1;
			int i;
			Row record = worksheet.createRow(rowToUpdate);
			org.apache.poi.ss.usermodel.Cell cell = null;
			for (i = 0; i <= APIExecutionDetails.length; i++) {
				cell = record.createCell(i);
				if (i == 6) {
					cell.setCellValue(getTimeTaken());
				} else {
					cell.setCellValue(APIExecutionDetails[i]);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(new File(configProperties.get("testReport")));
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the test case execution status based on its execution status code
	 */
	public String getExecutionResultStatus(int statusCode) {

		String testStatus = null;
		if (statusCode == 1) {
			testStatus = "PASS";
		} else if (statusCode == 2) {
			testStatus = "FAIL";
		} else if (statusCode == 3) {
			testStatus = "SKIP";
		}

		return testStatus;
	}

	/*
	 * Returns the set of all API's executed, as per its excel data
	 */
	public Set<String> returnAllAPINames(String excelFileName, String sheetName)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		Set<String> allAPI = new HashSet<String>();

		FileInputStream file = new FileInputStream(excelFileName);
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();

		Row headers = it.next();
		while (it.hasNext()) {

			Row record = it.next();
			String cellValue = record.getCell(2).toString();
			allAPI.add(cellValue);
		}
		return allAPI;
	}

	/*
	 * Returns the total, passed, failed and skipped tests for a particular API from
	 * its Excel data
	 */
	public int[] returnTestCountPerAPI(String excelFileName, String sheetName, String API)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		FileInputStream file = new FileInputStream(excelFileName);
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();
		int total = 0, pass = 0, fail = 0, skip = 0;

		Row headers = it.next();
		while (it.hasNext()) {

			Row record = it.next();
			String cellValue = record.getCell(2).toString().trim();
			if (cellValue.equalsIgnoreCase(API)) {
				String status = record.getCell(4).toString().trim();
				if (status.equalsIgnoreCase("PASS")) {
					pass++;
					total++;
				} else if (status.equalsIgnoreCase("FAIL")) {
					fail++;
					total++;
				} else if (status.equalsIgnoreCase("SKIP")) {
					skip++;
					total++;
				}
			}
		}
		return new int[] { total, pass, fail, skip };
	}

	public String report() throws EncryptedDocumentException, InvalidFormatException, IOException {
		StringBuilder form = new StringBuilder();
		LinkedHashMap<String, int[]> result = consolidatedReport();
		int totalTestExecuted = 0, totalPassed = 0, totalFailed = 0;
		form.append("<html>"
				+ "<table style='border-spacing: 0px; padding:5px; font-family: monospace; font-size: 1em;'>"
				+ "<tr style='background-color:#a3d5f5;font-weight: bold;font-family: monospace;font-size: 1.1em;'> "
				+ "<td style='border:1px solid;padding:5px'>DATE OF EXECUTION</td>"
				+ "<td style='border:1px solid;padding:5px'>BUILDNUMBER</td>"
				+ "<td style='border:1px solid;padding:5px'>FLOW NAME</td>"
				+ "<td style='border:1px solid;padding:5px'>TOTAL FLOWS EXECUTED</td>"
				+ "<td style='border:1px solid;padding:5px'>Passed Scenarios</td>"
				+ "<td style='border:1px solid;padding:5px'>Failed Scenarios</td>" + "</tr>");
		for (Map.Entry<String, int[]> data : result.entrySet()) {
			form.append("<tr style='font-family: monospace;font-size: 1em'>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + getTodaysDate("dd-MM-yyyy")
					+ "<td style='border:1px solid;text-align: center;padding:5px'>"
					+ configProperties.get("buildNumber") + "</td>" + "</td>"
					+ "<td style='border:1px solid;padding:5px'>" + data.getKey() + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[2] + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[0] + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[1] + "</td>"
					+ "</tr>");
			totalTestExecuted += data.getValue()[2];
			totalPassed += data.getValue()[0];
			totalFailed += data.getValue()[1];

		}
		form.append("<tr style='font-family: monospace;font-size: 1em'>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'></td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'></td>"
				+ "<td style='border:1px solid;padding:5px'></td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>" + totalTestExecuted + "</td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>"
				+ (totalPassed * 100 / totalTestExecuted) + " %</td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>"
				+ (totalFailed * 100 / totalTestExecuted) + " %</td>" + "</tr>");
		form.append("</table></html>");
		return form.toString();
	}

	public LinkedHashMap<String, int[]> consolidatedReport()
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		String sheetname = configProperties.get("reportSheetName");
		FileInputStream file = new FileInputStream(configProperties.get("testReport"));
		LinkedHashMap<String, int[]> executionResult = new LinkedHashMap<String, int[]>();
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetname);
		Iterator<Row> it = sheet.rowIterator();
		Row headers = it.next();
		while (it.hasNext()) {
			Row record = it.next();
			String api = record.getCell(3).toString();
			String result = record.getCell(5).toString();
			if (null != executionResult.get(api)) {
				if (result.equalsIgnoreCase("PASS")) {
					++executionResult.get(api)[0];
				} else if (result.equalsIgnoreCase("FAIL")) {
					++executionResult.get(api)[1];
				}
				++executionResult.get(api)[2];
			} else {
				if (result.equalsIgnoreCase("PASS")) {
					executionResult.put(api, new int[] { 1, 0, 1 });
				} else if (result.equalsIgnoreCase("FAIL")) {
					executionResult.put(api, new int[] { 0, 1, 1 });
				}

			}

		}

		/*
		 * for(Map.Entry<String, int[]> value : executionResult.entrySet()){ String key
		 * = value.getKey(); int [] arr = value.getValue(); System.out.println(key+"  "
		 * +Arrays.toString(arr)); }
		 */

		return executionResult;
	}

	public String getFailureReason() {
		return JavaUtils.failureReason;
	}

	public void setFailureReason(String msg) {
		JavaUtils.failureReason = msg;
	}

	public String getTimeTaken() {
		return JavaUtils.timeTaken;
	}

	public void setTimeTaken(String tt) {
		JavaUtils.timeTaken = tt;
	}

	public List<HashMap<String, String>> getListofHashMap(HashMap<String, String> usrData, String[] keys) {
		List<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
		HashMap<String, ArrayList<String>> innerMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, String> data = new HashMap<String, String>();
		for (String k : keys) {
			Set<String> key = new TreeSet<>(usrData.keySet().stream()
					.filter(s -> s.toLowerCase().startsWith(k.toLowerCase())).collect(Collectors.toSet()));
			innerMap.put(k, new ArrayList<String>(key));
		}
		/*
		 * for (int i = 0; i < keys.length; i++) { for (int j = 0; j <
		 * innerMap.get(keys[0]).size(); j++) { data.put(keys[i],
		 * innerMap.get(keys[i]).get(j)); } out.add(data); data = new HashMap<String,
		 * String>(); }
		 */
		for (int j = 0; j < innerMap.get(keys[0]).size(); j++) {
			for (int i = 0; i < keys.length; i++) {
				data.put(keys[i], innerMap.get(keys[i]).get(j));
			}

			out.add(data);
			data = new HashMap<String, String>();
		}
		return out;
	}

	public String generateRandomEmail() {
		return (RandomStringUtils.randomAlphabetic(5) + "@gmail.com").toString();
	}

	
	public static String getYesterdayFormatted(String format) {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		String formattedDate = yesterday.format(formatter);

		return formattedDate;
	}

	public String getFutureDate(int Day) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, Day);
		String date = String.valueOf(cal1.getTimeInMillis());
		return date;
	}

	public void assertResponceValues(String parameterName, String expectedValue, String actualValue)
			throws InvalidFormatException, IOException {
		StringBuilder mess = new StringBuilder();
		try {
			Assert.assertEquals(actualValue.replaceAll("[\\[\\]]", ""), expectedValue,
					"FAILURE..! Assertion for " + parameterName + " failed..!");
			Reporter.log("\n" + parameterName + " Assertion successful..!", true);
		} catch (AssertionError ae) {
			mess.append(
					"Response field assertion failed. Expected [" + expectedValue + "] Actual [" + actualValue + "]\n");
			Reporter.log(ae.getMessage(), true);
			String errMsgList[] = { mess.toString() };
			System.out.println("Size of errMsg " + errMsgList.length);
			writeExecutionStatusToExcel(errMsgList);

		}
	}

	public String getTestExcutionTime(long millisec) {
		String dateFormat = "dd-MM-yyyy hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisec);
		String executionTime = simpleDateFormat.format(calendar.getTime());
		return executionTime;
	}

	public void writeRequestToExcel(String apiName, String apiDescription, String result, String response)
			throws InvalidFormatException, IOException {

		try {
			int rowToUpdate = 0;

			File file = new File(configProperties.get("requestReport"));
			if (!(file.exists())) {
				file.createNewFile();
				Workbook workbook = new HSSFWorkbook();
				Sheet worksheet = workbook.createSheet(configProperties.get("reportSheetName"));
				Row headers = worksheet.createRow(0);
				headers.createCell(0).setCellValue("API URL");
				headers.createCell(1).setCellValue("Request");
				headers.createCell(2).setCellValue("Result");
				headers.createCell(3).setCellValue("Response");
				headers.createCell(4).setCellValue("API Description");
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
			}
			FileInputStream fileIn = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fileIn);
			Sheet worksheet = workbook.getSheet(configProperties.get("reportSheetName"));
			rowToUpdate = worksheet.getLastRowNum() + 1;
			int i;
			Row record = worksheet.createRow(rowToUpdate);
			org.apache.poi.ss.usermodel.Cell cell = null;
			cell = record.createCell(0);
			cell.setCellValue(apiName);
			cell = record.createCell(1);
			String req = getvalueFromTestDataIni("request");
			cell.setCellValue(req);
			cell = record.createCell(2);
			cell.setCellValue(result);
			cell = record.createCell(3);
			cell.setCellValue(response);
			cell = record.createCell(4);
			cell.setCellValue(apiDescription);

			FileOutputStream fileOut = new FileOutputStream(new File(configProperties.get("requestReport")));
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		}
	}

	public long getDateInMillis(String date) {

		LocalDate localDate = LocalDate.parse(date);

		// LocalDate to epoch days
		long numberOfDays = localDate.toEpochDay();
		System.out.println(numberOfDays);

		// LocalDate to epoch milliseconds
		Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
		long timeInMillis = instant.toEpochMilli();
		System.out.println(timeInMillis);

		instant = localDate.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
		timeInMillis = instant.toEpochMilli();
		System.out.println(timeInMillis);

		return timeInMillis;
	}

	public String getFutureDates(int Day, String format) {
		Format formatter = new SimpleDateFormat(format);
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, Day);
		Date date = cal1.getTime();
		String furDate = formatter.format(date);
		Log.info("Future Date " + furDate);
		return furDate;
	}

	public void assertValuesFloat(String responceValue, String sheetValues, String fieldName)
			throws InvalidFormatException, IOException {
		StringBuilder mess = new StringBuilder();
		try {
			/*
			 * Assert.assertEquals(sheetValues, responceValue, "FAILURE..! Assertion for " +
			 * fieldName + " failed..!");
			 */

			double recValue = (Math.round((Double.parseDouble(responceValue) * 100.0))) / 100.0;
			double sheeetValue = Double.parseDouble(sheetValues);
			System.out.println("responce Value " + recValue);
			System.out.println("Sheet Value " + sheeetValue);

			Assert.assertEquals(recValue, sheeetValue, "FAILURE..! Assertion for " + fieldName + " failed..!");

			Reporter.log("\n Assertion successful..!", true);
		} catch (AssertionError ae) {
			mess.append(
					"Response field assertion failed. Expected [" + sheetValues + "] Actual [" + responceValue + "]\n");
			Reporter.log(ae.getMessage(), true);
			String errMsgList[] = { mess.toString() };
			System.out.println("Size of errMsg " + errMsgList.length);
			// writeExecutionStatusToExcel(errMsgList);

		}

	}

	public void assertValues(String responceValue, String sheetValues, String fieldName)
			throws InvalidFormatException, IOException {
		StringBuilder mess = new StringBuilder();
		try {
			Assert.assertEquals(sheetValues, responceValue, "FAILURE..! Assertion for " + fieldName + " failed..!");
			Reporter.log("\n Assertion successful..!", true);
		} catch (AssertionError ae) {
			mess.append(
					"Response field assertion failed. Expected [" + sheetValues + "] Actual [" + responceValue + "]\n");
			Reporter.log(ae.getMessage(), true);
			String errMsgList[] = { mess.toString() };
			System.out.println("Size of errMsg " + errMsgList.length);
			writeExecutionStatusToExcel(errMsgList);

		}

	}

	public void deleteReportsAndScreenshots() {

		File file = new File("./reports/TestReport.xlsx");
		File reqFile = new File("./reports/RequestReport.xlsx");

		if (file.exists()) {
			file.delete();
			System.out.println("previous report file Deleted");

		}
		if (reqFile.exists()) {
			reqFile.delete();
			System.out.println("previous Request report file Deleted");

		}

	}

	public void writeExecutionStatusToExcel(String[] APIExecutionDetails) throws InvalidFormatException, IOException {

		try {
			int rowToUpdate = 0;
			File file = new File(configProperties.get("testReport"));
			if (!(file.exists())) {
				file.createNewFile();
				Workbook workbook = new HSSFWorkbook();
				Sheet worksheet = workbook.createSheet(configProperties.get("reportSheetName"));
				Row headers = worksheet.createRow(0);

				headers.createCell(0).setCellValue("BUILD NUMBER");
				headers.createCell(1).setCellValue("API NAME");
				headers.createCell(2).setCellValue("TCID");
				headers.createCell(3).setCellValue("TEST DESCRIPTION");
				headers.createCell(4).setCellValue("EXPECTED RESULT");
				headers.createCell(5).setCellValue("RESULT");
				headers.createCell(6).setCellValue("(WARNING) REASON OF FAILURE");
				headers.createCell(7).setCellValue("RESPONSE TIME");
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
			}
			FileInputStream fileIn = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fileIn);
			Sheet worksheet = workbook.getSheet(configProperties.get("reportSheetName"));
			rowToUpdate = worksheet.getLastRowNum() + 1;
			int i;
			Row record = worksheet.createRow(rowToUpdate);
			org.apache.poi.ss.usermodel.Cell cell = null;

			for (i = 0; i < APIExecutionDetails.length; i++) {

				System.out.println(i + " " + APIExecutionDetails[i]);

			}

			for (i = 0; i < APIExecutionDetails.length; i++) {
				cell = record.createCell(i);
				if (i == 7) {
					cell.setCellValue(getTimeTaken());
				} else {
					cell.setCellValue(APIExecutionDetails[i]);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(new File(configProperties.get("testReport")));
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			writeRequestToExcel(sectionName, fileName, failureReason, assertionMessage);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		Date endDate = calendar.getTime();
		System.out.println("end time of test report is:" + endDate);
	}

	public String getBase64EncodedValue(String data) {
		Base64.Encoder enc = Base64.getEncoder();

		String encodedValue = enc.encodeToString(data.getBytes());

		return encodedValue;
	}

	public void assertResponse(String api, String expectedMessage, String actualMessage) {

		int flag = 0;

		StringBuilder msg = new StringBuilder();

		try {
			Assert.assertEquals(actualMessage, expectedMessage,
					"FAILURE..! Assertion for " + api + " response message failed..!");
			Reporter.log("\nResponse message Assertion successful..!", true);
		} catch (AssertionError ae) {
			msg.append("Response status assertion failed. Expected [" + expectedMessage + "], Actual [" + actualMessage
					+ "]\n");
			Reporter.log(ae.getMessage(), true);
			flag = 1;
		}

		if (flag == 1) {
			setFailureReason(msg.toString());
			Assert.fail("TEST FAILED!!! Assertion error..!");
		}
	}

}