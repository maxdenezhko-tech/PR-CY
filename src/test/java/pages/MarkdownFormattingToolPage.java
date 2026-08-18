package pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class MarkdownFormattingToolPage {

    final Faker faker = new Faker();

    final SelenideElement titleElement = $x("//h1[text()='Отформатировать текст']");
    final SelenideElement inputField = $x("//textarea[@placeholder='Введите текст...']");
    final SelenideElement submitButton = $x("//span[text()='Отправить']//parent::button");
    final SelenideElement extendedSymbolsErrorText = $x("//div[starts-with(text(), 'Превышен лимит " +
            "символов:')]");
    final SelenideElement symbolsCountInfoText = $x("//div[starts-with(text(), 'Символов:')]");
    final SelenideElement markdownOutputField = $x("//div[@class='markdown']");
    final SelenideElement longTextSwitcher = $x("//div[text()='Длинный текст']/../../" +
            "preceding-sibling::*/button");
    final SelenideElement aiList = $x(" //input[@type='search']");
    final ElementsCollection aiListOptions = $$x("//div[@role='listbox']/child::*[@role='option']");
    final SelenideElement loadDocumentButton = $x("//span[text()='Загрузить файл']/../../child::input");
    final SelenideElement markdownHistoryExtendButton = $x("//button[text()='Загрузить больше']");
    final ElementsCollection markdownHistoryOptions = $$x("//div[text()='История']/../div/child::div");
    final SelenideElement markdownHistoryFirstOption = markdownHistoryOptions.get(0).$x(".//descendant::button");
    final SelenideElement markdownHistroyDeleteElementConfirmationButton = $x("//span[text()='Да']/..");
    //  final SelenideElement limitsAmount = $x("//header/div/div[4]/div[2]/div[1]/div/descendant::span");
    final SelenideElement noLimitsMessage = $x("//h3[text()='Кристаллы закончились']");

    @Step("Проверяем переход на страницу инструмента \"Отформатировать текст\"")
    public void checkTitle() {
        titleElement.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Вводим заданное количество символов")
    public void inputText(String text) {
        int characterCount = text.length();
        Allure.step("Введено символов - " + characterCount);
        inputField.should(exist).shouldBe(visible, Duration.ofSeconds(10)).setValue(text);
    }

    @Step("Отправляем текст на форматирование")
    public void submitText() {
        submitButton.click();
    }

    @Step("Проверяем наличие текста с информацией о количестве введённых символов")
    public void checkSymbolsCountTextExist(String infoSymbolCountText) {
        Allure.step("Информационный текст присутствует и совпадает с ожидаемым");
        symbolsCountInfoText.should(exist).shouldBe(visible, Duration.ofSeconds(10));
        Allure.step("Цвет информационного текста - серый");
        symbolsCountInfoText.shouldHave(cssValue("color", "rgba(161, 167, 173, 1)"));
        symbolsCountInfoText.shouldHave(Condition.matchText("^" + infoSymbolCountText));
    }

    @Step("Проверяем наличие текста с ошибкой о превышении допустимого количества символов")
    public void checkErrorTextExist(String errorText) {
        Allure.step("Ошибка присутствует. Текст ошибки совпадает с ожидаемым");
        extendedSymbolsErrorText.should(exist).shouldBe(visible, Duration.ofSeconds(10));
        Allure.step("Цвет текста ошибки - красный");
        extendedSymbolsErrorText.shouldHave(cssValue("color", "rgba(236, 44, 64, 1)"));
        extendedSymbolsErrorText.shouldHave(text(errorText));
    }

    @Step("Проверяем актиность кнопки \"Отправить\"")
    public void submitButtonDisableCheck(boolean shouldBeDisabled) {
        if (shouldBeDisabled) {
            Allure.step("Ожидаем результат: кнопка неактивна");
            submitButton.shouldHave(attribute("disabled"));
        } else {
            Allure.step("Ожидаем результат: кнопка активна");
            submitButton.shouldNotHave(attribute("disabled"));
        }
    }

    @Step("Проверяем, что в выходном поле отображаются отформатированные абзацы текста")
    public void checkMarkdownOutputFieldFilled() {
        markdownOutputField.should(exist);
        markdownOutputField.$$("p").shouldHave(CollectionCondition.sizeGreaterThan(0));
    }

    @Step("Проверяем стоимость операции в лимитах")
    public int checkLimitsCostOnFormatting() {
        String rawText = symbolsCountInfoText.getText();

        Pattern pattern = Pattern.compile("требуется лимитов:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(rawText);

        int limitCost = 0;

        if (matcher.find()) {
            String digitsAfterPhrase = matcher.group(1); // Достает то, что попало в скобки (\\d+)
            limitCost = Integer.parseInt(digitsAfterPhrase);
        } else {
            throw new RuntimeException("Не удаётся определить стоимость форматирования в лимитах");
        }

        Allure.step("Стоимость операции форматирования в лимитах - " + limitCost);
        return limitCost;
    }

    @Step("Проверяем, что появилось окно с текстом \"Кристаллы закончились\"")
    public void checkNoLimitsMessageExist() {
        noLimitsMessage.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Проверяем количество проверок в истории")
    public int checkMarkdownHistorySize() {

        while (markdownHistoryExtendButton.isDisplayed()) {
            markdownHistoryExtendButton.click();
        }

        try {
            com.codeborne.selenide.Selenide.sleep(500); // Даем сайту полсекунды на отрисовку
        } catch (Exception ignored) {
        }

        int markdownOptionsSize = markdownHistoryOptions.size();

        Allure.step("Текущее количество позиций в истории проверок - " + markdownOptionsSize);
        return markdownOptionsSize;
    }

    @Step("Удаляем первую опцию из списка истории форматирования")
    public void firstMarkdownHistoryOptionDelete() {
        markdownHistoryFirstOption.click();
        markdownHistroyDeleteElementConfirmationButton.should(exist).shouldBe(visible,
                Duration.ofSeconds(10)).click();
    }

    @Step("Нажимаем переключатель \"Длинный текст\"")
    public void switchLongTextSwitcher() {
        longTextSwitcher.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    @Step("Переключаем ИИ модель")
    public void switchAiModelByIndex(int aiOptionIndex) {
        aiList.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
        aiListOptions.get(aiOptionIndex).click();

        String selectedModelName = aiListOptions.get(aiOptionIndex).getText();

        Allure.step("Выбрана ИИ модель " + selectedModelName);
    }

    @Step("Создаём docx документ объемом {targetLength} символов с абзацами и загружаем в ИИ инструмент")
    public void createDocxWithTextAndInput(int targetLength) {
        Allure.step("Генерируем структурированный текст объемом " + targetLength + " символов");

        String resourcesPath = System.getProperty("user.dir") + "/build/resources/text_to_format.docx";

        File docxFile = new File(resourcesPath);

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(docxFile)) {

            int currentLength = 0;

            while (currentLength < targetLength) {
                String paragraphText = faker.lorem().paragraph();
                if (paragraphText == null || paragraphText.isEmpty()) {
                    paragraphText = "Lorem ipsum dolor sit amet " + java.util.UUID.randomUUID();
                }

                if (currentLength + paragraphText.length() <= targetLength) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(paragraphText);

                    currentLength += paragraphText.length();
                } else {
                    int remainingSymbols = targetLength - currentLength;
                    String finalCutParagraph = paragraphText.substring(0, remainingSymbols);

                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(finalCutParagraph);

                    currentLength += finalCutParagraph.length();
                }
            }

            document.write(out);
            System.out.println("=== Документ DOCX успешно сгенерирован! Чистый размер текста: " + currentLength);

        } catch (IOException e) {
            throw new RuntimeException("Не удалось сгенерировать Word-документ через Apache POI", e);
        }

        Allure.step("Передаем созданный файл \"text_to_format.docx\" в форму загрузки сайта");
        loadDocumentButton.uploadFile(docxFile);
    }
}
