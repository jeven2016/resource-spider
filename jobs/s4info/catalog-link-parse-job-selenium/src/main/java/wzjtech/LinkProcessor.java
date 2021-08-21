package wzjtech;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class LinkProcessor<IN, OUT> {
    @Getter
    private IN data;

    @Getter
    private WebDriver driver;

    public LinkProcessor(IN data, WebDriverManager driverManager) {
        this.driver = driverManager.create();
        this.data = data;
    }

    public OUT start() {
        login();
        return analyse(data, driver);
    }

    protected void login() {
        //检测是否已经登录


        //没有登录，跳转登录页面
        new WebDriverWait(driver, 10)
                .until(d -> d.findElement(By.cssSelector("a[href='index.php']")) != null);

        var loginLink = driver.findElement(By.cssSelector("a[href='logging.php?action=login']"));
        if (loginLink != null) {
            loginLink.click();

            //等待加载完成
            new WebDriverWait(driver, 10)
                    .until(d -> d.findElement(By.cssSelector("input[name='username']")) != null);

            //输入用户名&密码
            var username = driver.findElement(By.cssSelector("input[name='username']"));
            username.sendKeys("nj2015");

            var pwd = driver.findElement(By.cssSelector("input[name='password']"));
            pwd.sendKeys("562405");

            //登录
            driver.findElement(By.cssSelector("input[name=loginsubmit]")).click();

            //验证登录成功
            new WebDriverWait(driver, 10)
                    .until(d -> d.findElement(By.cssSelector("a[href='logging.php?action=logout']")) != null);

        }
    }
/*
    private void analysePage(WebDriver driver) {
        //检查是否存在'論壇主題'，如果是则是首页，此时只解析之后出现的列表
        var subjectLink = driver.findElement(By.xpath("//span[text()='論壇主題']"));
        if (subjectLink != null) {

        }

        // 只对第一页生效
        // 1. 先查找文字是‘主題’的链接
        //2. 再递归返回到父节点class是'maintable'的div
        //3. 再找后续兄弟节点中有相同class的div
        //4. 递归找到下面的文章链接然后返回不包含聊天分页的主题列表
        var articles = driver.findElements(By
                        .xpath("//span[text()='論壇主題']/ancestor::div[@class='maintable']/following-sibling::div[@class='maintable']/descendant::a[contains(@href, 'viewthread.php')]"))
                .stream().filter(link -> !link.getAttribute("href").contains("&page="))
                .collect(Collectors.toList());
//        artList = articles;
    }*/

    protected abstract OUT analyse(IN data, WebDriver driver);
}
