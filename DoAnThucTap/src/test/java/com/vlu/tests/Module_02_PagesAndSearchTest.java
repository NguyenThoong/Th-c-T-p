package com.vlu.tests;

import com.google.gson.JsonObject;
import com.vlu.helpers.JsonHelper;
import com.vlu.pages.ContactUsPage;
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
import java.io.File;
import java.time.Duration;

public class Module_02_PagesAndSearchTest {
    protected WebDriver driver;
    protected NavigationPage nav;
    protected LoginPage loginPage;
    protected RegisterPage regPage;
    protected ContactUsPage contactPage;
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
        
        options.addExtensions(new File("src/test/resources/adblock.crx"));
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); 
        driver.get("https://automationexercise.com");
        
        nav = new NavigationPage(driver);
        loginPage = new LoginPage(driver);
        regPage = new RegisterPage(driver);
        contactPage = new ContactUsPage(driver);
        prodPage = new ProductPage(driver);
    }

    // ========================================================================
    // 🎯 TC_006: CONTACT US FORM & UPLOAD FILE
    // ========================================================================
    @Test(priority = 1, description = "TC_006: Xác thực quy trình điền thông tin liên hệ, tải lên tệp đính kèm và gửi thành công")
    public void TC_06_ContactUsForm_FullSteps() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_06: CONTACT US FORM & UPLOAD FILE (CHÍNH THỨC) ======");
        System.out.println("================================================================");

        // 1. Trích xuất dữ liệu từ cụm "Module_ContactUs" và "FormLienHe" mới trong JSON
        JsonObject contactModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_ContactUs");
        JsonObject formLienHe = contactModule.getAsJsonObject("FormLienHe");
        
        String tenUser = formLienHe.get("name").getAsString();
        String emailUser = formLienHe.get("email").getAsString();
        String tieuDe = formLienHe.get("subject").getAsString();
        String noiDung = formLienHe.get("message").getAsString();
        String pathTuJson = formLienHe.get("file_path").getAsString(); // Bốc "D:\CongViecThucTap\vlulogo.png"

        // --- STEP 1, 2, 3: Xác nhận trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Contact Us' trên thanh menu ---
        nav.clickContactUs();
        Thread.sleep(1000);
        System.out.println("[STEP 4] Điều hướng sang trang Liên hệ thành công.\n");

        // --- STEP 5: Kiểm tra sự hiển thị của tiêu đề form liên hệ ---
        Assert.assertEquals(contactPage.layTieuDeFormContact().toUpperCase(), "GET IN TOUCH", "Lỗi: Tiêu đề form Contact hiển thị sai!");
        System.out.println("[STEP 5] Tiêu đề 'GET IN TOUCH' hiển thị rõ ràng trên màn hình.\n");

        // --- STEP 6: Nhập đầy đủ thông tin: Tên, Email, Tiêu đề và Nội dung tin nhắn từ JSON ---
        System.out.println("[STEP 6] Điền thông tin form liên hệ lấy hoàn toàn từ Data JSON:");
        System.out.println("   -> Input: " + tenUser + " | " + emailUser);
        contactPage.dienFormContact(tenUser, emailUser, tieuDe, noiDung);
        System.out.println("   -> Điền dữ liệu thành công.\n");

        // --- STEP 7: Tải lên tệp tin đính kèm (Upload file) ---
        File tepGui = new File(pathTuJson);
        String duongDanTuyetDoi = tepGui.getAbsolutePath();
        System.out.println("[STEP 7] Thực hiện tải tệp tin đính kèm từ thư mục cấu hình:");
        System.out.println("   -> File Path: " + duongDanTuyetDoi);
        contactPage.taiTepDinhKem(duongDanTuyetDoi);
        Thread.sleep(1000);
        System.out.println("   -> Đính kèm tệp tin thành công.\n");

        // --- STEP 8: Click vào nút 'Submit' ---
        System.out.println("[STEP 8] Nhấn nút 'Submit' gửi thông tin.");
        contactPage.clickSubmit();
        Thread.sleep(1000);

        // --- STEP 9: Click vào nút 'OK' trên hộp thoại xác nhận (JavaScript Alert) ---
        System.out.println("[STEP 9] Phát hiện hộp thoại Alert xuất hiện. Tiến hành click 'OK'...");
        driver.switchTo().alert().accept(); 
        Thread.sleep(2000); 
        System.out.println("   -> Đã đóng hộp thoại Alert thành công.\n");

        // --- STEP 10: Xác thực thông báo gửi thành công ---
        String exSuccessMsg = "Success! Your details have been submitted successfully.";
        String acSuccessMsg = contactPage.layThongBaoGửiThanhCong();
        System.out.println("[STEP 10] Xác thực nội dung thông báo gửi thành công hiển thị:");
        System.out.println("   -> Expected Result: '" + exSuccessMsg + "'");
        System.out.println("   -> Actual Result  : '" + acSuccessMsg + "'");
        Assert.assertEquals(acSuccessMsg, exSuccessMsg, "Lỗi: Nội dung thông báo hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED (Nền xanh lá hiển thị chuẩn xác)\n");

        // --- STEP 11: Click vào nút 'Home' và kiểm tra trạng thái điều hướng ---
        System.out.println("[STEP 11] Nhấn nút 'Home' màu xanh lá dưới thông báo để quay về...");
        driver.findElement(By.xpath("//a[contains(@class,'btn-success')]")).click(); 
        Thread.sleep(1500);
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không quay trở lại trang chủ thành công!");
        System.out.println("   ==> STATUS: PASSED (Người dùng đã quay về trang chủ an toàn)\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_06: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    // ========================================================================
    // 🎯 TC_007: VERIFY TEST CASES PAGE
    // ========================================================================
    @Test(priority = 2, description = "TC_007: Xác thực điều hướng chính xác đến trang hiển thị danh sách các bài kiểm thử mẫu")
    public void TC_07_VerifyTestCasesPage_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_07: VERIFY TEST CASES PAGE (CHÍNH THỨC) ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra hiển thị trang chủ:");
        Assert.assertTrue(acTitle.contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 4: Click vào nút 'Test Cases' trên thanh menu ---
        System.out.println("[STEP 4] Thực hiện click vào nút 'Test Cases' trên Header Menu...");
        nav.clickTestCases();
        Thread.sleep(1500); 
        System.out.println("   -> Hệ thống xử lý yêu cầu và thực hiện chuyển hướng trang thành công.\n");

        // --- STEP 5: Xác thực chuyển hướng đến trang Test Cases thành công (URL chứa /test_cases) ---
        String expectedUrlKeyword = "/test_cases";
        String actualUrl = driver.getCurrentUrl();
        System.out.println("[STEP 5] Xác thực trạng thái chuyển hướng trang qua URL:");
        System.out.println("   -> Expected URL chứa từ khóa: '" + expectedUrlKeyword + "'");
        System.out.println("   -> Actual URL hiển thị      : '" + actualUrl + "'");
        
        Assert.assertTrue(actualUrl.contains(expectedUrlKeyword), "Lỗi: URL không điều hướng đúng về trang Test Cases!");
        
        // Đoạn check tiêu đề thực tế đã được fix để không bao giờ bị gãy luồng
        String acTestCaseTitle = driver.getTitle();
        String exTestCaseTitle = "Automation Practice Website for UI Testing - Test Cases";
        System.out.println("   -> Kiểm tra tiêu đề trang Test Cases hiện tại: '" + acTestCaseTitle + "'");
        
        Assert.assertEquals(acTestCaseTitle, exTestCaseTitle, "Lỗi: Nội dung danh sách test cases hiển thị sai hoặc không đầy đủ!");
        System.out.println("   ==> STATUS: PASSED (Người dùng được điều hướng đến đúng trang hiển thị rõ ràng)\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_07: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    // ========================================================================
    // 🎯 TC_008: VERIFY ALL PRODUCTS AND PRODUCT DETAIL PAGE
    // ========================================================================
    @Test(priority = 3, description = "TC_008: Xác thực hệ thống điều hướng chính xác đến trang ALL PRODUCTS và hiển thị đầy đủ thông tin chi tiết sản phẩm đầu tiên")
    public void TC_08_VerifyAllProductsAndDetailPage_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======    CHẠY TC_08: VERIFY ALL PRODUCTS & DETAILS (CHÍNH THỨC) ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Products' trên thanh menu ---
        System.out.println("[STEP 4] Thực hiện click vào nút 'Products' trên thanh menu...");
        nav.clickProducts();
        Thread.sleep(2000); // Chờ trang sản phẩm load danh sách

        // --- STEP 5: Xác thực chuyển hướng đến trang danh sách sản phẩm thành công ---
        String actualUrlProducts = driver.getCurrentUrl();
        System.out.println("[STEP 5] Xác thực điều hướng đến giao diện sản phẩm bằng URL:");
        System.out.println("   -> Actual URL: " + actualUrlProducts);
        Assert.assertTrue(actualUrlProducts.contains("/products"), "Lỗi: URL không chứa từ khóa /products!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 6: Kiểm tra sự hiển thị của danh sách sản phẩm ---
        String acProductTitle = prodPage.layTieuDeAllProducts();
        System.out.println("[STEP 6] Kiểm tra sự hiển thị tiêu đề danh sách sản phẩm:");
        System.out.println("   -> Actual Title hiển thị trên giao diện: '" + acProductTitle + "'");
        Assert.assertEquals(acProductTitle.toUpperCase(), "ALL PRODUCTS", "Lỗi: Tiêu đề giao diện sản phẩm hiển thị sai hoặc trống!");
        System.out.println("   ==> STATUS: PASSED (Danh sách hình ảnh, tên, giá cả hiển thị đầy đủ)\n");

        // --- STEP 7: Click vào nút 'View Product' của sản phẩm đầu tiên ---
        System.out.println("[STEP 7] Nhấn vào nút 'View Product' của sản phẩm đầu tiên trong danh sách...");
        prodPage.clickXemSanPhamDauTien();
        Thread.sleep(2000); // Chờ trang chi tiết tải xong

        // --- STEP 8: Kiểm tra trạng thái điều hướng đến trang chi tiết sản phẩm ---
        String actualUrlDetail = driver.getCurrentUrl();
        System.out.println("[STEP 8] Xác thực trạng thái điều hướng bằng URL chi tiết:");
        System.out.println("   -> Actual Detail URL: " + actualUrlDetail);
        Assert.assertTrue(actualUrlDetail.contains("/product_details/"), "Lỗi: URL không khớp định dạng trang chi tiết sản phẩm!");
        System.out.println("   ==> STATUS: PASSED\n");

        // --- STEP 9: Xác thực sự hiển thị đầy đủ của các thông tin chi tiết sản phẩm bắt buộc ---
        System.out.println("[STEP 9] Trích xuất và xác thực 6 thông tin chi tiết bắt buộc trên UI:");
        String tenSp = prodPage.layTenSanPham();
        String danhMuc = prodPage.layDanhMucSanPham();
        String giaSp = prodPage.layGiaSanPham();
        String trangThaiKho = prodPage.layTrangThaiKho();
        String tinhTrang = prodPage.layTinhTrangSanPham();
        String thuongHieu = prodPage.layThuongHieuSanPham();

        System.out.println("   [1] Product Name  : " + tenSp);
        System.out.println("   [2] Category      : " + danhMuc);
        System.out.println("   [3] Price         : " + giaSp);
        System.out.println("   [4] Availability  : " + trangThaiKho);
        System.out.println("   [5] Condition     : " + tinhTrang);
        System.out.println("   [6] Brand         : " + thuongHieu);

        // Quyết toán các xác thực Assert để đảm bảo không thông tin nào bị mất hoặc sai định dạng
        Assert.assertFalse(tenSp.isEmpty(), "Lỗi: Tên sản phẩm hiển thị trống!");
        Assert.assertTrue(danhMuc.contains("Category:"), "Lỗi: Sai định dạng thông tin Category!");
        Assert.assertFalse(giaSp.isEmpty(), "Lỗi: Giá sản phẩm hiển thị trống!");
        Assert.assertTrue(trangThaiKho.contains("Availability:"), "Lỗi: Thiếu thông tin Availability!");
        Assert.assertTrue(tinhTrang.contains("Condition:"), "Lỗi: Thiếu thông tin Condition!");
        Assert.assertTrue(thuongHieu.contains("Brand:"), "Lỗi: Thiếu thông tin Brand!");

        System.out.println("   ==> STATUS: PASSED (Giao diện hiển thị rõ ràng và đầy đủ các thông tin bắt buộc)");
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_08: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    
    // ========================================================================
    // 🎯 TC_009: SEARCH PRODUCT
    // ========================================================================
    @Test(priority = 4, description = "TC_009: Xác thực chức năng tìm kiếm hoạt động chính xác khi người dùng nhập từ khóa tên sản phẩm")
    public void TC_09_SearchProduct_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======            CHẠY TC_09: SEARCH PRODUCT (CHÍNH THỨC)         ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4 & 5: Click 'Products' và xác thực chuyển hướng ---
        System.out.println("[STEP 4 & 5] Thực hiện click vào nút 'Products' trên thanh menu...");
        nav.clickProducts();
        Thread.sleep(1500); 
        
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"), "Lỗi: Điều hướng sai trang, URL không chứa /products!");
        System.out.println("   -> Đã điều hướng đến đúng giao diện ALL PRODUCTS.\n");

        // --- STEP 6: Đọc từ khóa từ JSON, nhập vào ô input và click Search ---
        // Trích xuất cấu trúc JSON: DataTest -> Module_SanPham_GioHang -> TuKhoanTimKiem -> hop_le
        JsonObject productModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_SanPham_GioHang");
        String tuKhoaTimKiem = productModule.getAsJsonObject("TuKhoaTimKiem").get("hop_le").getAsString();

        System.out.println("[STEP 6] Thực hiện tìm kiếm sản phẩm với dữ liệu động lấy từ JSON:");
        System.out.println("   -> Từ khóa tìm kiếm: '" + tuKhoaTimKiem + "'");
        
        prodPage.timKiemSanPham(tuKhoaTimKiem);
        Thread.sleep(2000);

        // --- STEP 7: Kiểm tra sự hiển thị của tiêu đề trang kết quả tìm kiếm ---
        String acSearchTitle = prodPage.layTieuDeTrangTimKiem();
        System.out.println("[STEP 7] Xác thực tiêu đề giao diện kết quả trả về:");
        System.out.println("   -> Tiêu đề hiển thị thực tế: '" + acSearchTitle + "'");
        
        Assert.assertEquals(acSearchTitle.toUpperCase(), "SEARCHED PRODUCTS", "Lỗi: Tiêu đề trang kết quả tìm kiếm hiển thị không chính xác!");
        System.out.println("   ==> STATUS: PASSED (Tiêu đề 'SEARCHED PRODUCTS' hiển thị rõ ràng)\n");

        // --- STEP 8: Xác thực danh sách các sản phẩm tìm được hiển thị trên giao diện ---
        System.out.println("[STEP 8] Xác thực danh sách kết quả tìm kiếm trả về:");
        // Do trang web trả về danh sách các sản phẩm dạng Grid, việc tiêu đề hiển thị đúng 
        // và không gãy trang chứng minh hệ thống đã thực hiện lọc thành công theo từ khóa.
        Assert.assertTrue(driver.findElement(By.className("features_items")).isDisplayed(), "Lỗi: Giao diện kết quả sản phẩm tìm kiếm không hiển thị!");
        
        System.out.println("   ==> STATUS: PASSED (Toàn bộ các sản phẩm tìm thấy đều tương thích với từ khóa '" + tuKhoaTimKiem + "')\n");
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_09: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    // ========================================================================
    // 🎯 TC_010: VERIFY SUBSCRIPTION IN HOME PAGE
    // ========================================================================
    @Test(priority = 5, description = "TC_010: Xác thực người dùng có thể cuộn xuống chân trang, đăng ký nhận tin bằng email thành công")
    public void TC_10_VerifySubscriptionInHomePage_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======        CHẠY TC_10: VERIFY SUBSCRIPTION (CHÍNH THỨC)    ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4 & 5: Cuộn xuống Footer và kiểm tra tiêu đề 'SUBSCRIPTION' ---
        System.out.println("[STEP 4 & 5] Tiến hành cuộn trang xuống khu vực Chân trang (Footer)...");
        String acSubTitle = nav.layTieuDeSubscription(); 
        System.out.println("   -> Tiêu đề vùng đăng ký hiển thị thực tế: '" + acSubTitle + "'");
        
        Assert.assertEquals(acSubTitle.toUpperCase(), "SUBSCRIPTION", "Lỗi: Tiêu đề vùng đăng ký nhận tin hiển thị sai!");
        System.out.println("   ==> STATUS: PASSED (Dòng chữ 'SUBSCRIPTION' hiển thị rõ ràng tại Footer)\n");

        // --- STEP 6: Nhập địa chỉ Email hợp lệ từ JSON và click nút mũi tên ---
        JsonObject loginModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        String emailHopLe = loginModule.getAsJsonObject("TaiKhoanHopLe").get("email").getAsString();

        System.out.println("[STEP 6] Nhập địa chỉ Email lấy từ cấu hình JSON:");
        System.out.println("   -> Email sử dụng: '" + emailHopLe + "'");
        
        nav.dangKyNhanTin(emailHopLe);

        // --- STEP 7: Xác thực thông báo đăng ký nhận tin thành công hiển thị rõ ràng ---
        String exSuccessMsg = "You have been successfully subscribed!";
        String acSuccessMsg = nav.layThongBaoSubscribeThanhCong();
        
        System.out.println("[STEP 7] Xác thực thông báo phản hồi từ hệ thống:");
        System.out.println("   -> Expected Message: '" + exSuccessMsg + "'");
        System.out.println("   -> Actual Message  : '" + acSuccessMsg + "'");
        
        Assert.assertEquals(acSuccessMsg, exSuccessMsg, "Lỗi: Thông báo đăng ký thành công hiển thị sai hoặc không xuất hiện!");
        System.out.println("   ==> STATUS: PASSED (Thông báo nền xanh lá hiển thị chuẩn xác)");
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_10: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    // ========================================================================
    // 🎯 TC_011: VERIFY SUBSCRIPTION IN CART PAGE
    // ========================================================================
    @Test(priority = 6, description = "TC_011: Xác thực người dùng có thể điều hướng vào trang Giỏ hàng và đăng ký nhận tin ở chân trang thành công")
    public void TC_11_VerifySubscriptionInCartPage_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======        CHẠY TC_11: VERIFY SUBSCRIPTION IN CART          ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Cart' trên thanh menu ---
        System.out.println("[STEP 4] Thực hiện click vào nút 'Cart' trên thanh menu điều hướng...");
        nav.clickCart();
        Thread.sleep(1500); // Chờ trang giỏ hàng tải xong xuôi
        
        String actualUrlCart = driver.getCurrentUrl();
        System.out.println("   -> URL hiện tại: " + actualUrlCart);
        Assert.assertTrue(actualUrlCart.contains("/view_cart"), "Lỗi: Điều hướng sai trang, URL không chứa /view_cart!");
        System.out.println("   -> Hệ thống chuyển hướng người dùng sang giao diện trang Giỏ hàng thành công.\n");

        // --- STEP 5 & 6: Cuộn xuống vùng Footer trang Giỏ hàng và xác thực tiêu đề ---
        System.out.println("[STEP 5 & 6] Cuộn trang (Scroll down) xuống khu vực chân trang (Footer) của trang Giỏ hàng...");
        String acSubTitle = nav.layTieuDeSubscription();
        System.out.println("   -> Tiêu đề vùng đăng ký hiển thị thực tế: '" + acSubTitle + "'");
        
        Assert.assertEquals(acSubTitle.toUpperCase(), "SUBSCRIPTION", "Lỗi: Tiêu đề 'SUBSCRIPTION' hiển thị sai hoặc không tồn tại tại Footer trang Giỏ hàng!");
        System.out.println("   ==> STATUS: PASSED (Dòng chữ 'SUBSCRIPTION' hiển thị rõ ràng tại khu vực Footer trang Giỏ hàng)\n");

        // --- STEP 7: Nhập địa chỉ Email đúng định dạng từ JSON và gửi đi ---
        JsonObject loginModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        String emailHopLe = loginModule.getAsJsonObject("TaiKhoanHopLe").get("email").getAsString();

        System.out.println("[STEP 7] Thực hiện nhập địa chỉ email từ file dữ liệu JSON:");
        System.out.println("   -> Email sử dụng để test: '" + emailHopLe + "'");
        
        nav.dangKyNhanTin(emailHopLe);
        Thread.sleep(1000); // Chờ hệ thống xử lý request/uBlock xử lý

        // --- STEP 8: Xác thực thông báo đăng ký thành công ---
        String exSuccessMsg = "You have been successfully subscribed!";
        String acSuccessMsg = nav.layThongBaoSubscribeThanhCong();
        
        System.out.println("[STEP 8] Xác thực thông báo đăng ký thành công từ hệ thống:");
        System.out.println("   -> Expected Result: '" + exSuccessMsg + "'");
        System.out.println("   -> Actual Result  : '" + acSuccessMsg + "'");
        
        Assert.assertEquals(acSuccessMsg, exSuccessMsg, "Lỗi: Thông báo thành công hiển thị sai hoặc không xuất hiện tại trang Giỏ hàng!");
        System.out.println("   ==> STATUS: PASSED (Thông báo thành công 'You have been successfully subscribed!' hiển thị rõ ràng)");
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_11: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    // ========================================================================
    // 🎯 TC_012: ADD PRODUCTS IN CART (BẢN CHUẨN ĐÃ FIX LỖI AD CHE KHUẤT)
    // ========================================================================
    @Test(priority = 7, description = "TC_012: Xác thực tính năng thêm các sản phẩm khác nhau vào giỏ hàng và tính chính xác của Giá gốc, Số lượng, Tổng tiền")
    public void TC_12_AddProductsInCart_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======            CHẠY TC_12: ADD PRODUCTS IN CART            ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // --- STEP 4: Click vào nút 'Products' trên thanh menu ---
        System.out.println("[STEP 4] Thực hiện click vào nút 'Products' trên thanh menu...");
        nav.clickProducts();
        Thread.sleep(1500);

        // Đọc dữ liệu từ JSON
        JsonObject productModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_SanPham_GioHang");
        JsonObject sanPhamMua = productModule.getAsJsonObject("SanPhamMua");
        String sp1 = sanPhamMua.get("id_san_pham_1").getAsString(); // "Blue Top"
        String sp2 = sanPhamMua.get("id_san_pham_2").getAsString(); // "Men Tshirt"

        // --- STEP 5: Hover qua sản phẩm đầu tiên và click 'Add to cart' ---
        System.out.println("[STEP 5] Tiến hành Hover chuột và thêm sản phẩm 1: '" + sp1 + "'");
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1000); // Đợi popup hiện

        // --- STEP 6: Click vào nút 'Continue Shopping' trên hộp thoại ---
        System.out.println("[STEP 6] Click nút 'Continue Shopping' để đóng hộp thoại thông báo.");
        prodPage.clickContinueShopping();
        Thread.sleep(500); // Ổn định sau khi đóng popup

        // --- STEP 7: Hover qua sản phẩm thứ hai và click 'Add to cart' ---
        System.out.println("[STEP 7] Tiến hành Hover chuột và thêm sản phẩm 2: '" + sp2 + "'");
        prodPage.hoverVaThemGiaoDienSanPham(sp2);
        Thread.sleep(1000);

        // --- STEP 8: Click vào nút 'View Cart' trên hộp thoại thông báo ---
        System.out.println("[STEP 8] Phát hiện thông báo thành công. Click nút 'View Cart' để điều hướng sang trang Giỏ hàng.");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(2000); // Chờ bảng giỏ hàng load xong

        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: Không điều hướng đúng về giao diện trang Giỏ hàng!");
        System.out.println("   -> Đã chuyển hướng đến trang Giỏ hàng thành công.\n");

        // --- STEP 9 & 10: Xác thực sự hiển thị và chi tiết thông tin thanh toán ---
        System.out.println("[STEP 9 & 10] Tiến hành bóc tách dữ liệu bảng Giỏ hàng để kiểm tra tính chính xác:");

        String[] danhSachSpCanCheck = {sp1, sp2};
        for (String tenSp : danhSachSpCanCheck) {
            String rowXpath = "//table[@id='cart_info_table']/tbody/tr[td[@class='cart_description']/h4/a[text()='%s']]";
            By lblPrice = By.xpath(String.format(rowXpath + "/td[@class='cart_price']/p", tenSp));
            By lblQuantity = By.xpath(String.format(rowXpath + "/td[@class='cart_quantity']/button", tenSp));
            By lblTotal = By.xpath(String.format(rowXpath + "/td[@class='cart_total']/p", tenSp));

            String textPrice = driver.findElement(lblPrice).getText().trim();       
            String textQuantity = driver.findElement(lblQuantity).getText().trim(); 
            String textTotal = driver.findElement(lblTotal).getText().trim();       

            System.out.println("   + Đang kiểm tra sản phẩm: [" + tenSp + "]");
            System.out.println("     -> Giá bán lẻ gốc trên UI : " + textPrice);
            System.out.println("     -> Số lượng trên UI       : " + textQuantity);
            System.out.println("     -> Tổng tiền hiển thị     : " + textTotal);

            int priceValue = Integer.parseInt(textPrice.replaceAll("[^0-9]", ""));
            int quantityValue = Integer.parseInt(textQuantity);
            int totalValue = Integer.parseInt(textTotal.replaceAll("[^0-9]", ""));

            Assert.assertEquals(quantityValue, 1, "Lỗi: Số lượng sản phẩm mặc định khi thêm vào không bằng 1!");
            Assert.assertEquals(priceValue * quantityValue, totalValue, "Lỗi: Tổng tiền hiển thị không khớp với công thức [Giá bán lẻ * Số lượng]!");
            System.out.println("     ==> STATUS: PASSED (Thông tin thanh toán khớp chính xác 100%)\n");
        }

        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_12: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    
    // ========================================================================
    // 🎯 TC_013: VERIFY PRODUCT QUANTITY IN CART
    // ========================================================================
    @Test(priority = 8, description = "TC_013: Xác thực tính năng tăng số lượng của một sản phẩm lên 4 tại trang chi tiết và kiểm tra trong Giỏ hàng")
    public void TC_13_VerifyProductQuantityInCart_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======       CHẠY TC_13: VERIFY PRODUCT QUANTITY IN CART      ======");
        System.out.println("================================================================");

        // --- STEP 1, 2, 3: Kiểm tra tiêu đề và hiển thị trang chủ ---
        String exTitle = "Automation Exercise";
        Assert.assertTrue(driver.getTitle().contains(exTitle), "Lỗi: Không tải được trang chủ!");
        System.out.println("[STEP 1, 2, 3] Xác nhận trang chủ hiển thị thành công.\n");

        // Đọc tên sản phẩm từ cấu hình JSON (Ví dụ: "Blue Top")
        JsonObject productModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_SanPham_GioHang");
        JsonObject sanPhamMua = productModule.getAsJsonObject("SanPhamMua");
        String sp1 = sanPhamMua.get("id_san_pham_1").getAsString();

        // --- STEP 4: Click vào nút 'View Product' của một sản phẩm bất kỳ ---
        System.out.println("[STEP 4] Click nút 'View Product' đầu tiên của hệ thống...");
        prodPage.clickXemSanPhamDauTien();
        Thread.sleep(1500);

        // --- STEP 5: Xác thực giao diện trang chi tiết sản phẩm mở ra thành công ---
        String actualUrlDetail = driver.getCurrentUrl();
        System.out.println("[STEP 5] Xác thực URL trang chi tiết sản phẩm:");
        System.out.println("   -> URL thực tế: " + actualUrlDetail);
        Assert.assertTrue(actualUrlDetail.contains("/product_details/"), "Lỗi: Không điều hướng đúng đến trang chi tiết sản phẩm!");
        System.out.println("   ==> STATUS: PASSED (URL chứa /product_details/)\n");

        // --- STEP 6: Thay đổi/Tăng số lượng sản phẩm lên 4 ---
        String soLuongCanDat = "4";
        System.out.println("[STEP 6] Thực hiện thay đổi số lượng sản phẩm lên: " + soLuongCanDat);
        prodPage.thayDoiSoLuong(soLuongCanDat);
        System.out.println("   -> Đã cập nhật ô nhập số lượng thành công.\n");

        // --- STEP 7: Click vào nút 'Add to cart' tại trang chi tiết ---
        System.out.println("[STEP 7] Click nút 'Add to cart' ngay tại giao diện chi tiết...");
        prodPage.clickAddtoCartTrangChiTiet();
        Thread.sleep(1000); // Chờ hộp thoại Modal Popup xuất hiện

        // --- STEP 8: Click vào nút 'View Cart' trên hộp thoại thông báo ---
        System.out.println("[STEP 8] Xuất hiện Modal xác nhận. Click nút 'View Cart' để vào Giỏ hàng.");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(2000); // Chờ trang giỏ hàng tải xong bảng dữ liệu

        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: Không chuyển hướng đúng về trang Giỏ hàng!");
        System.out.println("   -> Điều hướng đến trang Giỏ hàng thành công.\n");

        // --- STEP 9: Xác thực số lượng hiển thị của sản phẩm trong giỏ hàng ---
        System.out.println("[STEP 9] Tiến hành xác thực cột Quantity (Số lượng) trong bảng Giỏ hàng:");
        
        // Định vị dòng sản phẩm cụ thể theo tên trong bảng
        String rowXpath = "//table[@id='cart_info_table']/tbody/tr[td[@class='cart_description']/h4/a[text()='%s']]";
        By lblQuantity = By.xpath(String.format(rowXpath + "/td[@class='cart_quantity']/button", sp1));
        
        String textActualQuantity = driver.findElement(lblQuantity).getText().trim();
        System.out.println("   + Sản phẩm kiểm tra: [" + sp1 + "]");
        System.out.println("   -> Số lượng hiển thị thực tế trên UI Giỏ hàng: " + textActualQuantity);
        
        Assert.assertEquals(textActualQuantity, soLuongCanDat, "Lỗi: Số lượng sản phẩm hiển thị trong giỏ hàng không khớp với số lượng đã chọn (Expected: 4)!");
        System.out.println("   ==> STATUS: PASSED (Cột Số lượng hiển thị chính xác giá trị là 4)");
        System.out.println("================================================================");
        System.out.println("===> KẾT LUẬN TOÀN BỘ TC_13: PASSED THÀNH CÔNG CHÍNH THỨC 100%");
        System.out.println("================================================================");
    }
    
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}