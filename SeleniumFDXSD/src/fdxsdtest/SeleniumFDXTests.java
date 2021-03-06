package fdxsdtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.lang.StringBuilder;

public class SeleniumFDXTests {
	static StringBuilder msg = new StringBuilder();

	static double loginTime, quoteTime;

	public static void pause(final int iTimeInMillis) {
		try {
			Thread.sleep(iTimeInMillis);
		} catch (InterruptedException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void doPing() {
		String ip = "www.fedexsameday.com";

		String pingCmd = "ping " + ip;
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(pingCmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) { //
				System.out.println("inputLine : " + inputLine);
				msg.append("\t").append(inputLine).append("\n");
			}

			in.close();
			msg.append("\n\n");
		} catch (IOException e) {
		}

	}

	public static void doLogin(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 50);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Header_fdx_main_liLogin")));
		driver.findElement(By.id("Header_fdx_main_liLogin")).click();
		driver.findElement(By.id("Header_fdx_main_logon_name")).clear();
		driver.findElement(By.id("Header_fdx_main_logon_name")).sendKeys("fxo_rg1");
		// enter password
		driver.findElement(By.id("Header_fdx_main_logon_password")).clear();
		driver.findElement(By.id("Header_fdx_main_logon_password")).sendKeys("Fedex123");

		// click on 'Login'
		long start = System.nanoTime();
		driver.findElement(By.id("Header_fdx_main_cmdMenuLogin")).click();
		long end = System.nanoTime();
		loginTime = (end - start) * 1.0e-9;

		msg.append("\tLogin Time (in Seconds) = " + loginTime + "\n");
		msg.append(loginTime + ",");

	}

	@Test
	public static void fedExSameDay() throws IOException, InterruptedException {
		WebDriver driver = null;
		/*
		 * System.out.println("Testing email...."); String subject1 =
		 * "Test Email - FedEx SameDay WebSite Performance"; // String Message =
		 * "MNX SMTP Test Message";
		 * 
		 * try { NglogEmail.sendMail("ravina.prajapati@samyak.com", subject1,
		 * msg.toString(), ""); } catch (Exception ex) {
		 * Logger.getLogger(SeleniumFDXTests.class.getName()).log(Level.SEVERE, null,
		 * ex); }
		 */

		// try {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy");
		String currentDate = sdf1.format(c.getTime());
		String fileName = "FSDdotComPerfromance-" + sdf2.format(c.getTime()) + ".txt";

		msg.append("\n\nCurrent Date & Time = " + currentDate + "\n\n");
		msg.append(currentDate + ",");

		long start, end;
		System.setProperty("webdriver.chrome.driver", ".//Drivers//chromedriver.exe");
		driver = new ChromeDriver();

		// Main URL
		String baseUrl = "https://www.fedexsameday.com/fdx_main.aspx";

		driver.get(baseUrl);

		doPing();

		driver.manage().window().maximize();
		System.out.println("windows is maximized");
		WebDriverWait wait = new WebDriverWait(driver, 50);

		// --login
		doLogin(driver);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@class=\"fdx-o-grid\"]")));
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(".\\Screenshot\\HomePage.png"));

		// --PickupZip
		driver.findElement(By.id("txtOrig")).clear();
		System.out.println("clear pickup code");
		driver.findElement(By.id("txtOrig")).sendKeys("92612");
		System.out.println("ENtered pickup code");

		// --DeliveryZip
		driver.findElement(By.id("txtDest")).clear();
		System.out.println("cleare dest code");
		driver.findElement(By.id("txtDest")).sendKeys("90045");
		System.out.println("entered dest code");
		driver.findElement(By.id("txtDest")).sendKeys(Keys.TAB);

		// --Selected Packages
		Select qty = new Select(driver.findElement(By.id("txtQty0")));
		qty.selectByValue("5");
		System.out.println("selected  qty ");
		Thread.sleep(2000);

		// --Weight
		wait.until(ExpectedConditions.elementToBeClickable(By.id("txtActWt0")));
		driver.findElement(By.id("txtActWt0")).clear();
		driver.findElement(By.id("txtActWt0")).sendKeys("5");
		System.out.println("entered weight");
		pause(200);

		// --Dim(L)
		driver.findElement(By.id("txtDimLen0")).clear();
		driver.findElement(By.id("txtDimLen0")).sendKeys("5");
		System.out.println("entered dim");
		pause(200);

		// --Dim(W)
		driver.findElement(By.id("txtDimWid0")).clear();
		driver.findElement(By.id("txtDimWid0")).sendKeys("5");
		System.out.println("entered wid");
		pause(200);

		// --Dim(H)
		driver.findElement(By.id("txtDimHt0")).clear();
		driver.findElement(By.id("txtDimHt0")).sendKeys("5");
		System.out.println("entered Ht");
		pause(200);

		// --ShipDate
		driver.findElement(By.id("datepicker")).clear();
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);
		String newDate = dateFormat.format(cal.getTime());
		driver.findElement(By.id("datepicker")).sendKeys(newDate);
		System.out.println("Selected Date,1 day older from current Date");
		driver.findElement(By.id("datepicker")).sendKeys(Keys.TAB);
		pause(200);

		// Select hour
		Select hour = new Select(driver.findElement(By.id("ddlPickupHour")));
		hour.selectByIndex(7); // AM
		System.out.println("select hour");
		pause(200);

		// Select minuts
		Select minutes = new Select(driver.findElement(By.id("ddlPickupMinutes")));
		minutes.selectByIndex(1); // AM
		System.out.println("select minutes");
		pause(200);

		// Select AM/PM
		Select AmPm = new Select(driver.findElement(By.id("ddlTimeType")));
		AmPm.selectByIndex(0); // AM
		System.out.println("select ampm");
		pause(200);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		start = System.nanoTime();
		// --Click on show rates
		driver.findElement(By.id("btngetQuickquote")).click();
		pause(200);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table[@class=\"fdxRatetable\"]")));
		WebElement fdxRateTable = driver.findElement(By.xpath("//table[@class=\"fdxRatetable\"]"));
		js.executeScript("arguments[0].scrollIntoView();", fdxRateTable);
		File scrFile1 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile1, new File(".\\Screenshot\\Rates.png"));

		end = System.nanoTime();
		quoteTime = (end - start) * 1.0e-9;
		System.out.println("Login Time (in Seconds) = " + loginTime);
		System.out.println("Quote Time (in Seconds) = " + quoteTime);
		msg.append("\tQuote Time (in Seconds) = " + quoteTime + "\n");
		msg.append("\tQuote Time (in Seconds) = " + quoteTime + "\n");
		msg.append(quoteTime + ",");
		msg.append("SIPL01" + "\n");
		msg.append("SamyakAzure" + "\n");
		pause(30000);

		System.out.println(msg.toString());

		File file = new File(fileName);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), Boolean.TRUE);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(msg.toString());
		bw.close();

//			 save data in Data base

		/*
		 * DatabaseConnection databaseConnection = new DatabaseConnection();
		 * databaseConnection._insertDataFromDB(currentDate, loginTime, quoteTime);
		 * 
		 * String sql =
		 * "Insert into FSDPerfromance (MonitorDateTime, LoginTime, QuoteTime, System) Values ('"
		 * + currentDate + "', " + loginTime + ", " + quoteTime + ",'pc7738' )";
		 * 
		 * System.out.println(sql);
		 * System.out.println("sql executed done from FDXTest"); // By Ravina
		 */

		// Open this code and add both email for mail sending as par discuss mukund sir
		// and parth sir 12/11/2019 7:15
		if (loginTime > 10 || quoteTime > 10) {
			String subject = "FedEx SameDay WebSite Performance";
			try {

				/*
				 * NglogEmail.sendMail(
				 * 
				 * "jdhakshana@nglog.com,rtaneja@nglog.com,sdas@samyak.com,jbhatt@samyak.com,rpanchal@samyak.com,pgandhi@samyak.com,gsanghvi@samyak.com",
				 * subject, msg.toString(), "");
				 */

				// mthakkar@samyak.com, pgandhi@samyak.com, asharma@samyak.com
				NglogEmail.sendMail("ravina.prajapati@samyak.com", subject, msg.toString(), "");
			} catch (Exception ex) {
				Logger.getLogger(SeleniumFDXTests.class.getName()).log(Level.SEVERE, null, ex);
				ex.printStackTrace();
			}
		}

		// Close the browser

		/*
		 * } catch (Exception ex) { System.out.println(ex.toString()); }
		 */
		// finally {
		driver.close();
		driver.quit();
		// }

	}
}
