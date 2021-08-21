package wzjtech;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 首页解析，找到所有的分页URL
 */
public class HomePageProcessor extends LinkProcessor<Entities.Catalog, Entities.Catalog> {


    public HomePageProcessor(Entities.Catalog data, WebDriverManager driverManager) {
        super(data, driverManager);
    }

    @Override
    public Entities.Catalog analyse(Entities.Catalog data, WebDriver driver) {
        var pageInfoTd = getDriver().findElement(By.cssSelector("table.tableborder tr.smalltxt>td:nth-child(2)"));
        var text = pageInfoTd.getText();
        var pageCount = Integer.parseInt(text.split("/")[1].trim());

        data.setPageCount(pageCount);
        data.generateSubPageUrls((pageNumber) -> data.getUrl() + "&page=" + pageNumber);
        return data;
    }
}
