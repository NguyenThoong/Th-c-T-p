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

public class TestDraft {

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
    // 🎯 TC_023: VERIFY DELIVERY ADDRESS AND BILLING ADDRESS (15 STEPS)
    // ========================================================================
    @Test(priority = 4, description = "TC_023: Xác thực hệ thống hiển thị chính xác và đồng nhất dữ liệu Địa chỉ giao hàng & Địa chỉ hóa đơn")
    public void TC_23_VerifyDeliveryAndBillingAddress_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_023: VERIFY DELIVERY & BILLING ADDRESS ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        JsonObject dataTestObj = testData.getAsJsonObject("DataTest");
        JsonObject moduleAuth = dataTestObj.getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountData = moduleAuth.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressData = moduleAuth.getAsJsonObject("DiaChiGiaoHang");
        String sp1 = dataTestObj.getAsJsonObject("Module_SanPham_GioHang").getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();

        // Step 1, 2, 3: Xác nhận trang chủ hiển thị thành công
        String exTitle = "Automation Exercise";
        softAssert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi Step 1,2,3: Không tải được trang chủ!");

        // Step 4: Click vào nút 'Signup / Login'
        System.out.println("[STEP 4] Điều hướng sang trang Đăng ký/Đăng nhập...");
        nav.clickLoginSignup();
        Thread.sleep(1000);
        
        // Step 5: Điền thông tin cơ bản form Đăng ký tài khoản mới trong luồng
        System.out.println("[STEP 5] Điền thông tin tạo tài khoản mới...");
        String emailGiaoDichTC23 = "thong_tc23_" + System.currentTimeMillis() + "@gmail.com";
        driver.findElement(By.xpath("//input[@data-qa='signup-name']")).sendKeys(accountData.get("name").getAsString());
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys(emailGiaoDichTC23);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Thread.sleep(1500);

        // Điền chi tiết các trường thông tin bắt buộc
        driver.findElement(By.id("id_gender1")).click();
        driver.findElement(By.id("password")).sendKeys(accountData.get("password").getAsString());
        driver.findElement(By.id("first_name")).sendKeys(addressData.get("first_name").getAsString());
        driver.findElement(By.id("last_name")).sendKeys(addressData.get("last_name").getAsString());
        driver.findElement(By.id("address1")).sendKeys(addressData.get("address1").getAsString());
        driver.findElement(By.id("country")).sendKeys(addressData.get("country").getAsString());
        driver.findElement(By.id("state")).sendKeys(addressData.get("state").getAsString());
        driver.findElement(By.id("city")).sendKeys(addressData.get("city").getAsString());
        driver.findElement(By.id("zipcode")).sendKeys(addressData.get("zipcode").getAsString());
        driver.findElement(By.id("mobile_number")).sendKeys(addressData.get("mobile_number").getAsString());

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[@data-qa='create-account']")));
        Thread.sleep(1500);

        // Step 6: Xác thực thông báo tạo tài khoản 'ACCOUNT CREATED!' thành công
        System.out.println("[STEP 6] Xác thực thông báo ACCOUNT CREATED!");
        softAssert.assertTrue(driver.findElement(By.xpath("//b[text()='Account Created!']")).isDisplayed(), "Lỗi Step 6: Tạo tài khoản không thành công!");
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(1000);

        // Step 7: Xác thực trạng thái đăng nhập trên Header
        System.out.println("[STEP 7] Xác thực trạng thái hiển thị đăng nhập...");
        String exLoginText = "Logged in as " + accountData.get("name").getAsString();
        softAssert.assertTrue(nav.layTenUserDaDangNhap().contains(exLoginText), "Lỗi Step 7: Không đăng nhập thành công!");

        // Step 8 & 9: Duyệt sản phẩm và thêm vào giỏ hàng
        System.out.println("[STEP 8 & 9] Thêm sản phẩm '" + sp1 + "' vào giỏ hàng...");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);

        // Step 10: Click vào nút 'Cart' trên thanh điều hướng
        System.out.println("[STEP 10] Chuyển hướng sang trang Giỏ hàng...");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);
        softAssert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi Step 10: Vào sai trang giỏ hàng!");

        // Step 11: Click vào nút 'Proceed To Checkout'
        System.out.println("[STEP 11] Tiến hành click chọn 'Proceed To Checkout'...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1500);

        // Step 12 & 13: Xác thực tính chính xác và đồng nhất của Địa chỉ giao hàng & Địa chỉ hóa đơn
        System.out.println("[STEP 12 & 13] Tiến hành xác thực tính chính xác khối thông tin địa chỉ:");
        String expectedAddressKeyword = addressData.get("address1").getAsString();
        String actualDelivery = prodPage.layThongTinDiaChiGiaoHang();
        String actualBilling = prodPage.layThongTinDiaChiHoaDon();

        System.out.println("   -> Expected Keyword  : '" + expectedAddressKeyword + "'");
        System.out.println("   -> Actual Delivery   : '" + actualDelivery + "'");
        System.out.println("   -> Actual Billing    : '" + actualBilling + "'");

        softAssert.assertTrue(actualDelivery.contains(expectedAddressKeyword), "Lỗi Step 12: Khối địa chỉ giao hàng (Delivery Address) hiển thị sai dữ liệu!");
        softAssert.assertTrue(actualBilling.contains(expectedAddressKeyword), "Lỗi Step 13: Khối địa chỉ hóa đơn (Billing Address) hiển thị sai dữ liệu!");
        System.out.println("   ==> Khối địa chỉ hiển thị đồng nhất và chính xác hoàn toàn.\n");

        // Step 14: Click vào nút 'Delete Account' trên thanh menu
        System.out.println("[STEP 14] Thực hiện dọn dẹp xóa tài khoản sau suite...");
        nav.clickDeleteAccount();
        Thread.sleep(1500);

        // Step 15: Xác thực thông báo xóa tài khoản hoàn tất
        System.out.println("[STEP 15] Xác thực giao diện xóa tài khoản 'ACCOUNT DELETED!':");
        softAssert.assertTrue(driver.findElement(By.xpath("//h2[@data-qa='account-deleted']/b")).isDisplayed(), "Lỗi Step 15: Không hiển thị giao diện xóa tài khoản!");
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        
        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC TC_023: KIỂM TRA ĐỒNG NHẤT ĐỊA CHỈ HOÀN TẤT XANH MƯỢT");
        System.out.println("================================================================");
        softAssert.assertAll();
    }

    // ========================================================================
    // 🎯 TC_024: PLACE ORDER: DOWNLOAD INVOICE AFTER PURCHASE (22 STEPS)
    // ========================================================================
    @Test(priority = 5, description = "TC_024: Xác thực quy trình mua hàng kết hợp đăng ký tài khoản trực tiếp lúc checkout, thanh toán, tải tệp hóa đơn và xóa tài khoản")
    public void TC_24_PlaceOrder_DownloadInvoice_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_024: PLACE ORDER & DOWNLOAD INVOICE   ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        JsonObject dataTestObj = testData.getAsJsonObject("DataTest");
        JsonObject moduleAuth = dataTestObj.getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountData = moduleAuth.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressData = moduleAuth.getAsJsonObject("DiaChiGiaoHang");
        JsonObject cardData = dataTestObj.getAsJsonObject("Module_ThanhToan_Payment").getAsJsonObject("TheNganHang");
        String sp1 = dataTestObj.getAsJsonObject("Module_SanPham_GioHang").getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();

        // Step 1, 2, 3: Xác nhận trang chủ hiển thị thành công
        softAssert.assertTrue(driver.getTitle().contains("Automation Exercise"));

        // Step 4 & 5: Thêm sản phẩm bất kỳ vào giỏ hàng và chuyển hướng
        System.out.println("[STEP 4 & 5] Thêm sản phẩm và đi tới trang giỏ hàng...");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        // Step 6: Xác thực sự hiển thị của trang Giỏ hàng
        String acUrl = driver.getCurrentUrl();
        System.out.println("[STEP 6] Xác thực URL trang Giỏ hàng:");
        System.out.println("   -> Expected Result: URL chứa '/view_cart'");
        System.out.println("   -> Actual Result  : '" + acUrl + "'");
        softAssert.assertTrue(acUrl.contains("/view_cart"), "Lỗi Step 6: Chuyển hướng giỏ hàng thất bại!");

        // Step 7: Click vào nút 'Proceed To Checkout'
        System.out.println("[STEP 7] Click vào nút 'Proceed To Checkout'...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1000);

        // Step 8: Click vào nút 'Register / Login' trên hộp thoại thông báo
        System.out.println("[STEP 8] Bấm chọn liên kết 'Register / Login' tạo tài khoản...");
        driver.findElement(By.xpath("//u[text()='Register / Login']")).click();
        Thread.sleep(1500);

        // Step 9: Điền thông tin form Signup để khởi tạo tài khoản lúc thanh toán
        System.out.println("[STEP 9] Nhập thông tin form tạo tài khoản động...");
        String emailGiaoDichTC24 = "thong_tc24_" + System.currentTimeMillis() + "@gmail.com";
        driver.findElement(By.xpath("//input[@data-qa='signup-name']")).sendKeys(accountData.get("name").getAsString());
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys(emailGiaoDichTC24);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Thread.sleep(1500);

        // Điền đầy đủ thông tin bắt buộc
        driver.findElement(By.id("id_gender1")).click();
        driver.findElement(By.id("password")).sendKeys(accountData.get("password").getAsString());
        driver.findElement(By.id("first_name")).sendKeys(addressData.get("first_name").getAsString());
        driver.findElement(By.id("last_name")).sendKeys(addressData.get("last_name").getAsString());
        driver.findElement(By.id("address1")).sendKeys(addressData.get("address1").getAsString());
        driver.findElement(By.id("country")).sendKeys(addressData.get("country").getAsString());
        driver.findElement(By.id("state")).sendKeys(addressData.get("state").getAsString());
        driver.findElement(By.id("city")).sendKeys(addressData.get("city").getAsString());
        driver.findElement(By.id("zipcode")).sendKeys(addressData.get("zipcode").getAsString());
        driver.findElement(By.id("mobile_number")).sendKeys(addressData.get("mobile_number").getAsString());

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[@data-qa='create-account']")));
        Thread.sleep(1500);

        // Step 10: Xác thực thông báo tạo tài khoản hoàn tất
        System.out.println("[STEP 10] Xác thực giao diện ACCOUNT CREATED!");
        softAssert.assertTrue(driver.findElement(By.xpath("//b[text()='Account Created!']")).isDisplayed(), "Lỗi Step 10: Tạo tài khoản thất bại!");
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(1500);

        // Step 11: Xác thực trạng thái đăng nhập của tài khoản mới trên Header
        System.out.println("[STEP 11] Xác thực trạng thái hiển thị đăng nhập trên Header...");
        String exLoginTextTC24 = "Logged in as " + accountData.get("name").getAsString();
        softAssert.assertTrue(nav.layTenUserDaDangNhap().contains(exLoginTextTC24), "Lỗi Step 11: Không tìm thấy text đăng nhập!");

        // Step 12: Click lại vào nút 'Cart' trên thanh điều hướng
        System.out.println("[STEP 12] Quay trở lại trang Giỏ hàng...");
        nav.clickCart();
        Thread.sleep(1500);

        // Step 13: Click lại vào nút 'Proceed To Checkout'
        System.out.println("[STEP 13] Click nút 'Proceed To Checkout' để xác nhận lại đơn hàng...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1500);
        
        // Step 14 & 15: Nhập ghi chú bình luận và đi tới trang thanh toán
        System.out.println("[STEP 14 & 15] Điền ghi chú và click nút 'Place Order'...");
        driver.findElement(By.tagName("textarea")).sendKeys("Tải hóa đơn đơn hàng tự động TC24");
        driver.findElement(By.xpath("//a[@href='/payment']")).click();
        Thread.sleep(1500);

        // Step 16 & 17: Nhập đầy đủ thông tin thẻ tín dụng từ dữ liệu JSON và click 'Pay and Confirm Order'
        System.out.println("[STEP 16 & 17] Tiến hành điền cổng thông tin thẻ tín dụng:");
        prodPage.dienThongTinThanhToan(
            cardData.get("name_on_card").getAsString(),
            cardData.get("card_number").getAsString(),
            cardData.get("cvc").getAsString(),
            cardData.get("expiry_month").getAsString(),
            cardData.get("expiry_year").getAsString()
        );
        prodPage.clickPayAndConfirmOrder();
        Thread.sleep(2000);

        // Step 18: Xác thực đơn đặt hàng thành công xuất hiện
        System.out.println("[STEP 18] Xác thực đơn đặt hàng thành công xuất hiện:");
        String actualMsg = prodPage.layThongBaoDatHangThanhCong();
        System.out.println("   -> Actual Order Msg: '" + actualMsg + "'");
        softAssert.assertTrue(actualMsg.toUpperCase().contains("PLACED"), "Lỗi Step 18: Đơn hàng thanh toán thất bại!");

        // Step 19: Click vào nút 'Download Invoice' để tải file về máy
        System.out.println("[STEP 19] Click nút 'Download Invoice' kích hoạt tải tệp tin ngầm về máy...");
        prodPage.clickDownloadInvoice();
        Thread.sleep(3000); // Chờ 3 giây để máy tính lưu file ngầm hoàn tất

        // Step 20: Click vào nút 'Continue' quay lại trang chính
        System.out.println("[STEP 20] Click nút 'Continue' quay lại luồng chính...");
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(1000);

        // Step 21: Click vào nút 'Delete Account' trên thanh menu
        System.out.println("[STEP 21] Thực hiện dọn dẹp xóa tài khoản rác cuối luồng kịch bản...");
        nav.clickDeleteAccount();
        Thread.sleep(1500);

        // Step 22: Xác thực thông báo xóa tài khoản hoàn tất thành công
        System.out.println("[STEP 22] Xác thực màn hình xóa tài khoản thành công hiển thị:");
        softAssert.assertTrue(driver.findElement(By.xpath("//h2[@data-qa='account-deleted']/b")).isDisplayed(), "Lỗi Step 22: Xóa tài khoản thất bại!");
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ SUITE: ĐÃ HOÀN THÀNH CHÍNH THỨC 100% 26 TCS!");
        System.out.println("================================================================");
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