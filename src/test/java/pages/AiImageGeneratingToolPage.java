package pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


public class AiImageGeneratingToolPage {
    final SelenideElement titleElement = $x("//h1[text()='Создать изображение']");
    final SelenideElement manualAiInstrumentSelectionButton = $x("//span[text()='Ручной выбор модели']/..");
    final SelenideElement fewAiModelsSwitcher = $x("//button[@role='switch']");
    final SelenideElement aiSelectionList = $x("//div[@class='lgt-select-content']");
    final SelenideElement virtualListContainer = $(".rc-virtual-list-holder");
    final ElementsCollection aiSelectionListCurrentVisibleOptions = $$x("//div[@class='lgt-select" +
            "-item-option-content']/parent::div").filterBy(com.codeborne.selenide.Condition.visible);
    final SelenideElement aiDeleteIcon = $x("//span[@class='lgt-select-selection-item-remove']");
    final SelenideElement aiSelectionFieldWithError = $x("//div[contains(@class, " +
            "'lgt-select-status-error')]");
    final SelenideElement inputImageDescriptionField = $x("//div[@role='textbox']");
    final ElementsCollection qualityOptionByID = $$x("//label[text()='Выберите качество']/../" +
            "following-sibling::div/descendant::label");
    final SelenideElement generateImageButton = $x("//span[contains(text(), 'Сгенерировать')]" +
            "/parent::button");
    final SelenideElement historyTab = $x("//div[text()='История']");
    final SelenideElement paginationInHistoryForwardButton = $x("//span[@aria-label='right']" +
            "/parent::button[@class='lgt-pagination-item-link']");
    final SelenideElement paginationInHistoryBackwardButton = $x("//span[@aria-label='left']" +
            "/parent::button[@class='lgt-pagination-item-link']");
    final ElementsCollection aiGeneratedImagesHistoryOptions = $$x("//div[@role='tablist']/" +
            "following-sibling::div/div/div/div/div");
    final ElementsCollection activeGenerationCanvases = $$x("//canvas");
    final ElementsCollection easyModeModelOptions = $$x("//span[text()='Модель']/../" +
            "following-sibling::div/button");
    final SelenideElement imageUploadButton = $x("//label[text()='Фото']/../following-sibling::div" +
            "//descendant::input");


    @Step("Проверяем переход на страницу инструмента \"Генерация изображений нейросетью\"")
    public void checkTitle() {
        titleElement.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Переключаемся на ручной выбор ИИ модели")
    public void chooseManualAiSelecting() {
        manualAiInstrumentSelectionButton.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    @Step("Выбираем модель генерации для упрощенного режима")
    public void setEasyModeModelOptionById(int EasyModeModelOptionId) {
        if (EasyModeModelOptionId == 0) {
            Allure.step("Устанавливаем \"Эконом\"");
        } else if (EasyModeModelOptionId == 1) {
            Allure.step("Устанавливаем \"Быстрый старт\"");
        } else if (EasyModeModelOptionId == 2) {
            Allure.step("Устанавливаем \"Фото и реализм\"");
        } else if (EasyModeModelOptionId == 3) {
            Allure.step("Устанавливаем \"Максимум качества\"");
        }
        easyModeModelOptions.get(EasyModeModelOptionId).click();
    }

    @Step("Загружаем файл по ссылке")
    public void downloadAndUploadImageFromUrl(String fileUrl, String customFileName) {

        String targetResourcesPath = System.getProperty("user.dir") + "/build/resources/" + customFileName;
        File destinationFile = new File(targetResourcesPath);

        try {
            try (InputStream in = new URL(fileUrl).openStream()) {
                Files.copy(in, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            imageUploadButton.uploadFile(destinationFile);
        } catch (Exception e) {
            throw new RuntimeException("Критическая ошибка: Не удалось скачать файл по ссылке" +
                    " или сохранить", e);
        }
    }

    @Step("Включаем опцию \"Выбрать несколько моделей\"")
    public void severalAiModelsEnabling() {
        fewAiModelsSwitcher.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    @Step("Удаляем текущий выбранный ИИ инструмент")
    public void deleteLastAiModel() {
        aiDeleteIcon.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
        Allure.step("Проверяем, что после удаления нет выбранных ИИ моделей");
        aiSelectionFieldWithError.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    public void deleteLastAiModelSilent() {
        aiDeleteIcon.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
        aiSelectionFieldWithError.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Подключаем все доступные ИИ модели для генерации изображений")
    public int enableAllAiModels() {

        aiSelectionList.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();
        aiSelectionListCurrentVisibleOptions.get(0).shouldBe(com.codeborne.selenide.Condition.visible,
                Duration.ofSeconds(5));

        java.util.Set<String> clickedItems = new java.util.HashSet<>();

        int previousSize = -1;
        int currentScrollTop = 0;
        boolean canScrollMore = true;

        while (canScrollMore) {
            boolean foundUnclickedInCurrentView = true;

            while (foundUnclickedInCurrentView) {
                int currentVisibleCount = aiSelectionListCurrentVisibleOptions.size();
                foundUnclickedInCurrentView = false;

                for (int i = 0; i < currentVisibleCount - 1; i++) {
                    com.codeborne.selenide.SelenideElement option = aiSelectionListCurrentVisibleOptions.get(i);

                    String optionText = option.$x(".//div[contains(@class, 'e1s2rafk4')]").getOwnText().trim();

                    if (optionText.isEmpty()) {
                        optionText = option.$x(".//div[contains(@class, 'e1s2rafk4')]").attr("innerText").trim();
                    }

                    if (optionText.isEmpty()) {
                        optionText = option.attr("innerText").split("\n")[0].trim();
                    }

                    boolean isAlreadySelected = option.has(com.codeborne.selenide.Condition.cssClass("lgt-select" +
                            "-item-option-selected"));

                    if (isAlreadySelected) {
                        clickedItems.add(optionText);
                        continue;
                    }

                    if (!clickedItems.contains(optionText)) {
                        Allure.step("Отмечаем ИИ инструмент: " + optionText);

                        executeJavaScript("arguments[0].click();", option);

                        clickedItems.add(optionText);
                        foundUnclickedInCurrentView = true;
                        break;
                    }
                }
            }

            currentScrollTop += 200;
            executeJavaScript("arguments[0].scrollTop = arguments[1];", virtualListContainer, currentScrollTop);

            com.codeborne.selenide.Selenide.sleep(250);

            long actualScrollTop = executeJavaScript("return arguments[0].scrollTop;", virtualListContainer);

            if (actualScrollTop < currentScrollTop - 50 && clickedItems.size() == previousSize) {
                canScrollMore = false;
            }

            previousSize = clickedItems.size();
        }

        int finalCount = aiSelectionListCurrentVisibleOptions.size();
        if (finalCount > 0) {
            SelenideElement lastOptionOnTheBottom = aiSelectionListCurrentVisibleOptions.get(finalCount - 1);

            String lastOptionText = lastOptionOnTheBottom.$x(".//div[contains(@style, 'display: flex')]")
                    .getText().trim();
            boolean isLastAlreadySelected = lastOptionOnTheBottom.has(com.codeborne.selenide.
                    Condition.cssClass("lgt-select-item-option-selected"));

            if (!clickedItems.contains(lastOptionText) && !isLastAlreadySelected) {
                Allure.step("Отмечаем ИИ инструмент: " + lastOptionText);
                lastOptionOnTheBottom.scrollIntoView(false).click();
                clickedItems.add(lastOptionText);
            }
        }

        Allure.step("Всего отмечено уникальных ИИ инструментов: " + clickedItems.size());

        aiSelectionList.should(exist).shouldBe(visible, Duration.ofSeconds(10)).click();

        return clickedItems.size();
    }

    @Step("Проверяем стоимость генерации изображений в лимитах")
    public int checkLimitsCostOnImageGenerating() {
        String rawText = generateImageButton.getText();

        Pattern pattern = Pattern.compile("Сгенерировать ·\\s*(\\d+)");
        Matcher matcher = pattern.matcher(rawText);

        int limitCost = 0;

        if (matcher.find()) {
            String digitsAfterPhrase = matcher.group(1); // Достает то, что попало в скобки (\\d+)
            limitCost = Integer.parseInt(digitsAfterPhrase);
        } else {
            throw new RuntimeException("Не удаётся определить стоимость генерации изображений в лимитах");
        }

        Allure.step("Стоимость генерации изображений в лимитах - " + limitCost);
        return limitCost;
    }

    @Step("Проверяем, что рамка выпадающего списка моделей подсвечивается красным")
    public void checkAiModelListHasRedBorder() {
        String[] borderSides = {"border-bottom-color", "border-top-color", "border-left-color", "border-right-color"};

        for (String side : borderSides) {
            aiSelectionFieldWithError.shouldHave(cssValue(side, "rgba(212, 109, 80, 1)"));
        }
    }

    @Step("Выбираем качество генерируемых изображений")
    public void setImageQualityById(int imageQualityId) {
        if (imageQualityId == 0) {
            Allure.step("Устанавливаем низкое качество изображения");
        } else if (imageQualityId == 1) {
            Allure.step("Устанавливаем среднее качество изображения");
        } else if (imageQualityId == 2) {
            Allure.step("Устанавливаем высокое качество изображения");
        }
        qualityOptionByID.get(imageQualityId).click();
    }

    @Step("Вставляем описание генерируемого изображения")
    public void inputImageDescription(String inputText) {
        inputImageDescriptionField.should(exist).shouldBe(visible, Duration.ofSeconds(10)).setValue(inputText);
    }

    @Step("Инициируем генерацию изображений")
    public void generateImage() {
        generateImageButton.shouldBe(visible, Duration.ofSeconds(10)).click();
    }

    @Step("Ожидаем окончания генерации всех изображений нейросетями (до исчезновения лоадеров)")
    public void waitForImageGenerationToComplete() {
        activeGenerationCanvases.shouldHave(CollectionCondition.size(0), Duration.ofMinutes(7));
    }

    public void switchToFirstTabInPaginationSilent() {
        historyTab.shouldBe(visible, Duration.ofSeconds(10)).click();

        while (paginationInHistoryBackwardButton.isDisplayed() &&
                !paginationInHistoryBackwardButton.has(com.codeborne.selenide.Condition.
                        attribute("disabled"))) {

            paginationInHistoryBackwardButton.click();
            com.codeborne.selenide.Selenide.sleep(200);
        }
    }

    @Step("Проверяем текущее количество изображений в истории")
    public int countImagesInHistoryTab() {
        historyTab.shouldBe(visible, Duration.ofSeconds(10)).click();

        int totalImagesAmount = 0;
        boolean hasNextPage = true;
        int pageNumber = 1;

        while (hasNextPage) {
            aiGeneratedImagesHistoryOptions.shouldHave(com.codeborne.selenide.CollectionCondition.
                    sizeGreaterThan(0), Duration.ofSeconds(5));

            int currentPageSize = aiGeneratedImagesHistoryOptions.size();
            totalImagesAmount += currentPageSize;


            if (paginationInHistoryForwardButton.isDisplayed() &&
                    !paginationInHistoryForwardButton.has(com.codeborne.selenide.Condition.
                            attribute("disabled"))) {
                paginationInHistoryForwardButton.click();
                pageNumber++;

                com.codeborne.selenide.Selenide.sleep(300);
            } else {
                hasNextPage = false;
            }
        }

        Allure.step("Текущее количество изображений в истории " + totalImagesAmount);

        while (paginationInHistoryBackwardButton.isDisplayed() &&
                !paginationInHistoryBackwardButton.has(com.codeborne.selenide.Condition.
                        attribute("disabled"))) {

            paginationInHistoryBackwardButton.click();
            com.codeborne.selenide.Selenide.sleep(200);
        }

        return totalImagesAmount;
    }
}
