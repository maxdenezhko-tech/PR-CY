package tests;

import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("Инструменты")
@Feature("Инструменты на искусственном интеллекте")
@Owner("Denezhko Maksim Aleksandrovich maxdenezhko@gmail.com")
public class AiImageGeneratingToolTest extends BaseTest {

    @Story("Инструмент \"Генерация изображений нейросетью\"")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("#gid=0&range=C32")
    @Test(description = "Безуспешная генерация изображения в отсутствии выбранной Нейросети", priority = 2,
            enabled = true)
    public void ImageGeneratingWithNoAiModel() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseImageGeneratorTool();
        aiImageGeneratingToolPage.checkTitle();

        int initialLimitsAmount = mainPage.checkLimitsAmount();
        int initialImagesAmountInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        aiImageGeneratingToolPage.inputImageDescription("Generate an abstract, visually engaging image with " +
                "no specific subject or theme—let the composition, color, and form create interest through their" +
                " interplay alone.");
        aiImageGeneratingToolPage.chooseManualAiSelecting();
        aiImageGeneratingToolPage.severalAiModelsEnabling();
        aiImageGeneratingToolPage.deleteLastAiModel();
        aiImageGeneratingToolPage.checkAiModelListHasRedBorder();
        aiImageGeneratingToolPage.setImageQualityById(0);
        aiImageGeneratingToolPage.generateImage();

        int finalLimitsAmount = mainPage.checkLimitsAmount();
        int finalAmountImagesInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        Allure.step("Проверяем, что остаток лимитов не изменился", () -> {
            assertEquals(finalLimitsAmount, initialLimitsAmount,
                    "Изменился остаток лимитов на аккаунте");
        });

        Allure.step("Проверяем, что количество сгенерированных изображений в истории не изменилось", () -> {
            assertEquals(initialImagesAmountInHistory, finalAmountImagesInHistory,
                    "Количество сгенерированных изображений в истории изменилось");
        });
    }

    @Story("Инструмент \"Генерация изображений нейросетью\"")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("#gid=0&range=C27")
    @Test(description = "Успешная генерация изображений при выборе всех моделей ИИ для генерации", priority = 1,
            enabled = true)
    public void ImageGeneratingWithAllAiModelsSelected() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseImageGeneratorTool();
        aiImageGeneratingToolPage.checkTitle();
        aiImageGeneratingToolPage.switchToFirstTabInPaginationSilent();

        int initialLimitsAmount = mainPage.checkLimitsAmount();
        int initialImagesAmountInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        aiImageGeneratingToolPage.inputImageDescription("Generate an abstract, visually engaging image with " +
                "no specific subject or theme—let the composition, color, and form create interest through their" +
                " interplay alone.");
        aiImageGeneratingToolPage.chooseManualAiSelecting();
        aiImageGeneratingToolPage.severalAiModelsEnabling();
        aiImageGeneratingToolPage.deleteLastAiModelSilent();

        int aiModelsAmount = aiImageGeneratingToolPage.enableAllAiModels();

        int operationLimitsCost = aiImageGeneratingToolPage.checkLimitsCostOnImageGenerating();

        aiImageGeneratingToolPage.generateImage();
         aiImageGeneratingToolPage.waitForImageGenerationToComplete();

        int finalLimitsAmount = mainPage.checkLimitsAmount();
        int finalAmountImagesInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        Allure.step("Проверяем, что количество изображений в истории увеличилось на число " +
                "вновь сгенерированных изображений", () -> {
            assertEquals(finalAmountImagesInHistory, initialImagesAmountInHistory + aiModelsAmount,
                    "Количество сгенерированных изображений в истории не увеличилось на " + aiModelsAmount);
        });

        Allure.step("Проверяем, что остаток лимито уменьшился на стоимость генерации", () -> {
            assertEquals(finalLimitsAmount, initialLimitsAmount - operationLimitsCost,
                    "Некорректно подсчитан остаток лимитов после операции");
        });
    }

    @Story("Инструмент \"Генерация изображений нейросетью\"")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("#gid=0&range=C22")
    @Test(description = "Успешная генерация изображений по описанию с максимальным числом загруженных фотографий",
            priority = 1, enabled = true)
    public void ImageGeneratingOnMaxReferencesLoaded() {
        mainPage.openPage()
                .login()
                .checkAuthorization();
        mainPage.toolsPageRedirection();
        toolsPage.checkTitle();
        toolsPage.chooseImageGeneratorTool();
        aiImageGeneratingToolPage.checkTitle();

        int initialLimitsAmount = mainPage.checkLimitsAmount();
        int initialImagesAmountInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        aiImageGeneratingToolPage.inputImageDescription("Сгенерируй красивое инстаграммное блюдо, вдохновленное" +
                " приложенными картинками. Стиль реалистичный. Возможно, антураж какого-то ресторана или обеденного зала," +
                " но фокус именно на блюдо.");
        aiImageGeneratingToolPage.setEasyModeModelOptionById(0);

        aiImageGeneratingToolPage.downloadAndUploadImageFromUrl("https://drive.google.com/uc?export=download" +
                "&id=1OgGooc5zVBKe8BbuPA02DwIZqAPgsXK2","example1.png");
        aiImageGeneratingToolPage.downloadAndUploadImageFromUrl("https://drive.google.com/uc?export=download" +
                "&id=1t7561PzIA2LucSa_CFjHKdtLb0hyXpeW","example2.png");
        aiImageGeneratingToolPage.downloadAndUploadImageFromUrl("https://drive.google.com/uc?export=download" +
                "&id=1xMdhSM3sj48iNyMi5h6eW-BlbkUtScIo","example3.png");

        int operationLimitsCost = aiImageGeneratingToolPage.checkLimitsCostOnImageGenerating();
        aiImageGeneratingToolPage.generateImage();
        aiImageGeneratingToolPage.waitForImageGenerationToComplete();

        int finalLimitsAmount = mainPage.checkLimitsAmount();
        int finalAmountImagesInHistory = aiImageGeneratingToolPage.countImagesInHistoryTab();

        Allure.step("Проверяем, что количество изображений в истории увеличилось на число " +
                "вновь сгенерированных изображений", () -> {
            assertEquals(finalAmountImagesInHistory, initialImagesAmountInHistory + 1,
                    "Количество сгенерированных изображений в истории не увеличилось на " + 1);
        });

        Allure.step("Проверяем, что остаток лимито уменьшился на стоимость генерации", () -> {
            assertEquals(finalLimitsAmount, initialLimitsAmount - operationLimitsCost,
                    "Некорректно подсчитан остаток лимитов после операции");
        });
    }
}
