package com.auto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AutoApproval {
	public static void main(String[] args) throws IOException
	{
		
		File ufile = new File("temp.txt");
        ufile.createNewFile();
        FileWriter sr = new FileWriter(ufile);
        BufferedWriter ur = new BufferedWriter(sr);
       
		File log = new File("log.txt");
        log.createNewFile();
        FileWriter rr = new FileWriter(log);
        BufferedWriter lr = new BufferedWriter(rr);
        
        File file1 = new File("unsuccessful.txt");
        file1.createNewFile();
        FileWriter rw= new FileWriter(file1);
        BufferedWriter urw = new BufferedWriter(rw);
        
        File file2 = new File("properties.txt");
        FileInputStream fileInput = null;
        fileInput = new FileInputStream(file2);
        Properties prop = new Properties();
        prop.load(fileInput);
        
	 	String ifile="list.txt";
		FileReader FR = new FileReader(ifile);
		BufferedReader BR = new BufferedReader(FR);
		String line;
		Scanner scan=new Scanner(System.in);
		System.out.print("Enter your UserName: ");
		String userName = scan.nextLine();
		System.out.print("Enter your Passwrd: ");
		String passwrd = scan.nextLine();
		System.out.print("Choose Selection: \n");
		System.out.print("1.Submit for Approval \n");
		System.out.print("2.Approve \n");
		System.out.print("3.Pre-Approve \n");
		System.out.print("4.Return \n");
		String selection = null;
		String comment = null;
		int a = scan.nextInt();
        switch (a) {
            case 1:  selection = "Submit for Approval";
                     System.out.println("You have selected 'Submit for Approval' ...\n");
                     break;
            case 2:  selection = " Approve";
                     System.out.println("You have selected 'Approve' ...\n");
                     System.out.print("Do You Want Enter workflow Comments(If YES Enter comments, NO(Type 'Enter Key')):");
         			 scan.nextLine();
         			 String options = scan.nextLine();
         			 int aa=options.length();
         			 if (aa>0)
         			 {
         				comment = options;
         			 } 
                     break;
            case 3:  selection = "Pre-Approve";
                      System.out.println("You have selected 'Pre-Approve' ...\n");
                     break;
            case 4:  selection = "Return";
                     System.out.println("You have selected 'Return' ...\n");
                     System.out.print("Do You Want Enter Return Comments(If YES Enter comments, NO(Type 'Enter Key')):");
         			 scan.nextLine();
         			 String re = scan.nextLine();
         			 int e=re.length();
         			 if (e>0)
         			 {
         				comment = re;
         			 }
                     break;
            default: selection = "Invalid Selection";
                     System.out.println("Invalid Selection ...\n");
                     break;
        }
		scan.close();		
		
		
		System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
		WebDriver driver=new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.get(prop.getProperty("URL").trim());
        String title=prop.getProperty("TITLE");
	  if(driver.getTitle().contentEquals(title.trim()))
	    {
		driver.findElement(By.name("userId")).sendKeys(userName);
		driver.findElement(By.name("password")).sendKeys(passwrd);
		driver.findElement(By.name("subBtn")).click();
		String sub_title=prop.getProperty("SUB_TITLE");
		if(driver.getTitle().contentEquals(sub_title.trim()))
		{
		  int count=0;
	      while((line = BR.readLine())!= null)
	       {
	    	try{
	        count=1;
	        line=line.trim();
	        outerloop:
	    	for(int l=1;l<=2;l++)
	    	{
	    	 if(line.length()<1)
	    	  {
	    	  System.out.println("Skipping empty line....");	 
	    	  lr.write("INFO:  "+"Skipping empty line....");
	    	  lr.newLine();
	    	  break;
	    	  }
	    	driver.findElement(By.name("keyword")).clear();
			driver.findElement(By.name("keyword")).sendKeys(line.trim());
			driver.findElement(By.name("btnSearch")).click();
			List<WebElement> rowCount=driver.findElements(By.xpath("//table[@class='table_results']/tbody/tr"));
			int s=rowCount.size();
			if(rowCount.size()==0)
			 {
			 lr.write("INFO:  "+line+" ----  No record found");
			 lr.newLine();
			 break;
			 }
			String first_part = "//table[@class='table_results']/tbody/tr[";
			String second_part = "]/td[";
			String third_part = "]";
			 for (int i=1;i<rowCount.size();)
			  {
			  i=i+1;
			  String final_xpath = first_part+i+second_part+6+third_part;
			  String Table_data = driver.findElement(By.xpath(final_xpath)).getText();
			  String state=driver.findElement(By.xpath(first_part+i+second_part+2+third_part)).getText();
		      String Task_Type=driver.findElement(By.xpath(first_part+i+second_part+7+third_part)).getText();
		      String link=driver.findElement(By.xpath(first_part+i+second_part+1+third_part+"/a[2]")).getText();
			  if(Table_data.contentEquals(line.trim()))
			   {
			   if(Table_data.contentEquals(line.trim()) && state.contentEquals("Open"))
			   	{
			    driver.findElement(By.xpath(first_part+i+second_part+1+third_part+"/a[2]")).click();
			    WebDriverWait wait = new WebDriverWait(driver, 15);
			    wait.until(ExpectedConditions.presenceOfElementLocated(By.name("moreComments")));      	  
			    WebElement selectElement = driver.findElement(By.name("wfPath"));
			    int t=0;
			    if (selection.contains(" Approve"))
	        	  {
			    	t=1;
                driver.findElement(By.name("moreComments")).sendKeys(comment);
                
                }
			    if (selection.contains("Return"))
	        	  {
              driver.findElement(By.name("moreComments")).sendKeys(comment);
              
	        	  }
			    Select select = new Select(selectElement);
			    List<WebElement> allOptions = select.getOptions();    	  
			    int r=0;
			    if(allOptions.size()>0)
			     {
			     for (WebElement option : allOptions)
			      {
			      if(option.getText().contains(selection) || option.getText().startsWith(selection.trim()))
		            {
			    	  
			        r=1;
		        	break;
		            }
			      }
			     }	  
			     else
			      {
			       lr.write("WARNING:  "+line+" ----  check with right approver");
			       lr.newLine();
			       urw.write(line);
			       urw.newLine();
			       driver.findElement(By.name("btnCancel")).click();
			       break outerloop;
			      }
				  if (r==1)
	        	    {
			        Select sel = new Select(driver.findElement(By.name("wfPath")));
			        sel.selectByVisibleText(selection.trim());
			        Thread.sleep(2000);
			        driver.findElement(By.name("btnSelect")).click();
	        		}
		          else
		        	{
		        	lr.write("WARNING:  "+line+" ----  is in preapproval/Wrong Slection");
		        	lr.newLine();
		        	urw.write(line);
		        	urw.newLine();
		            driver.findElement(By.name("btnCancel")).click();
		            break outerloop;
		        	}
			       Thread.sleep(2000);
				   boolean isPresent= driver.findElements(By.xpath("//form/span[1]")).size()!=0;
				     if(isPresent==true)
					  {
					    String ms=driver.findElement(By.xpath("//form/span[1]")).getText();
						if(ms.contentEquals("Release cannot be Approved until the Product has been Approved along with the Release that created the Product."))
						  {
						   ur.write(line);
						   ur.newLine();
						   lr.write("WARNING:  "+line+" --- "+ms);
						   lr.newLine();
						   driver.findElement(By.name("btnCancel")).click();
						   break outerloop;
						  }
						   lr.write("WARNING:  "+line+" --- "+ms);
						   lr.newLine();
						   urw.write(line);
						   urw.newLine();
						   driver.findElement(By.name("btnCancel")).click();
						   break outerloop;
						  }
				     	  if(t==1)
				     	  {
					      lr.write("SUCCESS:  "+Table_data+" ----  "+Task_Type+" is successfully  "+selection+"d");
					      lr.newLine();
					      break;
				     	  }
				     	  else{
				     		 lr.write("SUCCESS:  "+Table_data+" ----  "+Task_Type+" is successfully  "+selection);
						     lr.newLine();
						     break;
				     	  }
			        }
			        else
					 {
			          lr.write("WARNING:  "+Table_data+" ----  "+Task_Type+" is  "+state+" state");
			          lr.newLine();
			          urw.write(line);
			          urw.newLine();
					 }
			     }
			     else
			      {
			       if(i==rowCount.size())
			       {
			        lr.write("INFO:  "+line+" --- No records found");
			        lr.newLine();
			       }
			      }
			  }
			}
	    	  }
	    	  catch(Exception e)
		       {
		        lr.write("EXCEPTION: "+e.toString());
			    lr.newLine();
		        driver.findElement(By.linkText("Home")).click();
		        }
		 }
	    if(count==0)
	     {
	      System.out.println("Input file is empty....");	
	      lr.write("INFO:  "+"Input file is empty.....");
	      lr.newLine();
	     }
		
		
		}
	 else
	  {
	  System.out.println(driver.findElement(By.cssSelector("td.login-error-message")).getText()); 
	  lr.write(driver.findElement(By.cssSelector("td.login-error-message")).getText());
	  lr.newLine();
	  }
  }
  else
   {
	System.out.println("Application URL Invalid");
	lr.write("Application URL Invalid");
	lr.newLine();
   } 
	lr.flush();
	ur.flush();
	urw.flush();
	sr.close();
	ur.close();
	urw.close();
	rw.close();
	rr.close();
	lr.close();
	FR.close();
	BR.close();
   	
   	if(ufile.length()>0)
   	{	
      FileWriter fw1 = new FileWriter("log.txt",true);
      BufferedWriter bw1 = new BufferedWriter(fw1);
      
      FileWriter rw1= new FileWriter("unsuccessful.txt",true);
      BufferedWriter urw1 = new BufferedWriter(rw1);
      
      String rfile="temp.txt";
  	  FileReader RR = new FileReader(rfile);
  	  BufferedReader BRR = new BufferedReader(RR);
  	while((line = BRR.readLine())!= null)
    {
 	  try{
     line=line.trim();
    outerloop:
 	for(int l=1;l<=2;l++)
 	{
 	 if(line.length()<1)
 	  {
 	 bw1.write("INFO:  "+"File is empty and ....");
 	 bw1.newLine();
 	  break;
 	  }
 	    driver.findElement(By.name("keyword")).clear();
		driver.findElement(By.name("keyword")).sendKeys(line.trim());
		driver.findElement(By.name("btnSearch")).click();
		List<WebElement> rowCount=driver.findElements(By.xpath("//table[@class='table_results']/tbody/tr"));
		int s=rowCount.size();
		if(rowCount.size()==0)
		 {
		 bw1.write("INFO:  "+line+" --- No record found");
		 bw1.newLine();
		 break;
		 }
		String first_part = "//table[@class='table_results']/tbody/tr[";
		String second_part = "]/td[";
		String third_part = "]";
		 for (int i=1;i<rowCount.size();)
		  {
		  i=i+1;
		  String final_xpath = first_part+i+second_part+6+third_part;
		  String Table_data = driver.findElement(By.xpath(final_xpath)).getText();
		  String state=driver.findElement(By.xpath(first_part+i+second_part+2+third_part)).getText();
	      String Task_Type=driver.findElement(By.xpath(first_part+i+second_part+7+third_part)).getText();
	      String link=driver.findElement(By.xpath(first_part+i+second_part+1+third_part+"/a[2]")).getText();
		  if(Table_data.contentEquals(line.trim()))
		   {
		   if(Table_data.contentEquals(line.trim()) && state.contentEquals("Open"))
		   	{
		    driver.findElement(By.xpath(first_part+i+second_part+1+third_part+"/a[2]")).click();
		    WebDriverWait wait = new WebDriverWait(driver, 15);
		    wait.until(ExpectedConditions.presenceOfElementLocated(By.name("moreComments")));      	  
		    WebElement selectElement = driver.findElement(By.name("wfPath"));
		    int t=0;
		    if (selection.contentEquals(" Approve"))
      	    {
		    	t=1;
              driver.findElement(By.name("moreComments")).sendKeys(comment); 	
              
            }
		    if (selection.contains("Return"))
      	  {
        driver.findElement(By.name("moreComments")).sendKeys(comment);
        
      	  }
		    Select select = new Select(selectElement);
		    List<WebElement> allOptions = select.getOptions();    	  
		    int r=0;
		    if(allOptions.size()>0)
		     {
		     for (WebElement option : allOptions)
		      {
		      if(option.getText().contains(selection) || option.getText().startsWith(selection.trim()))
	            {	  
		        r=1;
	        	break;
	            }
		      }
		     }	  
		     else
		      {
		       bw1.write("WARNING:  "+line+" --- check with right approver");
		       bw1.newLine();
		       urw1.write(line);
			   urw1.newLine();
		       driver.findElement(By.name("btnCancel")).click();
		       break outerloop;
		      }
			  if (r==1)
     	    {
		        Select sel = new Select(driver.findElement(By.name("wfPath")));
		        sel.selectByVisibleText(selection.trim());
		        Thread.sleep(2000);
		        
		        driver.findElement(By.name("btnSelect")).click();
     		}
	          else
	        	{
	        	bw1.write("WARNING:  "+line+" --- is in preapproval/Wrong Slection");
	        	bw1.newLine();
	        	urw1.write(line);
	        	urw1.newLine();
	            driver.findElement(By.name("btnCancel")).click();
				break outerloop;
	        	}
		       Thread.sleep(2000);
			   boolean isPresent= driver.findElements(By.xpath("//form/span[1]")).size()!=0;
			     if(isPresent==true)
				  {
				    String ms=driver.findElement(By.xpath("//form/span[1]")).getText();
					if(ms.contentEquals("Release cannot be Approved until the Product has been Approved along with the Release that created the Product."))
					  {
					   urw1.write(line);
					   urw1.newLine();
					   bw1.write("WARNING:  "+line+" --- "+ms);
					   bw1.newLine();
					   driver.findElement(By.name("btnCancel")).click();
				       break outerloop;
					  }
					   bw1.write("WARNING:  "+line+" --- "+ms);
					   bw1.newLine();
					   urw1.write(line);
					   urw1.newLine();
					   driver.findElement(By.name("btnCancel")).click();
					   break outerloop;
					  }
			     if(t==1)
		     	  {
			      lr.write("SUCCESS:  "+Table_data+" ----  "+Task_Type+" is successfully  "+selection+"d");
			      lr.newLine();
			      break;
		     	  }
		     	  else{
		     		 lr.write("SUCCESS:  "+Table_data+" ----  "+Task_Type+" is successfully  "+selection);
				     lr.newLine();
				     break;
		     	  }
		        }
		        else
				 {
		          bw1.write("WARNING:  "+Table_data+" ----  "+Task_Type+" is  "+state+" state");
		          bw1.newLine();
		          urw1.write(line);
				  urw1.newLine();
				 }
		     }
		     else
		      {
		       if(i==rowCount.size())
		       {
		        bw1.write("INFO:  "+line+" --- No records found");
		        bw1.newLine();
		       }
		      }
		  }
		}
 	  }
 	  catch(Exception e)
	       {
	        bw1.write("EXCEPTION:  "+e.toString());
	        bw1.newLine();
	        driver.findElement(By.linkText("Home")).click();
	        }
	 }
  	
  	bw1.flush();
	urw1.flush();
	rw1.close();
	urw1.close();
	fw1.close();
	bw1.close();
	RR.close();
	BRR.close();
   	}
   	
   	
driver.close();
ufile.delete();
}
		
}
