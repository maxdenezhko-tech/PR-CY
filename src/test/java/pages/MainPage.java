package pages;

import DB.DataBaseConnection;
import DB.SqlQuery;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {
    SqlQuery sqlQuery;

    final SelenideElement loginButton = $x("//span[text()='Вход']");
    final SelenideElement loginByEmailButton = $(byLinkText("Вход по паролю"));
    final SelenideElement loginByEmailAndPasswordButton = $x("//i[@class='fa fa-key']/..");
    final SelenideElement emailInputField = $(By.id("email"));
    final SelenideElement passwordInputField = $(By.id("password"));
    final SelenideElement submitButton = $x("//button[text()='Войти']");
    final SelenideElement mainButton = $(byLinkText("Главная"));
    final SelenideElement accountIcon = $x("//header/descendant::*[text()='URfever']");
    final ElementsCollection banners = $$x("//h3[text()='Обновления сервиса']/../child::div/child::div/" +
            "child::button");
    final SelenideElement toolsLink = $(byLinkText("Популярные инструменты"));
    final SelenideElement limitsAmount = $x("//header/div/div[4]/div[2]/div[1]/div/descendant::span");


    public MainPage openPage() {
        open("");
        return this;
    }

    public MainPage login() {
        sqlQuery = new SqlQuery();

        String getEmailFromDB = "";
        String getPasswordFromDB = "";

        System.out.println("=== НАЧАЛО: Подключение к базе данных Neon... ===");
        String query = sqlQuery.getLoginCreditsFromDB(1);

        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            if (rs.next()) {
                getEmailFromDB = rs.getString("login");
                getPasswordFromDB = rs.getString("password");
                System.out.println("=== Данные успешно получены из БД! ===");
            } else {
                throw new RuntimeException("Критическая ошибка: В базе данных не найдены учетные данные!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить данные для логина из базы данных Neon", e);
        }

        performLoginSteps(getEmailFromDB, getPasswordFromDB);

        return this;
    }

    @Step("Логинимся под кредами пользователя: email = {email}, пароль = ******")
    private void performLoginSteps(String email, String password) {
        loginButton.click();
        loginByEmailButton.click();
        loginByEmailAndPasswordButton.click();

        emailInputField.setValue(email);
        passwordInputField.setValue(password);

        submitButton.shouldBe(Condition.visible).submit();
    }

    @Step("Проверяем факт возврата на главную страницу после успешной авторизации")
    public void checkAuthorization() {
        mainButton.should(exist).shouldBe(visible, Duration.ofSeconds(10));
        accountIcon.should(exist).shouldBe(visible, Duration.ofSeconds(10));
        banners.shouldHave(size(10));
        banners.findBy(text("Векторизация"))
                .shouldBe(exist)
                .shouldHave(cssValue("width", "236px"))
                .shouldHave(cssValue("height", "118px"));
    }

    @Step("Выполняем переход к инструментам по ссылке на главной странице")
    public void toolsPageRedirection() {
        toolsLink.should(exist).click();
    }

    @Step("Проверяем количество лимитов на аккаунте")
    public int checkLimitsAmount() {
        String rawText = limitsAmount.getText();
        String cleanDigits = rawText.replaceAll("[^0-9]", "");
        Allure.step("Текущее количество лимитов на аккаунте - " + cleanDigits);
        return Integer.parseInt(cleanDigits);
    }

    public int checkLimitsAmountSilent() {
        String rawText = limitsAmount.getText();
        String cleanDigits = rawText.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleanDigits);
    }
}
