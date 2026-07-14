package com.vlu.tests;

import com.google.gson.JsonObject;
import com.vlu.helpers.JsonHelper;
import com.vlu.pages.LoginPage;
import com.vlu.pages.NavigationPage;
import com.vlu.pages.ProductPage;
import com.vlu.pages.RegisterPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class AutomationTest {
    protected WebDriver driver;
    protected NavigationPage nav;
    protected LoginPage loginPage;
    protected RegisterPage regPage;
    protected ProductPage prodPage;
    protected JsonObject testData;

    @BeforeMethod
    public void setup() {
        System.out.println(">>> Đang nạp dữ liệu từ file TestData.json...");
        testData = JsonHelper.readJson("src/test/resources/TestData.json");

        System.out.println(">>> Khởi động trình duyệt...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); 
        driver.get("https://automationexercise.com");
        
        nav = new NavigationPage(driver);
        loginPage = new LoginPage(driver);
        regPage = new RegisterPage(driver);
        prodPage = new ProductPage(driver);
    }

    // ========================================================================
    // 🎯 TC_001: REGISTER USER & DELETE ACCOUNT (Tự tạo tự xóa độc lập)
    // ========================================================================
    @Test(priority = 1, description = "TC_001: Quy trình đăng ký và xóa tài khoản sử dụng dữ liệu JSON")
    public void TC_01_RegisterUser_FullSteps() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("====== CHẠY TC_01: REGISTER & DELETE USER (ĐỌC DATA JSON) ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountInfo = authModule.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressInfo = authModule.getAsJsonObject("DiaChiGiaoHang");

        // --- STEP 3 & 4: Xác nhận trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 3 & 4] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Tiêu đề chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 5 & 6: Vào trang Login/Signup ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 
        System.out.println("[STEP 5 & 6] Điều hướng sang trang Login/Signup thành công.\n");

        // --- STEP 7 & 8: Nhập Name và Email bốc từ file JSON ---
        String tenUser = accountInfo.get("name").getAsString();
        String emailDocNhat = "thong.vlu" + System.currentTimeMillis() + "@gmail.com"; 
        loginPage.dienFormSignupBanDau(tenUser, emailDocNhat);
        Thread.sleep(1000); 
        System.out.println("[STEP 7 & 8] Nhập thông tin đăng ký ban đầu thành công.");
        System.out.println("   -> Data Input: Name = " + tenUser + " | Email = " + emailDocNhat + "\n");

        // --- STEP 9 & 10: Điền thông tin tài khoản lấy từ JSON ---
        regPage.dienThongTinTaiKhoan(
            accountInfo.get("title").getAsString(),
            accountInfo.get("password").getAsString(),
            accountInfo.get("birth_day").getAsString(),
            accountInfo.get("birth_month").getAsString(),
            accountInfo.get("birth_year").getAsString()
        );
        Thread.sleep(1000); 
        System.out.println("[STEP 9 & 10] Điền thông tin 'Enter Account Information' thành công.\n");

        // --- STEP 11, 12, 13: Điền địa chỉ giao hàng lấy từ JSON ---
        regPage.dienThongTinDiaChi(
            addressInfo.get("first_name").getAsString(),
            addressInfo.get("last_name").getAsString(),
            addressInfo.get("company").getAsString(),
            addressInfo.get("address1").getAsString(),
            addressInfo.get("address2").getAsString(),
            addressInfo.get("country").getAsString(),
            addressInfo.get("state").getAsString(),
            addressInfo.get("city").getAsString(),
            addressInfo.get("zipcode").getAsString(),
            addressInfo.get("mobile_number").getAsString()
        );
        Thread.sleep(1000); 
        System.out.println("[STEP 11, 12, 13] Điền thông tin địa chỉ và nhấn nút 'Create Account' thành công.\n");

        // --- STEP 14: Xác thực Đăng ký thành công ---
        String exCreatedMsg = "ACCOUNT CREATED!";
        String acCreatedMsg = regPage.layThongBaoDangKyThanhCong();
        System.out.println("[STEP 14] Xác thực hiển thị thông báo tạo tài khoản:");
        System.out.println("   -> Expected Result: '" + exCreatedMsg + "'");
        System.out.println("   -> Actual Result  : '" + acCreatedMsg + "'");
        Assert.assertEquals(acCreatedMsg, exCreatedMsg);
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 15 & 16: Bấm Continue và xác nhận tên đăng nhập hợp lệ ---
        driver.findElement(By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(2000); 
        
        String exLoginText = "Logged in as " + tenUser;
        String acLoginText = nav.layTenUserDaDangNhap();
        System.out.println("[STEP 15 & 16] Xác thực trạng thái đăng nhập trên Header:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exLoginText + "'");
        System.out.println("   -> Actual Result  : '" + acLoginText + "'");
        Assert.assertTrue(acLoginText.contains(exLoginText), "Lỗi: Không khớp tên User đã đăng nhập!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 17 & 18: Bấm Delete Account và kết thúc ---
        driver.findElement(By.xpath("//a[contains(@href,'delete_account')]")).click();
        Thread.sleep(2000); 
        
        String exDeletedMsg = "ACCOUNT DELETED!";
        String acDeletedMsg = driver.findElement(By.xpath("//h2[@data-qa='account-deleted']/b")).getText().trim();
        System.out.println("[STEP 17 & 18] Xác thực xóa tài khoản:");
        System.out.println("   -> Expected Result: '" + exDeletedMsg + "'");
        System.out.println("   -> Actual Result  : '" + acDeletedMsg + "'");
        Assert.assertEquals(acDeletedMsg, exDeletedMsg);
        System.out.println("   ==> STATUS: PASSED\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_01: PASSED THÀNH CÔNG 100%");
        System.out.println("================================================================");
    }

    // ========================================================================
    // 🎯 TC_004: LOGOUT USER (Chạy thứ 2 để tận dụng acc mồi còn sống)
    // ========================================================================
    @Test(priority = 2, description = "TC_004: Xác thực quy trình đăng xuất tài khoản và hủy phiên làm việc thành công")
    public void TC_04_LogoutUser_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_04: LOGOUT USER (CHÍNH THỨC)          ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject coSanInfo = authModule.getAsJsonObject("TaiKhoanDaCoSan");
        
        String emailLogin = coSanInfo.get("email").getAsString();
        String passLogin = coSanInfo.get("password").getAsString();
        String tenUser = coSanInfo.get("name").getAsString();

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Tiêu đề chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 
        System.out.println("[STEP 4] Điều hướng sang trang Đăng ký/Đăng nhập thành công.\n");

        // --- STEP 5: Kiểm tra sự hiển thị của tiêu đề form đăng nhập ---
        String exLoginTitle = "Login to your account";
        String acLoginTitle = loginPage.layTieuDeFormLogin();
        System.out.println("[STEP 5] Xác thực hiển thị tiêu đề form đăng nhập:");
        Assert.assertEquals(acLoginTitle, exLoginTitle, "Lỗi: Tiêu đề form Login hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 6 & 7: Nhập Email, Mật khẩu chính xác và click nút 'Login' ---
        System.out.println("[STEP 6 & 7] Thực hiện điền form đăng nhập:");
        System.out.println("   -> Data Input: Email = " + emailLogin);
        loginPage.dienFormLogin(emailLogin, passLogin);
        Thread.sleep(1000); 
        System.out.println("   -> Đã điền và nhấn nút 'Login'.\n");

        // --- STEP 8: Xác thực trạng thái đăng nhập thành công ---
        String exLoginText = "Logged in as " + tenUser;
        String acLoginText = nav.layTenUserDaDangNhap();
        System.out.println("[STEP 8] Xác thực trạng thái đăng nhập trên Header:");
        Assert.assertTrue(acLoginText.contains(exLoginText), "Lỗi: Tên hiển thị sau đăng nhập không khớp!");
        System.out.println("   ==> STATUS: PASSED (Hiển thị đúng: " + acLoginText + ")\n");

        // --- STEP 9: Click vào nút 'Logout' ---
        System.out.println("[STEP 9] Tiến hành hủy phiên làm việc, nhấn nút 'Logout'...");
        nav.clickLogout();
        Thread.sleep(1500); 

        // --- STEP 10: Xác thực chuyển hướng sau đăng xuất ---
        System.out.println("[STEP 10] Xác thực chuyển hướng sau đăng xuất:");
        String currentUrl = driver.getCurrentUrl();
        System.out.println("   -> Actual URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("/login"), "Lỗi: URL sau logout không chứa đường dẫn '/login'!");
        
        String acAfterLogoutTitle = loginPage.layTieuDeFormLogin();
        System.out.println("   -> Form Login tái hiển thị: '" + acAfterLogoutTitle + "'");
        Assert.assertEquals(acAfterLogoutTitle, "Login to your account", "Lỗi: Form đăng nhập không tái hiển thị!");
        System.out.println("   ==> STATUS: PASSED (URL chứa /login và form hiển thị sạch sẽ)\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_04: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }

    // ========================================================================
    // 🎯 TC_005: REGISTER USER WITH EXISTING EMAIL (Tận dụng tiếp acc mồi)
    // ========================================================================
    @Test(priority = 3, description = "TC_005: Xác thực hệ thống chặn quy trình đăng ký khi trùng địa chỉ email")
    public void TC_05_RegisterExistingEmail_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_05: REGISTER EXISTING EMAIL (CHÍNH THỨC) ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject coSanInfo = authModule.getAsJsonObject("TaiKhoanDaCoSan");
        
        String tenUser = coSanInfo.get("name").getAsString();
        String emailDaTonTai = coSanInfo.get("email").getAsString(); 

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 
        System.out.println("[STEP 4] Điều hướng sang trang Đăng ký/Đăng nhập thành công.\n");

        // --- STEP 5: Kiểm tra sự hiển thị của tiêu đề form đăng ký ---
        String exSignupTitle = "New User Signup!";
        String acSignupTitle = loginPage.layTieuDeFormSignup();
        System.out.println("[STEP 5] Xác thực hiển thị tiêu đề form đăng ký:");
        Assert.assertEquals(acSignupTitle, exSignupTitle, "Lỗi: Tiêu đề form Đăng ký hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 6 & 7: Nhập Tên và địa chỉ Email ĐÃ TỒN TẠI trước đó rồi nhấn 'Signup' ---
        System.out.println("[STEP 6 & 7] Thực hiện cố tình điền Email trùng lặp:");
        System.out.println("   -> Data Input: Name = " + tenUser + " | Email = " + emailDaTonTai);
        loginPage.dienFormSignupBanDau(tenUser, emailDaTonTai);
        Thread.sleep(1500); 
        System.out.println("   -> Hệ thống từ chối tạo tài khoản trùng lặp và giữ nguyên form hiện tại.\n");

        // --- STEP 8: Xác thực thông báo lỗi hiển thị (Email Address already exist!) ---
        String exErrorMsg = "Email Address already exist!";
        String acErrorMsg = loginPage.layLoiTrungEmail(); 
        System.out.println("[STEP 8] Xác thực thông báo lỗi trùng lặp hiển thị:");
        System.out.println("   -> Expected Result: '" + exErrorMsg + "'");
        System.out.println("   -> Actual Result  : '" + acErrorMsg + "'");
        Assert.assertEquals(acErrorMsg, exErrorMsg, "Lỗi: Nội dung thông báo lỗi trùng Email hiển thị không đúng!");
        System.out.println("   ==> STATUS: PASSED (Hiển thị chữ màu đỏ cảnh báo thành công)\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_05: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }

    // ========================================================================
    // 🎯 TC_002: LOGIN USER WITH CORRECT INFO & DELETE ACCOUNT (Xóa dọn dẹp cuối suite)
    // ========================================================================
    @Test(priority = 4, description = "TC_002: Quy trình đăng nhập và xóa tài khoản sử dụng dữ liệu JSON")
    public void TC_02_LoginUserCorrectInfo() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_02: LOGIN USER & DELETE ACCOUNT       ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject coSanInfo = authModule.getAsJsonObject("TaiKhoanDaCoSan");
        
        String emailLogin = coSanInfo.get("email").getAsString();
        String passLogin = coSanInfo.get("password").getAsString();
        String tenUser = coSanInfo.get("name").getAsString();

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 
        System.out.println("[STEP 4] Điều hướng sang trang Đăng ký/Đăng nhập thành công.\n");

        // --- STEP 5: Kiểm tra sự hiển thị của tiêu đề form đăng nhập ---
        String exLoginTitle = "Login to your account";
        String acLoginTitle = loginPage.layTieuDeFormLogin();
        System.out.println("[STEP 5] Xác thực hiển thị tiêu đề form đăng nhập:");
        Assert.assertEquals(acLoginTitle, exLoginTitle, "Lỗi: Tiêu đề form Login hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 6 & 7: Nhập Email, Mật khẩu chính xác và click nút 'Login' ---
        System.out.println("[STEP 6 & 7] Thực hiện điền form đăng nhập:");
        System.out.println("   -> Data Input: Email = " + emailLogin + " | Password = " + passLogin);
        loginPage.dienFormLogin(emailLogin, passLogin);
        Thread.sleep(1000); 
        System.out.println("   -> Đã điền và nhấn nút 'Login'.\n");

        // --- STEP 8: Xác thực trạng thái đăng nhập thành công ---
        String exLoginText = "Logged in as " + tenUser;
        String acLoginText = nav.layTenUserDaDangNhap();
        System.out.println("[STEP 8] Xác thực trạng thái đăng nhập thành công trên Header:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exLoginText + "'");
        System.out.println("   -> Actual Result  : '" + acLoginText + "'");
        Assert.assertTrue(acLoginText.contains(exLoginText), "Lỗi: Tên hiển thị sau đăng nhập không khớp!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 9: Click vào nút 'Delete Account' ---
        driver.findElement(By.xpath("//a[contains(@href,'delete_account')]")).click();
        Thread.sleep(1000); 
        System.out.println("[STEP 9] Nhấn nút 'Delete Account' thành công.\n");

        // --- STEP 10: Xác thực thông báo xóa tài khoản thành công ---
        String exDeletedMsg = "ACCOUNT DELETED!";
        String acDeletedMsg = driver.findElement(By.xpath("//h2[@data-qa='account-deleted']/b")).getText().trim();
        System.out.println("[STEP 10] Xác thực thông báo xóa tài khoản hiển thị:");
        System.out.println("   -> Expected Result: '" + exDeletedMsg + "'");
        System.out.println("   -> Actual Result  : '" + acDeletedMsg + "'");
        Assert.assertEquals(acDeletedMsg, exDeletedMsg, "Lỗi: Không hiển thị giao diện xóa tài khoản thành công!");
        System.out.println("   ==> STATUS: PASSED\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_02: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }

    // ========================================================================
    // 🎯 TC_003: LOGIN USER WITH INCORRECT INFO (Luồng bắt lỗi độc lập)
    // ========================================================================
    @Test(priority = 5, description = "TC_003: Xác thực hệ thống từ chối quyền truy cập khi đăng nhập sai thông tin")
    public void TC_03_LoginUserIncorrectInfo() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======   CHẠY TC_03: LOGIN USER INCORRECT INFO (CHÍNH THỨC) ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject saiInfo = authModule.getAsJsonObject("TaiKhoanSai");
        
        String emailSai = saiInfo.get("email_chua_ky_so").getAsString(); 
        String passSai = saiInfo.get("password_sai").getAsString();     

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề trang chủ ---
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        Assert.assertTrue(acTitle.contains("Automation Exercise"), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 4: Click vào nút 'Signup / Login' ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 
        System.out.println("[STEP 4] Điều hướng sang trang Đăng ký/Đăng nhập thành công.\n");

        // --- STEP 5: Kiểm tra sự hiển thị của tiêu đề form đăng nhập ---
        String acLoginTitle = loginPage.layTieuDeFormLogin();
        System.out.println("[STEP 5] Xác thực hiển thị tiêu đề form đăng nhập:");
        Assert.assertEquals(acLoginTitle, "Login to your account", "Lỗi: Tiêu đề form Login hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 6 & 7: Nhập Email và Mật khẩu KHÔNG chính xác và bấm nút 'Login' ---
        System.out.println("[STEP 6 & 7] Thực hiện điền form đăng nhập SAI:");
        System.out.println("   -> Data Input: Email = " + emailSai + " | Password = " + passSai);
        loginPage.dienFormLogin(emailSai, passSai);
        Thread.sleep(1500); 
        System.out.println("   -> Hệ thống xử lý từ chối quyền truy cập và giữ người dùng ở lại.\n");

        // --- STEP 8: Xác thực thông báo lỗi hiển thị (Chữ màu đỏ) ---
        String exErrorMsg = "Your email or password is incorrect!";
        String acErrorMsg = loginPage.layLoiDangNhapSai();
        System.out.println("[STEP 8] Xác thực thông báo lỗi hiển thị:");
        System.out.println("   -> Expected Result: '" + exErrorMsg + "'");
        System.out.println("   -> Actual Result  : '" + acErrorMsg + "'");
        Assert.assertEquals(acErrorMsg, exErrorMsg, "Lỗi: Thông báo lỗi đăng nhập hiển thị sai hoặc không xuất hiện!");
        System.out.println("   ==> STATUS: PASSED\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_03: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}