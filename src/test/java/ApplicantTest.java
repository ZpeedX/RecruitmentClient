
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import model.AvailabilityDTO;
import model.LanguageChange;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Evan
 */
public class ApplicantTest extends CommonMethods {

    @Test
    public void testParticipantLinks(WebDriver driver) throws Exception {
        driver.get(new URI(driver.getCurrentUrl()).resolve("apply.xhtml").toString());
        waitUntil(driver, By.id("applyForm"));

        driver.get(new URI(driver.getCurrentUrl()).resolve("index.xhtml").toString());
        waitUntil(driver, By.className("index-content"));

        driver.get(new URI(driver.getCurrentUrl()).resolve("applications.xhtml").toString());
        driver.findElement(By.xpath("//h1[contains(.,'403')]"));

        driver.get(new URI(driver.getCurrentUrl()).resolve("application_overview.xhtml").toString());
        driver.findElement(By.xpath("//h1[contains(.,'403')]"));
    }

    @Test
    public String testApplyPage(WebDriver driver,
            String avFrom,
            String avTo,
            String submissionDate) throws Exception {
        driver.get(new URI(driver.getCurrentUrl()).resolve("apply.xhtml").toString());

        addCompetence(driver, true, true);
        int count_table1 = countRowsIntable(driver, "applyForm:competenceTable");
        addCompetence(driver, false, true);
        int count_table2 = countRowsIntable(driver, "applyForm:competenceTable");
        Assert.assertEquals(count_table2, count_table1, "Adding nothing to table failed");
        addCompetence(driver, true, false);
        waitUntil(driver, By.id("applyForm:apply_yof_missing"));
        int count_table3 = countRowsIntable(driver, "applyForm:competenceTable");
        Assert.assertEquals(count_table3, count_table1, "Adding nothing with error to table failed");
        addCompetence(driver, true, true);
        int count_table4 = countRowsIntable(driver, "applyForm:competenceTable");
        Assert.assertEquals(count_table4, ++count_table1, "2.Adding something to table failed");

        addAvailablity(driver, "", "", By.id("applyForm:dateFromErrMsg"));
        addAvailablity(driver, "03/15/18", "", By.id("applyForm:dateToErrMsg"));
        addAvailablity(driver, "12-3", "03/15/18", By.id("applyForm:dateFromErrMsg"));
        addAvailablity(driver, "03/15/18", "illegal", By.id("applyForm:dateToErrMsg"));

        addAvailablity(driver, "03/15/18", "03/15/18", By.id("applyForm:availabilityTable"));
        addAvailablity(driver, "03/15/18", "03/15/19", By.id("applyForm:availabilityTable"));
        addAvailablity(driver, avFrom, avTo, By.id("applyForm:availabilityTable"));
        int count_tableAv = countRowsIntable(driver, "applyForm:availabilityTable");
        Assert.assertEquals(count_tableAv, 4);

        WebElement appSubmitBtn = driver.findElement(By.id("applyForm:submitAppBtn"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView();", appSubmitBtn);
        appSubmitBtn.click();
        String date = parseDateAfterLocale(new Date());
        
        waitUntil(driver, By.xpath("//span[@data-notify='message']"));
        return date;

    }

    public void addCompetence(WebDriver driver, boolean selectComp, boolean selectYOE) {
        Random rand = new Random();
        Select selectorComp = new Select(driver.findElement(By.id("applyForm:t1")));
        Select selectorYOE = new Select(driver.findElement(By.id("applyForm:t2")));
        selectorComp.selectByIndex(0);
        selectorYOE.selectByIndex(0);
        if (selectComp) {
            int optionIndex = rand.nextInt(selectorComp.getOptions().size() - 1);
            selectorComp.selectByIndex(optionIndex + 1);

        }
        if (selectYOE) {
            int optionIndex = rand.nextInt(selectorYOE.getOptions().size());
            selectorYOE.selectByIndex(optionIndex + 1);
        }

        driver.findElement(By.id("applyForm:addCompBtn")).click();
        waitUntil(driver, By.id("applyForm:addCompBtn"));
    }

    public void addAvailablity(WebDriver driver, String dateFrom, String dateTo, By by) {
        WebElement dateFromElem = driver.findElement(By.id("applyForm:t3"));
        WebElement dateToElem = driver.findElement(By.id("applyForm:t4"));
        dateFromElem.clear();
        dateFromElem.sendKeys(dateFrom);
        dateToElem.clear();
        dateToElem.sendKeys(dateTo);
        driver.findElement(By.id("applyForm:AddavailabilityButton")).click();
        waitUntil(driver, by);
    }

    private String parseDateAfterLocale(Date date) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
        return df.format(date);
    }
}
