package com.vlu.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.google.gson.JsonObject;
import com.vlu.helpers.JsonHelper;
import com.vlu.pages.LoginPage;
import com.vlu.pages.NavigationPage;
import com.vlu.pages.ProductPage;
import com.vlu.pages.RegisterPage;

public class Module_05_ProductReviewAndUITest {

    protected WebDriver driver;
    protected JsonObject testData;
    protected NavigationPage nav;
    protected LoginPage loginPage;
    protected RegisterPage regPage;
    protected ProductPage prodPage;

    // Email biến động theo thời gian để tránh trùng tài khoản khi chạy lại test suite
    private String emailGiaoDich; 

    // ========================================================================
    // ⏳ BEFORE CLASS: Nạp dữ liệu kiểm thử từ JSON một lần duy nhất
    // ========================================================================
    @BeforeClass
    public void setupClass() {
        System.out.println(">>> [SETUP CLASS] Đang nạp dữ liệu từ file TestData.json...");
        testData = JsonHelper.readJson("src/test/resources/TestData.json");
    }

    // ========================================================================
    // 🕒 BEFORE METHOD: Khởi động trình duyệt sạch trước mỗi Testcase
    // ========================================================================
    @BeforeMethod
    public void setup() {
        System.out.println(">>> [BEFORE METHOD] Khởi động trình duyệt sạch...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); 
        
        System.out.println(">>> [BEFORE METHOD] Điều hướng đến trang kiểm thử...");
        driver.get("https://automationexercise.com");
        
        nav = new NavigationPage(driver);
        loginPage = new LoginPage(driver);
        regPage = new RegisterPage(driver);
        prodPage = new ProductPage(driver);

        // Tạo email duy nhất cho mỗi lượt chạy kịch bản
        emailGiaoDich = "thong_checkout_" + System.currentTimeMillis() + "@gmail.com";
    }
    
    // ========================================================================
    // 🎯 TC_021: ADD REVIEW ON PRODUCT (9 STEPS)
    // ========================================================================
    @Test(priority = 1, description = "TC_021: Xác thực tính năng gửi nhận xét/đánh giá tại trang chi tiết sản phẩm")
    public void TC_21_AddReviewOnProduct_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======            CHẠY TC_021: ADD REVIEW ON PRODUCT      ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Bốc dữ liệu động từ JSON
        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountData = authModule.getAsJsonObject("TaiKhoanHopLe");
        String noiDungReview = "Sản phẩm chất lượng tốt, giao hàng nhanh chóng, đúng mô tả đồ án VLU!";

        // Step 1, 2: Kiểm tra hiển thị trang chủ
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        softAssert.assertTrue(acTitle.contains(exTitle), "Lỗi Step 1,2: Không tải được trang chủ!");

        // Step 3, 4: Click vào nút 'Products' trên thanh menu và kiểm tra chuyển hướng
        System.out.println("[STEP 3, 4] Điều hướng sang giao diện trang ALL PRODUCTS...");
        nav.clickProductsMenu();
        Thread.sleep(1500);
        
        String exProductsUrl = "/products";
        String acProductsUrl = driver.getCurrentUrl();
        System.out.println("[STEP 4 - VERIFY URL] Xác thực URL trang sản phẩm:");
        System.out.println("   -> Expected Result: URL chứa '" + exProductsUrl + "'");
        System.out.println("   -> Actual Result  : '" + acProductsUrl + "'");
        softAssert.assertTrue(acProductsUrl.contains(exProductsUrl), "Lỗi Step 4: Không điều hướng đúng về trang danh sách sản phẩm!");

        // Step 5: Click vào nút 'View Product' của một sản phẩm bất kỳ
        System.out.println("[STEP 5] Click chọn nút 'View Product' của sản phẩm đầu tiên...");
        prodPage.clickXemSanPhamDauTien();
        Thread.sleep(1500);

        // Step 6: Kiểm tra sự hiển thị của khu vực viết đánh giá 'Write Your Review'
        System.out.println("[STEP 6] Xác thực khu vực viết đánh giá hiển thị trên trang chi tiết:");
        boolean isReviewSectionDisplayed = prodPage.isFormWriteYourReviewHienThi();
        System.out.println("   -> Expected Result: true (Form review hiển thị rõ ràng)");
        System.out.println("   -> Actual Result  : " + isReviewSectionDisplayed);
        softAssert.assertTrue(isReviewSectionDisplayed, "Lỗi Step 6: Form 'Write Your Review' không hiển thị trên giao diện!");

        // Step 7: Nhập đầy đủ thông tin: Tên, Email và Nội dung nhận xét
        System.out.println("[STEP 7] Tiến hành điền thông tin đánh giá từ dữ liệu JSON:");
        System.out.println("   -> Name : " + accountData.get("name").getAsString());
        System.out.println("   -> Email: " + accountData.get("email").getAsString());
        prodPage.dienFormReview(
            accountData.get("name").getAsString(),
            accountData.get("email").getAsString(),
            noiDungReview
        );
        Thread.sleep(1000);

        // Step 8: Click vào nút 'Submit' phía dưới form đánh giá
        System.out.println("[STEP 8] Click vào nút 'Submit' gửi đánh giá lên server...");
        prodPage.clickSubmitReview();

        // Step 9: Xác thực thông báo gửi đánh giá thành công
        System.out.println("[STEP 9] Xác thực thông báo phản hồi thành công từ hệ thống:");
        String exSuccessMsg = "Thank you for your review.";
        String acSuccessMsg = prodPage.layThongBaoReviewThanhCong();
        System.out.println("   -> Expected Result: '" + exSuccessMsg + "'");
        System.out.println("   -> Actual Result  : '" + acSuccessMsg + "'");
        
        softAssert.assertEquals(acSuccessMsg, exSuccessMsg, "Lỗi Step 9: Thông báo phản hồi gửi nhận xét bị sai hoặc không xuất hiện!");
        System.out.println("   ==> Check Step 9 hoàn tất.\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC BÀI TEST THỰC THI: ĐANG TỔNG HỢP BÁO CÁO LỖI VLU...");
        System.out.println("================================================================");
        
        softAssert.assertAll();
    }
    
    // ========================================================================
    // 🎯 TC_025: SCROLL UP USING 'ARROW' BUTTON AND SCROLL DOWN (7 STEPS)
    // ========================================================================
    @Test(priority = 2, description = "TC_025: Xác thực tính năng cuộn xuống cuối trang và dùng nút mũi tên để cuộn lên đầu trang")
    public void TC_25_ScrollUpUsingArrowButton_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_025: SCROLL UP USING ARROW BUTTON     ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Step 1, 2, 3: Xác thực trang chủ hiển thị
        softAssert.assertTrue(driver.getTitle().contains("Automation Exercise"), "Lỗi: Không tải được trang chủ!");

        // Step 4: Cuộn trang xuống vùng dưới cùng (Footer)
        System.out.println("[STEP 4] Thực hiện cuộn chuột xuống cuối trang...");
        prodPage.cuonXuongCuoiTrang(); // Tái sử dụng hàm từ TC_022
        Thread.sleep(1500);
    

        // Step 5: Xác thực chữ 'SUBSCRIPTION' hiển thị rõ ràng ở footer
        System.out.println("[STEP 5] Xác thực chữ 'SUBSCRIPTION' xuất hiện ở Footer:");
        boolean isSubDisplayed = prodPage.isChunghNhankSubscriptionHienThi();
        System.out.println("   -> Expected Result: true");
        System.out.println("   -> Actual Result  : " + isSubDisplayed);
        softAssert.assertTrue(isSubDisplayed, "Lỗi Step 5: Không thấy chữ SUBSCRIPTION ở cuối trang!");
        Thread.sleep(1500);

        // Step 6: Click vào nút mũi tên cuộn lên (Arrow button) ở góc dưới bên phải
        System.out.println("[STEP 6] Click vào nút mũi tên cuộn lên nhanh ở góc phải...");
        prodPage.clickNutMuiTenCuonLen();
        Thread.sleep(1500);

        // Step 7: Xác thực trang web tự động cuộn lên đầu và hiển thị text trên banner
        System.out.println("[STEP 7] Xác thực màn hình đã cuộn lên đầu và hiển thị Banner:");
        boolean isBannerDisplayed = prodPage.isBannerHeaderHienThi();
        String txtBanner = prodPage.layTextBannerHeader();
        System.out.println("   -> Expected Result: true (Hiển thị văn bản chứa 'Automation')");
        System.out.println("   -> Actual Result  : " + isBannerDisplayed + " | Văn bản: '" + txtBanner + "'");
        
        softAssert.assertTrue(isBannerDisplayed && txtBanner.contains("Automation"), "Lỗi Step 7: Trang không tự cuộn lên đầu hoặc mất banner!");
        System.out.println("   ==> Check TC_025 hoàn tất.\n");
        softAssert.assertAll();
    }

    // ========================================================================
    // 🎯 TC_026: SCROLL UP WITHOUT 'ARROW' BUTTON AND SCROLL DOWN (7 STEPS)
    // ========================================================================
    @Test(priority = 3, description = "TC_026: Xác thực tính năng tự cuộn ngược lên đầu trang bằng chuột")
    public void TC_26_ScrollUpWithoutArrowButton_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_026: SCROLL UP WITHOUT ARROW BUTTON   ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Step 1, 2, 3: Xác thực trang chủ hiển thị
        softAssert.assertTrue(driver.getTitle().contains("Automation Exercise"), "Lỗi: Không tải được trang chủ!");

        // Step 4: Cuộn trang xuống vùng dưới cùng (Footer)
        System.out.println("[STEP 4] Thực hiện cuộn chuột xuống cuối trang...");
        prodPage.cuonXuongCuoiTrang();
        Thread.sleep(1500);

        // Step 5: Xác thực chữ 'SUBSCRIPTION' hiển thị
        System.out.println("[STEP 5] Xác thực chữ 'SUBSCRIPTION' xuất hiện ở Footer:");
        softAssert.assertTrue(prodPage.isChunghNhankSubscriptionHienThi(), "Lỗi Step 5: Không thấy chữ SUBSCRIPTION!");

        // Step 6: Cuộn thủ công ngược lên đầu trang bằng lệnh cuộn JavaScript (Giả lập cuộn chuột)
        System.out.println("[STEP 6] Thực hiện cuộn chuột ngược lên đầu trang bằng lệnh JS...");
        prodPage.cuonLenDauTrangBangChuot();
        Thread.sleep(1500);

        // Step 7: Xác thực màn hình quay về vị trí ban đầu và hiển thị văn bản banner
        System.out.println("[STEP 7] Xác thực màn hình quay lại vị trí ban đầu thành công:");
        boolean isBannerDisplayed = prodPage.isBannerHeaderHienThi();
        String txtBanner = prodPage.layTextBannerHeader();
        System.out.println("   -> Expected Result: true (Hiển thị văn bản chứa 'Automation')");
        System.out.println("   -> Actual Result  : " + isBannerDisplayed + " | Văn bản: '" + txtBanner + "'");
        
        softAssert.assertTrue(isBannerDisplayed && txtBanner.contains("Automation"), "Lỗi Step 7: Không cuộn chuột lên đầu trang thành công!");
        System.out.println("   ==> Check TC_026 hoàn tất.\n");
        softAssert.assertAll();
    }
    
    
    
    // ========================================================================
    // 🛑 AFTER METHOD: Đóng trình duyệt, giải phóng bộ nhớ
    // ========================================================================
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            System.out.println(">>> [AFTER METHOD] Đóng cửa sổ trình duyệt, giải phóng bộ nhớ.\n");
            driver.quit();
        }
    }
}
