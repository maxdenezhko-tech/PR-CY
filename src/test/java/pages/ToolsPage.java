package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class ToolsPage {
    final SelenideElement titleElement = $x("//h1[text()='Инструменты и приложения для SEO и маркетинга']");
    final SelenideElement markdownFormattingLink = $x("//a[text()='Инструменты на искусственном " +
            "интеллекте']/parent::div/child::div/child::a[@href='/markdown-formatting/']");
    final SelenideElement imageGeneratorLink = $x("//a[text()='Инструменты на искусственном интеллекте']" +
            "/parent::div/child::div/child::a[@href='/ai-image-generator/']");
    final SelenideElement ideogramLink = $x("//a[text()='Инструменты на искусственном интеллекте']" +
            "/parent::div/child::div/child::a[@href='/ideogram/']");
    final SelenideElement geminiLink = $x("//a[text()='Инструменты на искусственном интеллекте']" +
            "/parent::div/child::div/child::a[@href='/app/gemini/']");
    final SelenideElement fluxLink = $x("//a[text()='Инструменты на искусственном интеллекте']" +
            "/parent::div/child::div/child::a[@href='/flux-image-generator/']");

    @Step("Проверяем переход на страницу инструментов")
    public void checkTitle() {
        titleElement.should(exist).shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Осуществляем выбор инструмента \"Отформатировать текст\"")
    public void chooseMarkdownTool() {
        markdownFormattingLink.click();
    }

    @Step("Осуществляем выбор инструмента \"Генерация изображений нейросетью\"")
    public void chooseImageGeneratorTool() {
        imageGeneratorLink.click();
    }

    @Step("Осуществляем выбор инструмента \"Ideogram v2\"")
    public void chooseIdeogramTool() {
        ideogramLink.click();
    }

    @Step("Осуществляем выбор инструмента \"Gemini на русском\"")
    public void chooseGeminiTool() {
        geminiLink.click();
    }

    @Step("Осуществляем выбор инструмента \"Flux\"")
    public void chooseFluxTool() {
        fluxLink.click();
    }
}
