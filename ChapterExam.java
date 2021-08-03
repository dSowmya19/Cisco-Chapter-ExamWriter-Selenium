
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

public class ChapterExam {
	public static void answer(String s, ChromeDriver driver, ArrayList<String> tabs2,String qid) throws InterruptedException {
		String xml = String.format("//h3[text()='%s']//following-sibling::ul//li//span", s);
		try {
			List<WebElement> answerElements = driver.findElements(By.xpath(xml));
			Iterator<WebElement> itr = answerElements.iterator();
			ArrayList<String> answers = new ArrayList<String>();
			while (itr.hasNext()) {
				String a = itr.next().getText();
				//System.out.println(a);
				answers.add(a);
			}
			driver.switchTo().window(tabs2.get(0));
			Thread.sleep(500);
			for (String answer : answers) {
				String x = String.format("//input[@name='%s']/../div[2]/div[2]/div/label[text() = '%s']",qid, answer);
				WebElement l = driver.findElement(By.xpath(x));
				if(!(l.isSelected()))
				{
					driver.executeScript("arguments[0].scrollIntoView(true);", l);
					driver.findElement(By.xpath(x)).click();
					Thread.sleep(500);
				}
			}

		} catch (Exception e) {
			System.out.println("Unable to find Answer"+e);
		}

	}

	public static void main(String args[]) throws InterruptedException, AWTException ,IOException{
		System.setProperty("webdriver.chrome.driver", "C:\\Browser Driver\\chromedriver.exe");
		ChromeDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));
	    System.out.println("Enter chapter Exam Link: ");
	    String link = reader.readLine();
	    
	    System.out.println("Enter chapter Solution Link: ");
	    String sollink = reader.readLine();
		

		driver.get(link);
		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("idp-discovery-username")));
	      
	      System.out.println("Enter your user name: ");
	      String name = reader.readLine();
	      
	      System.out.println("Enter your password: ");
	      String pwd = reader.readLine();

		element.sendKeys(name);
		driver.findElementById("idp-discovery-submit").click();
		Thread.sleep(5000);
		driver.findElementById("okta-signin-password").sendKeys(pwd);
		driver.findElementById("okta-signin-submit").click();
		Thread.sleep(5000);

		boolean first = true;
		while (true) 
		{
			if (driver.findElements(By.xpath("//input[@name = 'next']")).isEmpty()) {
				System.out.println("Bye Prands");break;
			}
				
			List<WebElement> questionsElements = driver.findElements(By.xpath("//div[@class='qtext']"));
			Iterator<WebElement> itr = questionsElements.iterator();
			ArrayList<String> questions = new ArrayList<String>();
			ArrayList<String> qids = new ArrayList<String>();
			while (itr.hasNext()) 
			{
				WebElement z = itr.next();
				qids.add(z.findElement(By.xpath("preceding-sibling::input")).getAttribute("name"));
				questions.add(z.getText());
			}
			//System.out.println(qids);
			if(first == true)
			{
				Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_T);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.keyRelease(KeyEvent.VK_T);
				Thread.sleep(1000);
			}
			
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
			System.out.println(tabs2);
			if(first == true)
			{
				
				driver.switchTo().window(tabs2.get(1));
				driver.get(sollink);
				first = false;
			}
			else {driver.switchTo().window(tabs2.get(1));}
			Thread.sleep(1000);
			Iterator<String> i = questions.iterator();
			int count = 0;
			while (i.hasNext()) 
			{
				String a = i.next();
				//System.out.println(count + a);
				answer(a, driver, tabs2,qids.get(count));
				count += 1;
				driver.switchTo().window(tabs2.get(1));
				Thread.sleep(500);
			}
			driver.switchTo().window(tabs2.get(0));
			driver.findElement(By.xpath("//input[@name = 'next']")).click();

		}

	}
}