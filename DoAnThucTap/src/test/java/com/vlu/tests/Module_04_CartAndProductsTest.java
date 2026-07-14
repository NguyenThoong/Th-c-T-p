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

public class Module_04_CartAndProductsTest {

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
    // 🎯 TC_017: REMOVE PRODUCTS FROM CART (7 STEPS)
    // ========================================================================
    @Test(priority = 1, description = "TC_017: Xác thực tính năng xóa sản phẩm cụ thể ra khỏi giỏ hàng")
    public void TC_17_RemoveProductsFromCart_Test() throws InterruptedException {
        System.out.println("====== CHẠY TC_17: REMOVE PRODUCTS FROM CART ======");
        JsonObject productModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_SanPham_GioHang");
        String sp1 = productModule.getAsJsonObject("SanPhamMua").get("id_san_pham_1").getAsString();

        // Step 1, 2, 3: Trang chủ hiển thị thành công
        Assert.assertTrue(driver.getTitle().contains("Automation Exercise"));

        // Step 4: Thêm sản phẩm bất kỳ vào giỏ hàng
        prodPage.hoverVaThemGiaoDienSanPham(sp1);
        Thread.sleep(1500);

        // Step 5: Click vào nút 'Cart' trên thanh điều hướng
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        // Step 6: Xác thực sự hiển thị của trang Giỏ hàng
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Lỗi: Không vào đúng trang giỏ hàng!");

        // Step 7: Click vào nút 'X' (Delete màu đỏ) tương ứng để xóa sản phẩm
        System.out.println("[STEP 7] Click nút X đỏ để xóa sản phẩm khỏi giỏ hàng...");
        driver.findElement(By.xpath("//a[@class='cart_quantity_delete']")).click();
        Thread.sleep(2000);

        // Xác thực sản phẩm đã được xóa khỏi giỏ hàng (Hiển thị thông báo Giỏ hàng trống)
        WebElement lblEmptyCart = driver.findElement(By.id("empty_cart"));
        Assert.assertTrue(lblEmptyCart.isDisplayed(), "Lỗi: Sản phẩm chưa được xóa hoặc giỏ hàng không trống!");
        System.out.println("   ==> STATUS: PASSED (Xóa sản phẩm thành công)\n");
    }
    
    // ========================================================================
    // 🎯 TC_018: VIEW CATEGORY PRODUCTS (8 STEPS) - IN LOG SO SÁNH CHI TIẾT
    // ========================================================================
    @Test(priority = 2, description = "TC_018: Xác thực tính năng menu danh mục (Category) ở bên trái hoạt động chính xác")
    public void TC_18_ViewCategoryProducts_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======          CHẠY TC_18: VIEW CATEGORY PRODUCTS        ======");
        System.out.println("================================================================");
        
        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Step 1, 2, 3: Kiểm tra hiển thị trang chủ
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        softAssert.assertTrue(acTitle.contains(exTitle), "Lỗi Step 1,2,3: Không tải được trang chủ!");
        System.out.println("   ==> Check Step 1, 2, 3 xong.\n");

        // Step 4: Xác thực thanh danh mục bên trái (Sidebar) hiển thị
        System.out.println("[STEP 4] Xác thực thanh danh mục Sidebar hiển thị:");
        boolean isCategoryDisplayed = prodPage.isThanhDanhMucHienThi();
        System.out.println("   -> Expected Result: true (Hiển thị)");
        System.out.println("   -> Actual Result  : " + isCategoryDisplayed);
        softAssert.assertTrue(isCategoryDisplayed, "Lỗi Step 4: Không tìm thấy thanh danh mục Category!");
        System.out.println("   ==> Check Step 4 xong.\n");

        // Step 5: Click vào danh mục 'Women'
        System.out.println("[STEP 5] Click vào danh mục 'Women' trên thanh menu bên trái...");
        prodPage.clickDanhMucWomen();
        Thread.sleep(1000);

        // Step 6: Click vào một danh mục con bất kỳ dưới nhóm 'Women' (Ví dụ: Dress)
        System.out.println("[STEP 6] Click chọn danh mục con 'Dress'...");
        prodPage.clickDanhMucConDress();
        Thread.sleep(1500);

        // Step 7: Xác thực giao diện trang danh mục và tiêu đề text (WOMEN - DRESS PRODUCTS)
        System.out.println("[STEP 7] Xác thực tiêu đề trang sản phẩm lọc theo danh mục:");
        String exCategoryTitle = "WOMEN - DRESS PRODUCTS";
        String acCategoryTitle = prodPage.layTieuDeTrangDanhMuc(); // Hàm này trả về chữ IN HOA
        
        System.out.println("   -> Expected Result: '" + exCategoryTitle + "'");
        System.out.println("   -> Actual Result  : '" + acCategoryTitle + "'");
        
        softAssert.assertTrue(acCategoryTitle.contains("WOMEN") && acCategoryTitle.contains("DRESS"), 
                "Lỗi Step 7: Tiêu đề trang danh mục hiển thị sai! Thực tế là: " + acCategoryTitle);
        System.out.println("   ==> Check Step 7 xong.\n");

        // Step 8: Tại sidebar bên trái, click vào một danh mục của nhóm khác (Ví dụ: Men -> Tshirts)
        System.out.println("[STEP 8] Click chuyển sang danh mục 'Men' -> 'Tshirts'...");
        prodPage.clickDanhMucMen();
        Thread.sleep(1000);
        prodPage.clickDanhMucConTshirts();
        Thread.sleep(1500);

        // Xác thực chuyển hướng sang trang sản phẩm Men thành công qua URL
        String exUrlKeyword = "/category_products/3";
        String acUrl = driver.getCurrentUrl();
        System.out.println("[STEP 8 - VERIFY URL] Xác thực URL sau khi chuyển danh mục Men:");
        System.out.println("   -> Expected Result: URL chứa '" + exUrlKeyword + "'");
        System.out.println("   -> Actual Result  : '" + acUrl + "'");
        
        softAssert.assertTrue(acUrl.contains(exUrlKeyword), "Lỗi Step 8: Không điều hướng đúng sang trang sản phẩm của Men!");
        System.out.println("   ==> Check Step 8 xong.\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC BÀI TEST THỰC THI: ĐANG TỔNG HỢP BÁO CÁO LỖI VLU...");
        System.out.println("================================================================");
        
        // Chốt hạ báo cáo kết quả lên TestNG
        softAssert.assertAll();
    }
    
    // ========================================================================
    // 🎯 TC_019: VIEW & CART BRAND PRODUCTS (8 STEPS)
    // ========================================================================
    @Test(priority = 3, description = "TC_019: Xác thực tính năng lọc và hiển thị thương hiệu (Brands) hoạt động chính xác")
    public void TC_19_ViewBrandProducts_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======            CHẠY TC_19: VIEW BRAND PRODUCTS         ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Step 1, 2, 3: Kiểm tra hiển thị trang chủ
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        softAssert.assertTrue(acTitle.contains(exTitle), "Lỗi Step 1,2,3: Không tải được trang chủ!");
        System.out.println("   ==> Check Step 1, 2, 3 xong.\n");

        // Step 4: Click vào nút 'Products' trên thanh menu
        System.out.println("[STEP 4] Click vào nút 'Products' trên thanh điều hướng...");
        nav.clickProductsMenu();
        Thread.sleep(1500);

        // Step 5: Xác thực sự xuất hiện của thanh Brands tại góc trái giao diện
        System.out.println("[STEP 5] Xác thực khu vực hiển thị Brands trên Sidebar:");
        boolean isBrandsDisplayed = prodPage.isThanhBrandsHienThi();
        System.out.println("   -> Expected Result: true (Hiển thị)");
        System.out.println("   -> Actual Result  : " + isBrandsDisplayed);
        softAssert.assertTrue(isBrandsDisplayed, "Lỗi Step 5: Không tìm thấy khu vực hiển thị Brands!");
        System.out.println("   ==> Check Step 5 xong.\n");

        // Step 6: Click chọn một tên thương hiệu bất kỳ (Ví dụ: Polo)
        System.out.println("[STEP 6] Thực hiện click chọn thương hiệu 'Polo'...");
        prodPage.clickThuongHieuPolo();
        Thread.sleep(1500);

        // Step 7: Xác thực người dùng được điều hướng đến trang thương hiệu và danh sách sản phẩm hiển thị đúng
        String exBrandUrl = "/brand_products/Polo";
        String acBrandUrl = driver.getCurrentUrl();
        System.out.println("[STEP 7 - VERIFY URL] Xác thực URL trang thương hiệu Polo:");
        System.out.println("   -> Expected Result: URL chứa '" + exBrandUrl + "'");
        System.out.println("   -> Actual Result  : '" + acBrandUrl + "'");
        softAssert.assertTrue(acBrandUrl.contains(exBrandUrl), "Lỗi Step 7: URL trang thương hiệu Polo không đúng!");

        System.out.println("[STEP 7 - VERIFY TITLE] Xác thực tiêu đề text của trang thương hiệu:");
        String exBrandTitle = "BRAND - POLO PRODUCTS";
        String acBrandTitle = prodPage.layTieuDeTrangThuongHieu();
        System.out.println("   -> Expected Result: '" + exBrandTitle + "'");
        System.out.println("   -> Actual Result  : '" + acBrandTitle + "'");
        softAssert.assertTrue(acBrandTitle.contains("BRAND") && acBrandTitle.contains("POLO"), 
                "Lỗi Step 7: Tiêu đề trang thương hiệu Polo hiển thị sai!");
        System.out.println("   ==> Check Step 7 xong.\n");

        // Step 8: Tại sidebar bên trái, click vào tên một thương hiệu khác (Ví dụ: H&M)
        System.out.println("[STEP 8] Thực hiện click chuyển đổi sang thương hiệu 'H&M'...");
        prodPage.clickThuongHieuHM();
        Thread.sleep(1500);
        
        String exHMUrl = "/brand_products/H&M";
        String acHMUrl = driver.getCurrentUrl();
        System.out.println("[STEP 8 - VERIFY URL] Xác thực URL sau khi chuyển sang thương hiệu H&M:");
        System.out.println("   -> Expected Result: URL chứa '" + exHMUrl + "'");
        System.out.println("   -> Actual Result  : '" + acHMUrl + "'");
        softAssert.assertTrue(acHMUrl.contains("/brand_products/H"), "Lỗi Step 8: Không điều hướng đúng sang trang thương hiệu H&M!");
        System.out.println("   ==> Check Step 8 xong.\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC BÀI TEST THỰC THI: ĐANG TỔNG HỢP BÁO CÁO LỖI VLU...");
        System.out.println("================================================================");
        
        // Tổng hợp báo cáo kết quả lên TestNG
        softAssert.assertAll();
    }
    
    // ========================================================================
    // 🎯 TC_020: SEARCH PRODUCTS AND VERIFY CART AFTER LOGIN (12 STEPS)
    // ========================================================================
    @Test(priority = 4, description = "TC_020: Xác thực hành vi của giỏ hàng khi khách vãng lai thực hiện tìm kiếm sản phẩm, thêm vào giỏ hàng, sau đó tiến hành đăng nhập")
    public void TC_20_SearchProductsAndVerifyCartAfterLogin_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("====== CHẠY TC_20: SEARCH PRODUCTS & VERIFY CART AFTER LOGIN ======");
        System.out.println("================================================================");
        
        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Đọc dữ liệu từ file JSON
        JsonObject authModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_DangKy_DangNhap");
        JsonObject coSanInfo = authModule.getAsJsonObject("TaiKhoanDaCoSan");
        JsonObject productModule = testData.getAsJsonObject("DataTest").getAsJsonObject("Module_SanPham_GioHang");
        String tuKhoa = productModule.getAsJsonObject("TuKhoaTimKiem").get("hop_le").getAsString();

        // Step 1, 2, 3: Kiểm tra hiển thị trang chủ
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        softAssert.assertTrue(acTitle.contains(exTitle), "Lỗi Step 1,2,3: Không tải được trang chủ!");
        System.out.println("   ==> Check Step 1, 2, 3 xong.\n");

        // Step 4: Click vào nút 'Products' trên thanh menu
        System.out.println("[STEP 4] Điều hướng sang trang Products...");
        nav.clickProductsMenu();
        Thread.sleep(1500);

        // Step 5: Nhập tên sản phẩm vào ô tìm kiếm và click nút kính lúp
        System.out.println("[STEP 5] Tiến hành tìm kiếm sản phẩm:");
        System.out.println("   -> Từ khóa nhập vào: '" + tuKhoa + "'");
        prodPage.timKiemSanPham(tuKhoa); // Gọi hàm có sẵn của bạn
        Thread.sleep(1500);

        // Step 6: Xác thực dòng chữ 'SEARCHED PRODUCTS' hiển thị rõ ràng
        System.out.println("[STEP 6] Xác thực tiêu đề kết quả tìm kiếm xuất hiện:");
        boolean isTitleSearchedDisplayed = prodPage.isTieuDeSearchedProductsHienThi();
        System.out.println("   -> Expected Result: true (Hiển thị)");
        System.out.println("   -> Actual Result  : " + isTitleSearchedDisplayed);
        softAssert.assertTrue(isTitleSearchedDisplayed, "Lỗi Step 6: Tiêu đề kết quả tìm kiếm không hiển thị!");
        System.out.println("   ==> Check Step 6 xong.\n");

        // Step 7: Thêm sản phẩm thuộc kết quả tìm kiếm vào giỏ hàng
        System.out.println("[STEP 7] Thực hiện thêm sản phẩm đầu tiên tìm thấy vào giỏ hàng...");
        prodPage.themSanPhamDauTienTimThay();
        Thread.sleep(1500);

        // Step 8: Click nút 'Cart' trên thanh điều hướng (Dùng nút View Cart trên Modal nổi)
        System.out.println("[STEP 8] Click 'View Cart' trên Modal Popup thông báo...");
        prodPage.clickViewCartTrenModal(); // Gọi hàm có sẵn của bạn
        Thread.sleep(1500);

        // Step 9: Xác thực sự hiển thị của trang Giỏ hàng
        String exCartUrl = "/view_cart";
        String acCartUrl = driver.getCurrentUrl();
        System.out.println("[STEP 9] Xác thực URL trang Giỏ hàng:");
        System.out.println("   -> Expected Result: URL chứa '" + exCartUrl + "'");
        System.out.println("   -> Actual Result  : '" + acCartUrl + "'");
        softAssert.assertTrue(acCartUrl.contains(exCartUrl), "Lỗi Step 9: Không vào đúng trang giỏ hàng!");
        System.out.println("   ==> Check Step 9 xong.\n");

        // Step 10: Click nút 'Signup / Login' và nhập tài khoản hợp lệ
        System.out.println("[STEP 10] Click chọn 'Signup / Login' để tiến hành đăng nhập đồng bộ giỏ hàng...");
        nav.clickLoginSignup();
        Thread.sleep(1000);
        loginPage.dienFormLogin(coSanInfo.get("email").getAsString(), coSanInfo.get("password").getAsString());
        Thread.sleep(1500);

        // Step 11: Click lại vào nút 'Cart' trên thanh menu
        System.out.println("[STEP 11] Quay lại trang Giỏ hàng sau khi đăng nhập thành công...");
        nav.clickCart();
        Thread.sleep(1500);

        // Step 12: Xác thực các sản phẩm trước đó trong giỏ hàng vẫn tồn tại đầy đủ
        System.out.println("[STEP 12] Xác thực số lượng dòng sản phẩm được bảo lưu trong giỏ hàng:");
        int actualProductRows = prodPage.laySoLuongDongSanPhanTrongGio();
        System.out.println("   -> Expected Result: Lớn hơn 0 dòng sản phẩm (Giỏ hàng không bị xóa trống)");
        System.out.println("   -> Actual Result  : " + actualProductRows + " dòng sản phẩm trong bảng.");
        
        softAssert.assertTrue(actualProductRows > 0, "Lỗi Step 12: Giỏ hàng bị mất sạch sản phẩm sau khi người dùng đăng nhập!");
        System.out.println("   ==> Check Step 12 xong.\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC BÀI TEST THỰC THI: ĐANG TỔNG HỢP BÁO CÁO LỖI VLU...");
        System.out.println("================================================================");
        
        softAssert.assertAll();
    }
    
    
    // ========================================================================
    // 🎯 TC_022: ADD TO CART FROM RECOMMENDED ITEMS (7 STEPS) - CHUẨN POM
    // ========================================================================
    @Test(priority = 5, description = "TC_022: Xác thực tính năng mua hàng nhanh từ danh sách sản phẩm được đề xuất ở cuối trang chủ")
    public void TC_22_AddToCartFromRecommendedItems_Test() throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("======      CHẠY TC_022: ADD TO CART FROM RECOMMENDED ITEMS   ======");
        System.out.println("================================================================");

        org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();

        // Step 1, 2, 3: Kiểm tra hiển thị trang chủ
        String exTitle = "Automation Exercise";
        String acTitle = driver.getTitle();
        System.out.println("[STEP 1, 2, 3] Kiểm tra tiêu đề trang chủ:");
        System.out.println("   -> Expected Result: Chuỗi chứa '" + exTitle + "'");
        System.out.println("   -> Actual Result  : '" + acTitle + "'");
        softAssert.assertTrue(acTitle.contains(exTitle), "Lỗi Step 1,2,3: Không tải được trang chủ!");
        System.out.println("   ==> Check Step 1, 2, 3 xong.\n");

        // Step 4: Cuộn trang xuống vùng dưới cùng (Footer) nơi chứa Recommended Items
        System.out.println("[STEP 4] Thực hiện cuộn chuột xuống cuối trang chủ bằng JavaScript...");
        prodPage.cuonXuongCuoiTrang();
        Thread.sleep(1500);

        // Step 5: Xác thực khối 'RECOMMENDED ITEMS' hiển thị rõ ràng trên màn hình
        System.out.println("[STEP 5] Xác thực khối tiêu đề 'RECOMMENDED ITEMS' hiển thị:");
        boolean isRecommendedDisplayed = prodPage.isKhoiRecommendedItemsHienThi();
        System.out.println("   -> Expected Result: true (Hiển thị ở footer)");
        System.out.println("   -> Actual Result  : " + isRecommendedDisplayed);
        softAssert.assertTrue(isRecommendedDisplayed, "Lỗi Step 5: Khu vực Recommended Items không hiển thị!");
        System.out.println("   ==> Check Step 5 xong.\n");

        // Step 6: Click vào nút 'Add To Cart' của một sản phẩm bất kỳ trong danh mục đề xuất
        System.out.println("[STEP 6] Click nút 'Add To Cart' của sản phẩm thuộc Carousel Đề xuất đang hiển thị...");
        prodPage.clickAddtoCartSanPhamDeXuat();
        Thread.sleep(1500);

        // Step 7: Click vào nút 'View Cart' trên hộp thoại và xác thực sản phẩm hiển thị trong giỏ hàng
        System.out.println("[STEP 7] Điều hướng vào trang Giỏ hàng từ Modal thông báo...");
        prodPage.clickViewCartTrenModal();
        Thread.sleep(1500);

        String exCartUrl = "/view_cart";
        String acCartUrl = driver.getCurrentUrl();
        System.out.println("[STEP 7 - VERIFY URL] Xác thực URL trang Giỏ hàng:");
        System.out.println("   -> Expected Result: URL chứa '" + exCartUrl + "'");
        System.out.println("   -> Actual Result  : '" + acCartUrl + "'");
        softAssert.assertTrue(acCartUrl.contains(exCartUrl), "Lỗi Step 7: URL trang Giỏ hàng không chính xác!");

        System.out.println("[STEP 7 - VERIFY ITEM] Xác thực dòng sản phẩm đề xuất tồn tại trong bảng:");
        int actualRows = prodPage.laySoLuongDongSanPhanTrongGio(); // Tái sử dụng hàm đã khai báo ở TC_020
        System.out.println("   -> Expected Result: Số lượng dòng sản phẩm > 0");
        System.out.println("   -> Actual Result  : Có " + actualRows + " dòng trong bảng giỏ hàng.");
        softAssert.assertTrue(actualRows > 0, "Lỗi Step 7: Sản phẩm đề xuất chưa được thêm thành công vào giỏ hàng!");
        System.out.println("   ==> Check Step 7 xong.\n");

        System.out.println("================================================================");
        System.out.println("===> KẾT THÚC BÀI TEST THỰC THI: ĐANG TỔNG HỢP BÁO CÁO LỖI VLU...");
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
