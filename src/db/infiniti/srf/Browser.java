package db.infiniti.srf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Browser {

	WebDriver driver = new FirefoxDriver();
	
	public void captureSc(String savePath) {

	    
/*	    WebDriver driver = new FirefoxDriver();
	 //   driver.get("http://www.google.com/");
	    try {
	        WebDriver augmentedDriver = new Augmenter().augment(driver);
	        File source = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
	        path = "./target/screenshots/" + source.getName();
	        FileUtils.copyFile(source, new File(path)); 
	    }
	    catch(IOException e) {
	        path = "Failed to capture screenshot: " + e.getMessage();
	    }
	    return path;*/

	    //WebDriver driver = new FirefoxDriver();
//	    driver.get(url); //"http://www.google.com/"
	    
	    File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	    // Now you can do whatever you need to do with it, for example copy somewhere
	    try {
			FileUtils.copyFile(scrFile, new File(savePath+"_sc.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String getPageSource(String url){
	//	driver.navigate().to(url);
		driver.get(url);
		return driver.getPageSource();
	}
	public void stopFXDriver(){
		this.driver.close();
	}
	
	public String getPageText(){
		List<WebElement> qResS = runXPathQuery("//*[contains(.,'content')]");
		if(qResS.isEmpty()){
			qResS = runXPathQuery("//*");
		}
		String text = qResS.get(0).getText().toLowerCase();
		return text;
	}
	public List<WebElement> runXPathQuery(String xpathExpression){
		List<WebElement> qResS= driver.findElements(By.xpath(xpathExpression)); 
		/*for(int i=0; i<l.size(); i++) {
		    	WebElement wel = qResS.get(i);
		    	wel.
		    	new HtmlElementGeometry(wel.getLocation().x,wel.getLocation().y,wel.getSize().getHeight(),wel.getSize().getWidth());
		    }*/
		return qResS;
	}
}
