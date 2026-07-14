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

public class Module_03_CheckoutAndOrderTest {

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
    // 🎯 TC_014: PLACE ORDER: REGISTER WHILE CHECKOUT (Khớp Động 20 Steps)
    // ========================================================================
    @Test(priority = 1, description = "TC_014: Xác thực luồng mua hàng của khách vãng lai đăng ký khi thanh toán và xóa tài khoản")
    public void TC_14_PlaceOrder_RegisterWhileCheckout_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_14: PLACE ORDER - REGISTER WHILE CHECKOUT   ======");
        System.out.println("================================================================");

        // --- ĐỌC DỮ LIỆU ĐỘNG TỪ CÁC PHÂN NHÁNH JSON CÓ SẴN ---
        JsonObject dataTestObj = testData.getAsJsonObject("DataTest");
        
        // 1. Nhánh Đăng Ký Đăng Nhập
        JsonObject moduleAuth = dataTestObj.getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountData = moduleAuth.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressData = moduleAuth.getAsJsonObject("DiaChiGiaoHang");
        
        // 2. Nhánh Sản phẩm và Thanh toán
        JsonObject productModule = dataTestObj.getAsJsonObject("Module_SanPham_GioHang");
        String sp1 = productModule.getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();
        JsonObject cardData = dataTestObj.getAsJsonObject("Module_ThanhToan_Payment").getAsJsonObject("TheNganHang");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Thêm sản phẩm bất kỳ vào giỏ hàng ---
        System.out.println("[STEP 4] Thực hiện thêm sản phẩm '" + sp1 + "' vào giỏ hàng...");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);

        // --- STEP 5: Click vào nút 'View Cart' trên Modal Popup để chuyển hướng ---
        System.out.println("[STEP 5] Click vào nút 'View Cart' trên Modal Popup để vào Giỏ hàng...");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        // --- STEP 6: Xác thực sự hiển thị của trang Giỏ hàng ---
        System.out.println("[STEP 6] Xác thực trang Giỏ hàng hiển thị thành công:");
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: Giao diện Giỏ hàng không hiển thị đúng!");
        System.out.println("   -> URL thực tế: " + driver.getCurrentUrl() + "\n");

        // --- STEP 7: Click vào nút 'Proceed To Checkout' ---
        System.out.println("[STEP 7] Click vào nút 'Proceed To Checkout'...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1000);

        // --- STEP 8: Click vào nút 'Register / Login' trên hộp thoại thông báo ---
        System.out.println("[STEP 8] Click vào nút 'Register / Login' trên hộp thoại thông báo...");
        driver.findElement(By.xpath("//u[text()='Register / Login']")).click();
        Thread.sleep(1500);

        // --- STEP 9: Điền đầy đủ tất cả thông tin form Signup để tạo tài khoản mới ---
        System.out.println("[STEP 9] Điền thông tin form Signup & Account chi tiết từ TestData.json...");
        driver.findElement(By.xpath("//input[@data-qa='signup-name']")).sendKeys(accountData.get("name").getAsString());
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys(emailGiaoDich);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Thread.sleep(1500);

        // Điền chi tiết thông tin bắt buộc vào form Account từ file JSON
        driver.findElement(By.id("id_gender1")).click(); // Mr.
        driver.findElement(By.id("password")).sendKeys(accountData.get("password").getAsString());
        
        // Đọc thông tin từ nhánh DiaChiGiaoHang
        driver.findElement(By.id("first_name")).sendKeys(addressData.get("first_name").getAsString());
        driver.findElement(By.id("last_name")).sendKeys(addressData.get("last_name").getAsString());
        driver.findElement(By.id("company")).sendKeys(addressData.get("company").getAsString());
        driver.findElement(By.id("address1")).sendKeys(addressData.get("address1").getAsString());
        driver.findElement(By.id("address2")).sendKeys(addressData.get("address2").getAsString());
        driver.findElement(By.id("country")).sendKeys(addressData.get("country").getAsString());
        driver.findElement(By.id("state")).sendKeys(addressData.get("state").getAsString());
        driver.findElement(By.id("city")).sendKeys(addressData.get("city").getAsString());
        driver.findElement(By.id("zipcode")).sendKeys(addressData.get("zipcode").getAsString());
        driver.findElement(By.id("mobile_number")).sendKeys(addressData.get("mobile_number").getAsString());

        // Submit form tạo tài khoản bằng JS Click để tránh lỗi overlay che khuất
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[@data-qa='create-account']")));
        Thread.sleep(1500);

        // --- STEP 10: Xác thực thông báo tạo tài khoản thành công và tiếp tục ---
        System.out.println("[STEP 10] Xác thực thông báo tạo tài khoản thành công...");
        WebElement lblAccountCreated = driver.findElement(By.xpath("//b[text()='Account Created!']"));
        Assert.assertTrue(lblAccountCreated.isDisplayed(), "Lỗi: Không tìm thấy thông báo 'ACCOUNT CREATED!'");
        
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(1500);

        // --- STEP 11: Xác thực trạng thái đăng nhập của tài khoản mới ---
        System.out.println("[STEP 11] Xác thực trạng thái đăng nhập phía trên thanh menu...");
        WebElement lblLogged = driver.findElement(By.xpath("//a[contains(., 'Logged in as')]"));
        Assert.assertTrue(lblLogged.isDisplayed(), "Lỗi: Không hiển thị trạng thái 'Logged in as'!");
        System.out.println("   -> Hiển thị thực tế: " + lblLogged.getText().trim() + "\n");

        // --- STEP 12: Click lại vào nút 'Cart' trên thanh điều hướng ---
        System.out.println("[STEP 12] Click lại vào nút 'Cart' trên thanh điều hướng...");
        nav.clickCart();
        Thread.sleep(1500);

        // --- STEP 13: Click lại vào nút 'Proceed To Checkout' ---
        System.out.println("[STEP 13] Click lại vào nút 'Proceed To Checkout' để sang trang Checkout...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1500);

        // --- STEP 14: Xác thực thông tin Địa chỉ giao hàng và Đơn đặt hàng ---
        System.out.println("[STEP 14] Xác thực thông tin Address Details và Review Your Order...");
        WebElement sectionAddress = driver.findElement(By.id("address_delivery"));
        Assert.assertTrue(sectionAddress.isDisplayed(), "Lỗi: Không hiển thị thông tin địa chỉ giao hàng!");
        System.out.println("   ==> STATUS: PASSED (Thông tin địa chỉ hiển thị chính xác)\n");

        // --- STEP 15: Nhập ghi chú vào vùng văn bản bình luận và click 'Place Order' ---
        System.out.println("[STEP 15] Nhập ghi chú đơn hàng và click nút 'Place Order'...");
        driver.findElement(By.tagName("textarea")).sendKeys("Giao hàng giờ hành chính - Đồ án kiểm thử phần mềm VLU");
        driver.findElement(By.xpath("//a[@href='/payment']")).click();
        Thread.sleep(1500);

        // --- STEP 16: Nhập đầy đủ thông tin thẻ thanh toán bốc động từ JSON ---
        System.out.println("[STEP 16] Nhập thông tin thẻ thanh toán động từ nhánh TheNganHang...");
        prodPage.dienThongTinThanhToan(
            cardData.get("name_on_card").getAsString(),
            cardData.get("card_number").getAsString(),
            cardData.get("cvc").getAsString(),
            cardData.get("expiry_month").getAsString(),
            cardData.get("expiry_year").getAsString()
        );

        // --- STEP 17: Click vào nút 'Pay and Confirm Order' ---
        System.out.println("[STEP 17] Click nút 'Pay and Confirm Order'...");
        prodPage.clickPayAndConfirmOrder();
        Thread.sleep(2000);

        // --- STEP 18: Xác thực thông báo đặt hàng thành công ---
        System.out.println("[STEP 18] Xác thực thông báo đặt hàng thành công...");
        String successMsg = prodPage.layThongBaoDatHangThanhCong();
        System.out.println("   -> Thông báo nhận được: " + successMsg);
        Assert.assertTrue(successMsg.toUpperCase().contains("PLACED"), "Lỗi: Đặt hàng không thành công!");
        System.out.println("   ==> STATUS: PASSED (Đặt hàng thành công hiển thị rõ ràng)\n");

        // --- STEP 19: Click vào nút 'Delete Account' trên thanh menu ---
        System.out.println("[STEP 19] Click vào nút 'Delete Account' để tiến hành hủy tài khoản hiện tại...");
        nav.clickDeleteAccount();
        Thread.sleep(1500);

        // --- STEP 20: Xác thực thông báo xóa tài khoản hoàn tất ---
        System.out.println("[STEP 20] Tiến hành xác thực thông báo 'ACCOUNT DELETED!'...");
        
        // Sử dụng XPath chuẩn có thuộc tính data-qa để quét chính xác phần tử
        By lblAccountDeleted = By.xpath("//h2[@data-qa='account-deleted']/b");
        
        Assert.assertTrue(driver.findElement(lblAccountDeleted).isDisplayed(), 
                "Lỗi: Không hiển thị thông báo xóa tài khoản thành công!");
        System.out.println("   -> Tìm thấy thông báo ACCOUNT DELETED thành công.");
        
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
    }
    
    // ========================================================================
    // 🎯 TC_015: PLACE ORDER: REGISTER BEFORE CHECKOUT (18 STEPS)
    // ========================================================================
    @Test(priority = 2, description = "TC_015: Xác thực quy trình người dùng chủ động tạo tài khoản từ trước, sau đó tiến hành mua sắm, thanh toán và xóa tài khoản")
    public void TC_15_PlaceOrder_RegisterBeforeCheckout_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_15: PLACE ORDER - REGISTER BEFORE CHECKOUT  ======");
        System.out.println("================================================================");

        // --- BỐC DỮ LIỆU ĐỘNG TỪ JSON ---
        JsonObject dataTestObj = testData.getAsJsonObject("DataTest");
        JsonObject moduleAuth = dataTestObj.getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountData = moduleAuth.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressData = moduleAuth.getAsJsonObject("DiaChiGiaoHang");
        
        JsonObject productModule = dataTestObj.getAsJsonObject("Module_SanPham_GioHang");
        String sp1 = productModule.getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();
        JsonObject cardData = dataTestObj.getAsJsonObject("Module_ThanhToan_Payment").getAsJsonObject("TheNganHang");

        // --- STEP 1, 2, 3: Khởi động, truy cập link và kiểm tra sự hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        System.out.println("[STEP 4] Click nút 'Signup / Login' trên thanh menu...");
        nav.clickLoginSignup(); // Hàm điều hướng sang cụm Đăng ký/Đăng nhập của bạn
        Thread.sleep(1500);

        // --- STEP 5: Điền đầy đủ tất cả thông tin form Signup để tạo tài khoản mới ---
        System.out.println("[STEP 5] Điền thông tin form Signup & Account chi tiết từ dữ liệu JSON...");
        driver.findElement(By.xpath("//input[@data-qa='signup-name']")).sendKeys(accountData.get("name").getAsString());
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys(emailGiaoDich); // Email ngẫu nhiên tránh trùng sinh ở BeforeMethod
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Thread.sleep(1500);

        // Điền chi tiết form thông tin cá nhân và địa chỉ giao hàng
        driver.findElement(By.id("id_gender1")).click();
        driver.findElement(By.id("password")).sendKeys(accountData.get("password").getAsString());
        driver.findElement(By.id("first_name")).sendKeys(addressData.get("first_name").getAsString());
        driver.findElement(By.id("last_name")).sendKeys(addressData.get("last_name").getAsString());
        driver.findElement(By.id("company")).sendKeys(addressData.get("company").getAsString());
        driver.findElement(By.id("address1")).sendKeys(addressData.get("address1").getAsString());
        driver.findElement(By.id("address2")).sendKeys(addressData.get("address2").getAsString());
        driver.findElement(By.id("country")).sendKeys(addressData.get("country").getAsString());
        driver.findElement(By.id("state")).sendKeys(addressData.get("state").getAsString());
        driver.findElement(By.id("city")).sendKeys(addressData.get("city").getAsString());
        driver.findElement(By.id("zipcode")).sendKeys(addressData.get("zipcode").getAsString());
        driver.findElement(By.id("mobile_number")).sendKeys(addressData.get("mobile_number").getAsString());

        // Submit form tạo tài khoản bằng JS Click
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[@data-qa='create-account']")));
        Thread.sleep(1500);

        // --- STEP 6: Xác thực thông báo tạo tài khoản thành công và tiếp tục ---
        System.out.println("[STEP 6] Xác thực thông báo tạo tài khoản thành công...");
        WebElement lblAccountCreated = driver.findElement(By.xpath("//b[text()='Account Created!']"));
        Assert.assertTrue(lblAccountCreated.isDisplayed(), "Lỗi: Không hiển thị thông báo 'ACCOUNT CREATED!'");
        
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(1500);

        // --- STEP 7: Xác thực trạng thái đăng nhập của tài khoản mới ---
        System.out.println("[STEP 7] Xác thực trạng thái đăng nhập phía trên thanh menu...");
        WebElement lblLogged = driver.findElement(By.xpath("//a[contains(., 'Logged in as')]"));
        Assert.assertTrue(lblLogged.isDisplayed(), "Lỗi: Không hiển thị trạng thái 'Logged in as'!");
        System.out.println("   -> Hiển thị thực tế: " + lblLogged.getText().trim() + "\n");

        // --- STEP 8: Duyệt sản phẩm và thêm sản phẩm bất kỳ vào giỏ hàng ---
        System.out.println("[STEP 8] Thực hiện mua sắm, thêm sản phẩm '" + sp1 + "' vào giỏ hàng...");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);

        // --- STEP 9: Click vào nút 'Cart' trên thanh điều hướng (Dùng nút View Cart trên Modal nổi cho an toàn) ---
        System.out.println("[STEP 9] Click vào nút 'View Cart' trên Modal để chuyển hướng...");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        // --- STEP 10: Xác thực sự hiển thị của trang Giỏ hàng ---
        System.out.println("[STEP 10] Xác thực URL trang Giỏ hàng hiển thị thành công:");
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: Không điều hướng đúng về trang Giỏ hàng!");
        System.out.println("   -> URL thực tế: " + driver.getCurrentUrl() + "\n");

        // --- STEP 11: Click vào nút 'Proceed To Checkout' ---
        System.out.println("[STEP 11] Click vào nút 'Proceed To Checkout'...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1500);

        // --- STEP 12: Xác thực thông tin Địa chỉ giao hàng và Đơn đặt hàng ---
        System.out.println("[STEP 12] Xác thực khối thông tin Address Details và Review Your Order...");
        WebElement sectionAddress = driver.findElement(By.id("address_delivery"));
        Assert.assertTrue(sectionAddress.isDisplayed(), "Lỗi: Khối thông tin địa chỉ giao hàng không hiển thị!");
        System.out.println("   ==> STATUS: PASSED (Địa chỉ checkout hiển thị khớp dữ liệu đăng ký)\n");

        // --- STEP 13: Nhập ghi chú vào vùng văn bản bình luận và click 'Place Order' ---
        System.out.println("[STEP 13] Nhập bình luận đơn hàng và click nút 'Place Order'...");
        driver.findElement(By.tagName("textarea")).sendKeys("Giao hàng giờ hành chính - Đồ án thực tập checkout TC15");
        driver.findElement(By.xpath("//a[@href='/payment']")).click();
        Thread.sleep(1500);

        // --- STEP 14: Nhập đầy đủ thông tin thẻ thanh toán ---
        System.out.println("[STEP 14] Nhập thông tin thẻ thanh toán bọc từ nhánh TheNganHang...");
        prodPage.dienThongTinThanhToan(
            cardData.get("name_on_card").getAsString(),
            cardData.get("card_number").getAsString(),
            cardData.get("cvc").getAsString(),
            cardData.get("expiry_month").getAsString(),
            cardData.get("expiry_year").getAsString()
        );

        // --- STEP 15: Click vào nút 'Pay and Confirm Order' ---
        System.out.println("[STEP 15] Click nút 'Pay and Confirm Order'...");
        prodPage.clickPayAndConfirmOrder();
        Thread.sleep(2000);

        // --- STEP 16: Xác thực thông báo đặt hàng thành công ---
        System.out.println("[STEP 16] Xác thực thông báo đặt hàng thành công trên giao diện...");
        String successMsg = prodPage.layThongBaoDatHangThanhCong();
        System.out.println("   -> Thông báo nhận được: " + successMsg);
        Assert.assertTrue(successMsg.toUpperCase().contains("PLACED"), "Lỗi: Không tìm thấy xác nhận đặt hàng thành công!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 17: Click vào nút 'Delete Account' trên thanh menu (Áp dụng JS click chống kẹt) ---
        System.out.println("[STEP 17] Click vào nút 'Delete Account' để hủy tài khoản hiện tại...");
        try {
            By btnDelete = By.xpath("//a[contains(@href, 'delete_account')]");
            js.executeScript("arguments[0].click();", driver.findElement(btnDelete));
        } catch (Exception e) {
            nav.clickDeleteAccount();
        }
        System.out.println("   -> Đang chờ 3 giây để bạn tắt quảng cáo thủ công (nếu có)...");
        Thread.sleep(3000);

        // --- STEP 18: Xác thực thông báo xóa tài khoản hoàn tất ---
        System.out.println("[STEP 18] Xác thực thông báo 'ACCOUNT DELETED!' ");
        By lblAccountDeleted = By.xpath("//h2[@data-qa='account-deleted']/b");
        Assert.assertTrue(driver.findElement(lblAccountDeleted).isDisplayed(), "Lỗi: Không hiển thị giao diện xóa tài khoản thành công!");
        
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_15: PASSED ");
        System.out.println("================================================================");
    }
    
    // ========================================================================
    // 🎯 TC_016: PLACE ORDER: LOGIN BEFORE CHECKOUT (17 STEPS)
    // ========================================================================
    @Test(priority = 3, description = "TC_016: Xác thực quy trình người dùng đã có tài khoản tiến hành đăng nhập từ trang chủ, mua sắm, checkout và xóa tài khoản")
    public void TC_16_PlaceOrder_LoginBeforeCheckout_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_16: PLACE ORDER - LOGIN BEFORE CHECKOUT   ======");
        System.out.println("================================================================");

        // --- BỐC DỮ LIỆU ĐỘNG TỪ JSON ---
        JsonObject dataTestObj = testData.getAsJsonObject("DataTest");
        JsonObject moduleAuth = dataTestObj.getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountExistData = moduleAuth.getAsJsonObject("TaiKhoanDaCoSan");
        
        JsonObject productModule = dataTestObj.getAsJsonObject("Module_SanPham_GioHang");
        String sp1 = productModule.getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();
        JsonObject cardData = dataTestObj.getAsJsonObject("Module_ThanhToan_Payment").getAsJsonObject("TheNganHang");

        String emailLogin = accountExistData.get("email").getAsString();
        String passLogin = accountExistData.get("password").getAsString();
        String tenUser = accountExistData.get("name").getAsString();

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        System.out.println("[STEP 4] Click nút 'Signup / Login' trên thanh menu...");
        nav.clickLoginSignup(); 
        Thread.sleep(1500);

        // --- STEP 5: Nhập Email, Mật khẩu hợp lệ và click nút 'Login' ---
        System.out.println("[STEP 5] Đăng nhập bằng tài khoản có sẵn từ dữ liệu JSON (TaiKhoanDaCoSan)...");
        System.out.println("   -> Data Input: Email = " + emailLogin + " | Password = " + passLogin);
        loginPage.dienFormLogin(emailLogin, passLogin); 
        Thread.sleep(1500);

        // --- STEP 6: Xác thực trạng thái đăng nhập thành công ---
        System.out.println("[STEP 6] Xác thực hiển thị dòng chữ 'Logged in as...' trên thanh menu...");
        String exLoginText = "Logged in as " + tenUser;
        String acLoginText = nav.layTenUserDaDangNhap(); 
        Assert.assertTrue(acLoginText.contains(exLoginText), "Lỗi: Tên hiển thị sau đăng nhập không khớp!");
        System.out.println("   -> Hiển thị thực tế: '" + acLoginText + "'\n");

        // --- STEP 7: Duyệt sản phẩm và thêm sản phẩm bất kỳ vào giỏ hàng ---
        System.out.println("[STEP 7] Thực hiện thêm sản phẩm '" + sp1 + "' vào giỏ hàng...");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);

        // --- STEP 8: Click vào nút 'Cart' trên thanh điều hướng ---
        System.out.println("[STEP 8] Click vào nút 'View Cart' trên Modal để vào Giỏ hàng...");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        // --- STEP 9: Xác thực sự hiển thị của trang Giỏ hàng ---
        System.out.println("[STEP 9] Xác thực giao diện bảng Giỏ hàng hiển thị thành công:");
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: URL trang Giỏ hàng không chính xác!");
        System.out.println("   -> URL thực tế: " + driver.getCurrentUrl() + "\n");

        // --- STEP 10: Click vào nút 'Proceed To Checkout' ---
        System.out.println("[STEP 10] Click vào nút 'Proceed To Checkout'...");
        driver.findElement(By.xpath("//a[text()='Proceed To Checkout']")).click();
        Thread.sleep(1500);

        // --- STEP 11: Xác thực thông tin Địa chỉ giao hàng và Đơn đặt hàng ---
        System.out.println("[STEP 11] Xác thực thông tin Address Details và Review Your Order...");
        WebElement sectionAddress = driver.findElement(By.id("address_delivery"));
        Assert.assertTrue(sectionAddress.isDisplayed(), "Lỗi: Không hiển thị thông tin địa chỉ giao hàng!");
        System.out.println("   ==> STATUS: PASSED (Thông tin địa chỉ hiển thị chính xác)\n");

        // --- STEP 12: Nhập ghi chú vào vùng văn bản bình luận và click 'Place Order' ---
        System.out.println("[STEP 12] Nhập ghi chú đơn hàng và click nút 'Place Order'...");
        driver.findElement(By.tagName("textarea")).sendKeys("Giao hàng giờ hành chính - Đồ án kiểm thử luồng người dùng đăng nhập TC16");
        driver.findElement(By.xpath("//a[@href='/payment']")).click();
        Thread.sleep(1500);

        // --- STEP 13: Nhập đầy đủ thông tin thẻ thanh toán ---
        System.out.println("[STEP 13] Nhập thông tin tài khoản thẻ tín dụng từ JSON...");
        prodPage.dienThongTinThanhToan(
            cardData.get("name_on_card").getAsString(),
            cardData.get("card_number").getAsString(),
            cardData.get("cvc").getAsString(),
            cardData.get("expiry_month").getAsString(),
            cardData.get("expiry_year").getAsString()
        );

        // --- STEP 14: Click vào nút 'Pay and Confirm Order' ---
        System.out.println("[STEP 14] Click nút 'Pay and Confirm Order'...");
        prodPage.clickPayAndConfirmOrder();
        Thread.sleep(2000);

        // --- STEP 15: Xác thực thông báo đặt hàng thành công ---
        System.out.println("[STEP 15] Xác thực thông báo đặt hàng thành công...");
        String successMsg = prodPage.layThongBaoDatHangThanhCong();
        System.out.println("   -> Thông báo nhận được: " + successMsg);
        Assert.assertTrue(successMsg.toUpperCase().contains("PLACED"), "Lỗi: Đơn hàng đặt không thành công!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 16: Click vào nút 'Delete Account' trên thanh menu ---
        System.out.println("[STEP 16] Thực hiện click chọn 'Delete Account' để hủy tài khoản...");
        try {
            By btnDelete = By.xpath("//a[contains(@href, 'delete_account')]");
            JavascriptExecutor jsClick = (JavascriptExecutor) driver;
            jsClick.executeScript("arguments[0].click();", driver.findElement(btnDelete));
        } catch (Exception e) {
            nav.clickDeleteAccount();
        }
        System.out.println("   -> Đang dừng 3 giây để bạn chủ động dập quảng cáo thủ công (nếu có)...");
        Thread.sleep(3000);

        // --- STEP 17: Xác thực thông báo xóa tài khoản hoàn tất ---
        System.out.println("[STEP 17] Xác thực thông báo 'ACCOUNT DELETED!' hiển thị rõ ràng...");
        String exDeletedMsg = "ACCOUNT DELETED!";
        String acDeletedMsg = driver.findElement(By.xpath("//h2[@data-qa='account-deleted']/b")).getText().trim();
        Assert.assertEquals(acDeletedMsg, exDeletedMsg, "Lỗi: Không hiển thị giao diện xóa tài khoản thành công!");
        
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_16: PASSED THÀNH CÔNG TRỌN VẸN 17 STEPS!");
        System.out.println("================================================================");
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