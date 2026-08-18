package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import pages.AiImageGeneratingToolPage;
import pages.MainPage;
import pages.MarkdownFormattingToolPage;
import pages.ToolsPage;

import java.sql.Connection;
import java.sql.DriverManager;

@Listeners({AllureTestNg.class})
public class BaseTest {
    MainPage mainPage;
    ToolsPage toolsPage;
    MarkdownFormattingToolPage markdownFormattingToolPage;
    AiImageGeneratingToolPage aiImageGeneratingToolPage;

    @BeforeMethod
    public void setUP() {
        Configuration.browser = "chrome";
        Configuration.timeout = 60000;
        Configuration.headless = false;
        Configuration.browserSize = "maximized";
        Configuration.baseUrl = "https://pr-cy.ru/";
        Configuration.holdBrowserOpen = true;

        Configuration.pageLoadTimeout = 60000;

        mainPage = new MainPage();
        toolsPage = new ToolsPage();
        markdownFormattingToolPage = new MarkdownFormattingToolPage();
        aiImageGeneratingToolPage = new AiImageGeneratingToolPage();

    }

    @BeforeSuite(alwaysRun = true)
    public static void wakeUpNeon() {
        String url = "jdbc:postgresql://ep-fragrant-wind-ax2ayd7p.c-4.us-east-2.aws.neon.tech:5432/neondb?sslmode=require";
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            try (Connection conn = DriverManager.getConnection(url, "neondb_owner", "npg_rLgYtB79qCQf")) {
                conn.createStatement().execute("SELECT 1;");

                Reporter.log("[NEON_LOG] Neon DB успешно проснулась и готова к тестам!", true);
                return;
            } catch (Exception e) {
                Reporter.log("[NEON_LOG] Neon спит, ожидаем пробуждения (попытка " + (i + 1) + ")...", true);
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void close() {
        WebDriverRunner.clearBrowserCache();
        Selenide.closeWebDriver();
    }
}
