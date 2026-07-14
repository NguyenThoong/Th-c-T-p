package com.vlu.tests;

import com.google.gson.JsonObject;
import com.vlu.helpers.JsonHelper;
import com.vlu.pages.LoginPage;
import com.vlu.pages.NavigationPage;
import com.vlu.pages.ProductPage;
import com.vlu.pages.RegisterPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class TaoAcc {
    protected WebDriver driver;
    protected NavigationPage nav;
    protected LoginPage loginPage;
    protected RegisterPage regPage;
    protected ProductPage prodPage;
    
    // Biến lưu trữ toàn bộ dữ liệu đọc từ file JSON
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

    @Test(priority = 1, description = "Hàm chuyên dụng: Chỉ tạo tài khoản và giữ lại trên hệ thống")
    public void TC_01_ChiTaoAccount() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY SCRIPT CHUYÊN DỤNG: CHỈ TẠO TÀI KHOẢN    ======");
        System.out.println("================================================================");

        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject accountInfo = authModule.getAsJsonObject("TaiKhoanHopLe");
        JsonObject addressInfo = authModule.getAsJsonObject("DiaChiGiaoHang");

        // --- STEP 3 & 4: Xác nhận trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");

        // --- STEP 5 & 6: Vào trang Login/Signup ---
        nav.clickLoginSignup();
        Thread.sleep(1000); 

        // --- STEP 7 & 8: Nhập Name và sinh Email ngẫu nhiên ---
        String tenUser = "Nguyễn Hoàng Thông";
        String matKhau = "Thong@2026";
        String emailDocNhat = "thong.cosan2026@gmail.com"; 
        
        loginPage.dienFormSignupBanDau(tenUser, emailDocNhat);
        Thread.sleep(1000); 

        // --- STEP 9 & 10: Điền thông tin tài khoản ---
        regPage.dienThongTinTaiKhoan(
            accountInfo.get("title").getAsString(),
            matKhau,
            accountInfo.get("birth_day").getAsString(),
            accountInfo.get("birth_month").getAsString(),
            accountInfo.get("birth_year").getAsString()
        );
        Thread.sleep(1000); 

        // --- STEP 11, 12, 13: Điền địa chỉ giao hàng và Submit ---
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

        // --- STEP 14: Xác thực Đăng ký thành công màn hình ACCOUNT CREATED! ---
        String exCreatedMsg = "ACCOUNT CREATED!";
        String acCreatedMsg = regPage.layThongBaoDangKyThanhCong();
        Assert.assertEquals(acCreatedMsg, exCreatedMsg, "Lỗi: Không hiển thị trang ACCOUNT CREATED!");
        System.out.println("[XÁC THỰC] Khởi tạo tài khoản trên Database thành công!\n");

        // --- STEP 15 & 16: Bấm Continue để hoàn tất quy trình kích hoạt ---
        driver.findElement(org.openqa.selenium.By.xpath("//a[@data-qa='continue-button']")).click();
        Thread.sleep(2000); 
        
        // Xác thực xem Header đã nhận diện trạng thái Đăng nhập chưa
        String exLoginText = "Logged in as " + tenUser;
        String acLoginText = nav.layTenUserDaDangNhap();
        Assert.assertTrue(acLoginText.contains(exLoginText), "Lỗi: Hệ thống chưa đăng nhập đúng User!");

        // --- HÀM IN THÔNG TIN TÀI KHOẢN ĐỂ SỬ DỤNG LẠI ---
        System.out.println("================================================================");
        System.out.println("🎉 🎉 TẠO ACC THÀNH CÔNG! HÃY COPY DATA NÀY VÀO FILE JSON:");
        System.out.println("      -> Email   : " + emailDocNhat);
        System.out.println("      -> Password: " + matKhau);
        System.out.println("      -> Name    : " + tenUser);
        System.out.println("================================================================");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}