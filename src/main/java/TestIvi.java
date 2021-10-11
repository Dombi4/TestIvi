import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class TestIvi {

// Запускаем тесты по очереди. Для экономи ресурсов импорт минимален
// Хром ругался на безопасность, поэтому нашел ему замену
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
		System.setProperty("webdriver.gecko.driver", "src\\main\\resources\\geckodriver.exe");
			System.out.println(test1());
			System.out.println(test2());
			System.out.println(test3());

	}

	// неавторизованный пользователь заходит в https://www.google.com/
	//ищет ivi
	//переходит в картинки
	//выбирает большие
	//убеждается, что не менее 3 картинок в выдаче ведут на сайт ivi.ru
	public static String test1() {
		// запускаем
		WebDriver driver = new FirefoxDriver();
		String ivi = "Ivi";
		// размер окна на максималках
		driver.manage().window().maximize();
		// Если будет как планировалось
		try {
			// открываем страницу
			driver.get("https://www.google.com");
			//Неявное ожидание для загрузки сайта
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			// Ищем строку поиска
			WebElement input = driver.findElement(By.xpath("//input[@title='Поиск']"));
			// вводим текст и жмем ентер
			input.sendKeys(ivi, Keys.ENTER);
			// Находим и переключаемся на картинки
			WebElement pic = driver.findElement(By.xpath("//a[@data-hveid][text() = 'Картинки']"));
			pic.click();
			// Собираем все элементы из больших картинок
			List<WebElement> link = driver.findElements(By.xpath("//div[@class = 'islrc']//a[@target]"));
			//Отбираем и считаем те которые с нужной нам ссылкой
			int i = 0;
			for (WebElement ne : link) {
				String temp = ne.getAttribute("href");
				String sub = temp.substring(0, 18);
				if (sub.equalsIgnoreCase("https://www.ivi.ru")) {
					i++;
				}
			}
			// закрываем браузер
			driver.close();
			// Оцениваем результат
			if (i > 3) {
				return ("Test1 OK");
			} else {
				return ("Beda Test1");
			}
			//Если что-то пошло не так
		} catch (Exception e){
			driver.close();
			return ("TEST1 TROUBLE");
		}
	}

	// неавторизованный пользователь заходит в https://www.google.com/
	//ищет ivi
	//на первых 5 страницах находит ссылки на приложение ivi в play.google.com
	//убеждается, что рейтинг приложения на кратком контенте страницы совпадает с рейтингом при переходе
	public static String test2(){
		WebDriver driver = new FirefoxDriver();
		String ivi = "Ivi";
		driver.manage().window().maximize();
		try {
			driver.get("https://www.google.com");
			//Неявное ожидание для загрузки сайта
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			WebElement input = driver.findElement(By.xpath("//input[@title='Поиск']"));
			input.sendKeys(ivi, Keys.ENTER);
			// Привычка с языка Си
			List<WebElement> link = null;
			// будем проверять 5 страниц
			for (int i = 0; i < 4; i++ ){
				// нужен в случае плохого инета, как сейчас у меня, проверяет что все необходимое загрузилось
				// явное ожидание
				WebElement nextPage = (new WebDriverWait(driver, Duration.ofSeconds(10)))
						.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Следующая")));
				// так как в ТЗ не прописано что с этим дальше делать, оставил так
				// просто заполняем список если на странице есть переход ivi в play.google.com
				link = driver.findElements(By.xpath("//div[@data-hveid][@data-ved][div/div/a[@href='https://play.google.com/store/apps/details?id=ru.ivi.client&hl=ru&gl=US']]"));
				nextPage.click();
			}
			// возвращаемся на первую страницу
			WebElement firstPage = driver.findElement(By.xpath("//a[@aria-label='Page 1']"));
			firstPage.click();
			// тут снова медленный инет
			WebElement nextPage = (new WebDriverWait(driver, Duration.ofSeconds(10)))
					.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Следующая")));
			// элемент для перехода ivi в play.google.com, если её нет на первой странице поисковика,
			// то упавший тест это уже проблема другого отдела ))
			WebElement iviLink = driver.findElement(By.xpath("//a[@href='https://play.google.com/store/apps/details?id=ru.ivi.client&hl=ru&gl=US']"));
			//так вытащим данные по рейтингу
			link = driver.findElements(By.xpath("//div[@data-hveid][@data-ved][div/div/a[@href='https://play.google.com/store/apps/details?id=ru.ivi.client&hl=ru&gl=US']]"));
			String temp = null;
			// так отсечем лишнее
			for (WebElement e : link) {
				temp = e.getText();
			}
			String temp1 = temp.substring(temp.indexOf("Рейтинг:")+9);
			String rating = temp1.split(" ")[0];
			// идем на play.google.com
			iviLink.click();
			// смотрим рейтинг
			WebElement ratingGP = driver.findElement(By.xpath("//c-wiz[@data-p]/div/div[text()]"));
			String rating2 = ratingGP.getText();
			driver.close();
			// результаты теста2
			return ("Test2 Rating = " + rating + " " + rating2.equals(rating) );

		} catch (Exception e){
			driver.close();
			return ("TEST2 TROUBLE");
		}
	}
//неавторизованный пользователь заходит в https://www.google.com/
//ищет ivi
//на первых 5 страницах находит ссылку на статью в wikipedia об ivi
//убеждается, что в статье есть ссылка на официальный сайт ivi.ru
	public static String test3(){
		WebDriver driver = new FirefoxDriver();
		String ivi = "Ivi";
		driver.manage().window().maximize();
		try {
			driver.get("https://www.google.com");
			String testResume = "FALSE";
			//Неявное ожидание для загрузки сайта
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			WebElement input = driver.findElement(By.xpath("//input[@title='Поиск']"));
			input.sendKeys(ivi, Keys.ENTER);
			// проверяем 5 страниц
			for (int i = 0; i < 4; i++ ){
				WebElement nextPage = (new WebDriverWait(driver, Duration.ofSeconds(10)))
						.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Следующая")));
				// ищем ссылки на вики
				Boolean linkPresent = driver.findElements(By.xpath("//div[@data-hveid][@data-ved][div/div/a[@href='https://ru.wikipedia.org/wiki/Ivi.ru']]")).size() > 0;
				// если нашли
				if (linkPresent == true){
					//идем по ссылке
					WebElement link = driver.findElement(By.xpath("//h3[text()='Ivi.ru - — Википедия']"));
					link.click();
					// проверяем есть ли ссылка на ivi.ru
					Boolean linkIviInWiki = driver.findElements(By.xpath("//a[@href = 'https://www.ivi.ru/']")).size() > 0;
					if (linkIviInWiki == true){
						testResume = "OK";
					}
					// возвращаемся на google.com
					driver.navigate().back();
				}
				// после driver.navigate().back() теряется nextPage, поэтому костыль
				WebElement nextPage1 = (new WebDriverWait(driver, Duration.ofSeconds(10)))
						.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Следующая")));
				nextPage1.click();
			}
			driver.close();
			return ("Test3 " + testResume);
		} catch (Exception e){
			driver.close();
			return ("TEST3 TROUBLE");
		}
	}
}
