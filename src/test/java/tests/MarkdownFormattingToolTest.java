package tests;

import io.qameta.allure.*;
import net.datafaker.Faker;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("Инструменты")
@Feature("Инструменты на искусственном интеллекте")
@Owner("Denezhko Maksim Aleksandrovich maxdenezhko@gmail.com")
public class MarkdownFormattingToolTest extends BaseTest {

    final Faker faker = new Faker();

    String longReadableText = String.join("\n\n", faker.lorem().paragraphs(200));
    String maxSymbolsToInput = longReadableText.substring(0, 4000);
    String excessiveSymbolsToInput = longReadableText.substring(0, 4001);
    String lowSymbolsToInput = longReadableText.substring(0, 499);

    @Story("Инструмент \"Отформатировать текст\"")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("#gid=0&range=C40")
    @Test(description = "Успешное форматирование текста - 4000 знаков", priority = 1, enabled = true)
    public void maxSymbolsFormatting() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseMarkdownTool();
        markdownFormattingToolPage.checkTitle();

        int currentMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();
        int initialLimitsAmount = mainPage.checkLimitsAmount();

        markdownFormattingToolPage.inputText(maxSymbolsToInput);
        markdownFormattingToolPage.checkSymbolsCountTextExist("Символов: 4000 из 4000");

        int operationLimitsCost = markdownFormattingToolPage.checkLimitsCostOnFormatting();

        markdownFormattingToolPage.submitButtonDisableCheck(false);
        markdownFormattingToolPage.submitText();
        markdownFormattingToolPage.checkMarkdownOutputFieldFilled();

        int updatedMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();
        int finalLimitsAmount = mainPage.checkLimitsAmount();


        Allure.step("Проверяем, что в истории форматирования количество записей увеличилось на единицу", () -> {
            assertEquals(updatedMarkdownHistorySize, currentMarkdownHistorySize + 1,
                    "Количество записей в истории форматирования не увеличилось на единицу");
        });

        Allure.step("Проверяем, что правильно списывается стоимость операции в лимитах", () -> {
            assertEquals(finalLimitsAmount, initialLimitsAmount-operationLimitsCost,
                    "Неправильный остаток лимитов после списания стоимости операции");
        });
    }

    @Story("Инструмент \"Отформатировать текст\"")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("#gid=0&range=C37")
    @Test(description = "Неуспешное форматирование текста - 4001 знаков", priority = 2, enabled = false)
    public void excessiveSymbolsFormatting() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseMarkdownTool();
        markdownFormattingToolPage.checkTitle();
        markdownFormattingToolPage.inputText(excessiveSymbolsToInput);
        markdownFormattingToolPage.checkErrorTextExist("Превышен лимит символов: 4001 из 4000");
        markdownFormattingToolPage.submitButtonDisableCheck(true);
    }

    @Story("Инструмент \"Отформатировать текст\"")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("#gid=0&range=C49")
    @TmsLink("#gid=0&range=C44")
    @Test(description = "Успешное форматирование текста - 6000 знаков с включенной опцией \"Длинный текст\"",
            priority = 1, enabled = false)
    public void longTextFormatting() {

        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseMarkdownTool();
        markdownFormattingToolPage.checkTitle();

        int currentMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();
        int initialLimitsAmount = mainPage.checkLimitsAmount();

        markdownFormattingToolPage.switchAiModelByIndex(3);
        markdownFormattingToolPage.createDocxWithTextAndInput(6000);
        markdownFormattingToolPage.checkErrorTextExist("Превышен лимит символов: 6000 из 4000");
        markdownFormattingToolPage.submitButtonDisableCheck(true);
        markdownFormattingToolPage.switchLongTextSwitcher();
        markdownFormattingToolPage.checkSymbolsCountTextExist("Символов: 6000 из 50000");

        int operationLimitsCost = markdownFormattingToolPage.checkLimitsCostOnFormatting();

        markdownFormattingToolPage.submitButtonDisableCheck(false);
        markdownFormattingToolPage.submitText();
        markdownFormattingToolPage.checkMarkdownOutputFieldFilled();

        int updatedMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();
        int finalLimitsAmount = mainPage.checkLimitsAmount();

        Allure.step("Проверяем, что в истории форматирования количество записей увеличилось на единицу", () -> {
            assertEquals(updatedMarkdownHistorySize, currentMarkdownHistorySize + 1,
                    "Количество записей в истории форматирования не увеличилось на единицу");
        });

        Allure.step("Проверяем, что правильно списывается стоимость операции в лимитах", () -> {
            assertEquals(finalLimitsAmount, initialLimitsAmount-operationLimitsCost,
                    "Неправильный остаток лимитов после списания стоимости операции");
        });
    }

    @Story("Инструмент \"Отформатировать текст\"")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("#gid=0&range=C53")
    @Test(description = "Неуспешное форматирование текста в отсутствии лимитов", priority = 1, enabled = true)
    public void noLimitsSymbolsFormatting() {
        mainPage.openPage();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseMarkdownTool();
        markdownFormattingToolPage.checkTitle();
        markdownFormattingToolPage.switchAiModelByIndex(2);

        while (mainPage.checkLimitsAmountSilent() > 1) {
            markdownFormattingToolPage.inputText(lowSymbolsToInput);
            markdownFormattingToolPage.submitText();
        }

        markdownFormattingToolPage.inputText(lowSymbolsToInput);
        markdownFormattingToolPage.checkSymbolsCountTextExist("Символов: 499 из 500");
        markdownFormattingToolPage.submitButtonDisableCheck(false);
        markdownFormattingToolPage.submitText();
        markdownFormattingToolPage.checkNoLimitsMessageExist();
    }

    @Story("Инструмент \"Отформатировать текст\"")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("#gid=0&range=C56")
    @Test(description = "Удаление проверки из истории\n", priority = 1, dependsOnMethods =
            {"maxSymbolsFormatting"}, enabled = true)
    public void markdownHistoryOptionDelete() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseMarkdownTool();
        markdownFormattingToolPage.checkTitle();

        int startMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();

        markdownFormattingToolPage.firstMarkdownHistoryOptionDelete();

        int afterMarkdownHistorySize = markdownFormattingToolPage.checkMarkdownHistorySize();

        Allure.step("Проверяем, что в истории форматирования количество записей уменьшилось на единицу", () -> {
            assertEquals(afterMarkdownHistorySize, startMarkdownHistorySize - 1,
                    "Количество записей в истории форматирования не уменьшилось на единицу");
        });
    }

}
