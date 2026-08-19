package tests;

import io.qameta.allure.*;
import org.testng.annotations.Test;

@Epic("Базовые проверки")
@Feature("Авторизация")
@Owner("Denezhko Maksim Aleksandrovich maxdenezhko@gmail.com")
public class LoginTest extends BaseTest {
    @Story("Ввод персональных данных")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("TMS-link")
    @Issue("PRCY-24")
    @Test(description = "Проверка корректной авторизации", priority = 1, enabled = false)
    public void projectisOpen() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
    }
}
    