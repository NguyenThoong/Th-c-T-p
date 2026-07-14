package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

/**
 * ProductPage - Quản lý toàn bộ giao diện danh sách, tìm kiếm, chi tiết sản phẩm,
 * thanh toán, và các khu vực tương tác nâng cao (Category, Brands, Reviews).
 * Thiết kế tuân thủ mô hình Page Object Model (POM) chuẩn hệ thống kiểm thử VLU.
 */
public class ProductPage extends BasePage {

    // ========================================================================
    // 🎯 1. DANH SÁCH LOCATORS ĐỊNH VỊ PHẦN TỬ (ELEMENTS)
    // ========================================================================
    
    // Luồng Tìm kiếm & Giỏ hàng cơ bản
    private By txtSearchBox = By.id("search_product");
    private By btnSearch = By.id("submit_search");
    private By lblSearchTitle = By.xpath("//h2[@class='title text-center']");
    private By btnAddToCart = By.xpath("//a[@class='btn btn-default add-to-cart']");
    private By btnViewCart = By.xpath("//u[contains(text(),'View Cart')]");
    private By lblCartPageTitle = By.xpath("//li[@class='active']");
    private By rowCartItems = By.xpath("//table[@id='cart_info_table']/tbody/tr");
    
    // Giao diện danh sách và chi tiết sản phẩm (TC_008)
    private By lblAllProducts = By.xpath("//h2[@class='title text-center' and text()='All Products']");
    private By btnFirstViewProduct = By.xpath("(//a[text()='View Product'])[1]");
    private By lblProductName = By.xpath("//div[@class='product-information']/h2");
    private By lblCategory = By.xpath("//div[@class='product-information']/p[contains(text(), 'Category:')]");
    private By lblPrice = By.xpath("//div[@class='product-information']/span/span");
    private By lblAvailability = By.xpath("//div[@class='product-information']/p[b[text()='Availability:']]");
    private By lblCondition = By.xpath("//div[@class='product-information']/p[b[text()='Condition:']]");
    private By lblBrand = By.xpath("//div[@class='product-information']/p[b[text()='Brand:']]");
    
    // Biểu mẫu Contact Us (TC_006)
    private By txtContactName = By.xpath("//input[@data-qa='name']");
    private By txtContactEmail = By.xpath("//input[@data-qa='email']");
    private By txtContactSubject = By.xpath("//input[@data-qa='subject']");
    private By txtContactMessage = By.xpath("//textarea[@data-qa='message']");
    private By btnSubmitContact = By.xpath("//input[@data-qa='submit-button']");
    private By lblContactSuccessMsg = By.xpath("//div[@class='status alert alert-success']");

    // Biểu mẫu Thanh toán & Xác nhận đơn hàng (TC_014)
    private By txtCardName = By.xpath("//input[@data-qa='name-on-card']");
    private By txtCardNumber = By.xpath("//input[@data-qa='card-number']");
    private By txtCvc = By.xpath("//input[@data-qa='cvc']");
    private By txtExpMonth = By.xpath("//input[@data-qa='expiry-month']");
    private By txtExpYear = By.xpath("//input[@data-qa='expiry-year']");
    private By btnPayAndConfirm = By.xpath("//button[@data-qa='pay-button']");
    private By lblOrderPlacedMsg = By.xpath("//h2[@data-qa='order-placed']/b");

    // Định vị động dựa trên Tên sản phẩm & Modal Popup (TC_012)
    private String xpathProductBox = "//p[text()='%s']/ancestor::div[@class='product-image-wrapper']";
    private String xpathBtnAddToCart = "//p[text()='%s']/following-sibling::a[contains(@class,'add-to-cart')]";
    private By btnContinueShopping = By.xpath("//div[@id='cartModal']//button[text()='Continue Shopping']");
    private By btnViewCartModal = By.xpath("//div[@id='cartModal']//a[@href='/view_cart']");
    
    // Tăng số lượng tại trang chi tiết sản phẩm (TC_013)
    private By txtQuantityInput = By.id("quantity");
    private By btnDetailAddToCart = By.xpath("//button[contains(@class,'cart')]");
    
    // Bộ lọc Sidebar Category (TC_018)
    private By titleCategorySidebar = By.xpath("//div[@class='left-sidebar']/h2[text()='Category']");
    private By linkWomenCategory = By.xpath("//a[contains(@href, '#Women')]");
    private By linkWomenDress = By.xpath("//a[contains(@href, '/category_products/1')]");
    private By lblCategoryPageTitle = By.xpath("//h2[@class='title text-center']");
    private By linkMenCategory = By.xpath("//a[contains(@href, '#Men')]");
    private By linkMenTshirts = By.xpath("//a[contains(@href, '/category_products/3')]");
    
    // Bộ lọc Sidebar Brands (TC_019)
    private By titleBrandsSidebar = By.xpath("/html/body/section[2]/div/div/div[1]/div/div[3]/h2/a/span");
    private By linkBrandPolo = By.xpath("//a[contains(@href, '/brand_products/Polo')]");
    private By linkBrandHM = By.xpath("//a[contains(@href, '/brand_products/H&M')]");
    private By lblBrandPageTitle = By.xpath("//h2[@class='title text-center']");
    
    // Khu vực Sản phẩm đề xuất - Recommended Items (TC_022)
    private By lblRecommendedTitle = By.xpath("//div[@class='recommended_items']/h2[text()='recommended items']");
    private By btnAddRecommendedActive = By.xpath("//div[@id='recommended-item-carousel']//div[@class='item active']//a[@class='btn btn-default add-to-cart']");
    
    // Biểu mẫu gửi Đánh giá - Write Your Review (TC_021)
    private By txtReviewName = By.id("name");
    private By txtReviewEmail = By.id("email");
    private By txtReviewComment = By.id("review");
    private By btnReviewSubmit = By.id("button-review");
    private By lblReviewSuccessAlert = By.xpath("//*[@id='review-section']/div/div/span");
    
    // Khu vực Footer Subscription & Giao diện cuộn (TC_025 & TC_026)
    private By lblSubscriptionFooter = By.xpath("//div[@class='single-widget']/h2[text()='Subscription']");
    private By btnScrollUpArrow = By.id("scrollUp");
    private By lblHeaderCarouselTitle = By.xpath("//div[@id='slider-carousel']//div[@class='item active']//h2[contains(text(),'Automation')]");
    
    // Khối xác thực địa chỉ Đơn hàng (TC_023 & TC_024)
    private By lblDeliveryAddressBlock = By.xpath("//ul[@id='address_delivery']/li");
    private By lblBillingAddressBlock = By.xpath("//ul[@id='address_invoice']/li");

    // ========================================================================
    // ⏳ 2. HÀM KHỞI TẠO (CONSTRUCTOR)
    // ========================================================================
    public ProductPage(WebDriver driver) {
        super(driver);
    }

    // ========================================================================
    // 🚀 3. CÁC HÀM HÀNH ĐỘNG NGHIỆP VỤ (METHODS)
    // ========================================================================
    
    /** Thực hiện tìm kiếm sản phẩm theo từ khóa nhập vào. */
    public void timKiemSanPham(String text) {
        sendKeys(txtSearchBox, text);
        click(btnSearch);
    }

    /** Lấy tiêu đề trang kết quả tìm kiếm sản phẩm. */
    public String layTieuDeTrangTimKiem() {
        return getText(lblSearchTitle);
    }

    /** Lấy tiêu đề khối All Products tại trang danh sách. */
    public String layTieuDeAllProducts() {
        return getText(lblAllProducts);
    }

    /** Kích hoạt xem chi tiết của sản phẩm đầu tiên hiển thị. */
    public void clickXemSanPhamDauTien() {
        click(btnFirstViewProduct);
    }

    /** Lấy tên sản phẩm tại giao diện trang chi tiết. */
    public String layTenSanPham() {
        return getText(lblProductName);
    }

    /** Lấy thông tin danh mục (Category) của sản phẩm. */
    public String layDanhMucSanPham() {
        return getText(lblCategory);
    }

    /** Lấy giá bán hiển thị của sản phẩm. */
    public String layGiaSanPham() {
        return getText(lblPrice);
    }

    /** Lấy trạng thái tồn kho (Availability). */
    public String layTrangThaiKho() {
        return getText(lblAvailability);
    }

    /** Lấy tình trạng mới/cũ (Condition) của sản phẩm. */
    public String layTinhTrangSanPham() {
        return getText(lblCondition);
    }

    /** Lấy tên thương hiệu (Brand) của sản phẩm. */
    public String layThuongHieuSanPham() {
        return getText(lblBrand);
    }

    /** Tiến hành điền và submit biểu mẫu Contact Us. */
    public void dienFormContactUs(String name, String email, String subject, String message) {
        sendKeys(txtContactName, name);
        sendKeys(txtContactEmail, email);
        sendKeys(txtContactSubject, subject);
        sendKeys(txtContactMessage, message);
        click(btnSubmitContact);
    }

    /** Lấy thông điệp phản hồi gửi liên hệ thành công. */
    public String layThongBaoContactThanhCong() {
        return getText(lblContactSuccessMsg);
    }

    /** Điền thông tin thẻ ngân hàng vào form Checkout/Payment. */
    public void dienThongTinThanhToan(String cardName, String cardNumber, String cvc, String expMonth, String expYear) {
        sendKeys(txtCardName, cardName);
        sendKeys(txtCardNumber, cardNumber);
        sendKeys(txtCvc, cvc);
        sendKeys(txtExpMonth, expMonth);
        sendKeys(txtExpYear, expYear);
    }
    
    /** Kích hoạt xác nhận thanh toán đơn hàng bằng JavaScript Click phòng vệ. */
    public void clickPayAndConfirmOrder() {
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnPayAndConfirm));
        } catch (Exception e) {
            click(btnPayAndConfirm);
        }
    }

    /** Lấy thông điệp phản hồi đặt hàng thành công. */
    public String layThongBaoDatHangThanhCong() {
        return getText(lblOrderPlacedMsg);
    }

    /** Rê chuột và thêm sản phẩm chỉ định vào giỏ hàng thông qua cơ chế định vị động. */
    public void hoverVaThemGiaoDienSanPham(String tenSanPham) throws InterruptedException {
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        By productLocation = By.xpath(String.format(xpathProductBox, tenSanPham));
        By btnAddLocation = By.xpath(String.format(xpathBtnAddToCart, tenSanPham));
        
        actions.moveToElement(driver.findElement(productLocation)).perform();
        Thread.sleep(1000);
        
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnAddLocation));
        } catch (Exception e) {
            click(btnAddLocation);
        }
    }

    /** Nhấn nút 'Continue Shopping' trên Modal thông báo. */
    public void clickContinueShopping() {
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnContinueShopping));
        } catch (Exception e) {
            click(btnContinueShopping);
        }
    }

    /** Nhấn nút 'View Cart' trực tiếp trên Modal nổi. */
    public void clickViewCartTrenModal() {
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnViewCartModal));
        } catch (Exception e) {
            click(btnViewCartModal);
        }
    }
    
    /** Thay đổi số lượng mua hàng tại trường Input trang chi tiết. */
    public void thayDoiSoLuong(String soLuong) {
        driver.findElement(txtQuantityInput).clear();
        sendKeys(txtQuantityInput, soLuong);
    }

    /** Nhấn thêm vào giỏ hàng tại giao diện trang chi tiết. */
    public void clickAddtoCartTrangChiTiet() {
        try {
            click(btnDetailAddToCart);
        } catch (Exception e) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnDetailAddToCart));
        }
    }
    
    /** Xác thực thanh danh mục Sidebar Category có hiển thị hay không. */
    public boolean isThanhDanhMucHienThi() {
        return driver.findElement(titleCategorySidebar).isDisplayed();
    }

    /** Nhấn mở rộng nhóm danh mục 'Women'. */
    public void clickDanhMucWomen() {
        click(linkWomenCategory);
    }

    /** Chọn danh mục con 'Dress' thuộc nhóm phụ nữ. */
    public void clickDanhMucConDress() {
        WebElement element = driver.findElement(linkWomenDress);
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    /** Thu thập tiêu đề bộ lọc trang danh mục hiện hành. */
    public String layTieuDeTrangDanhMuc() {
        return driver.findElement(lblCategoryPageTitle).getText().trim();
    }

    /** Nhấn mở rộng nhóm danh mục 'Men'. */
    public void clickDanhMucMen() {
        click(linkMenCategory);
    }

    /** Chọn danh mục con 'Tshirts' thuộc nhóm nam giới. */
    public void clickDanhMucConTshirts() {
        WebElement element = driver.findElement(linkMenTshirts);
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }
    
    /** Xác thực sự xuất hiện của thanh thương hiệu Brands trên Sidebar. */
    public boolean isThanhBrandsHienThi() {
        return driver.findElement(titleBrandsSidebar).isDisplayed();
    }

    /** Lọc danh sách sản phẩm theo thương hiệu Polo. */
    public void clickThuongHieuPolo() {
        WebElement element = driver.findElement(linkBrandPolo);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Lọc danh sách sản phẩm theo thương hiệu H&M. */
    public void clickThuongHieuHM() {
        WebElement element = driver.findElement(linkBrandHM);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Lấy nội dung tiêu đề trang sau khi lọc thương hiệu. */
    public String layTieuDeTrangThuongHieu() {
        return driver.findElement(lblBrandPageTitle).getText().trim().toUpperCase();
    }
    
    /** Kiểm tra xem tiêu đề 'SEARCHED PRODUCTS' có hiển thị đúng kịch bản hay không. */
    public boolean isTieuDeSearchedProductsHienThi() {
        return driver.findElement(lblSearchTitle).isDisplayed();
    }

    /** Thêm sản phẩm đầu tiên tìm thấy trong danh sách kết quả tìm kiếm vào giỏ hàng. */
    public void themSanPhamDauTienTimThay() {
        WebElement element = driver.findElement(btnAddToCart);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Đếm tổng số lượng dòng sản phẩm hiện có trong bảng giỏ hàng. */
    public int laySoLuongDongSanPhanTrongGio() {
        return driver.findElements(rowCartItems).size();
    }
    
    /** Cuộn màn hình xuống cuối trang chủ (vùng Footer). */
    public void cuonXuongCuoiTrang() {
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /** Kiểm tra xem khối sản phẩm đề xuất (Recommended Items) có hiển thị hay không. */
    public boolean isKhoiRecommendedItemsHienThi() {
        return driver.findElement(lblRecommendedTitle).isDisplayed();
    }

    /** Bấm nút thêm vào giỏ của sản phẩm đề xuất đang Active trên Carousel slide. */
    public void clickAddtoCartSanPhamDeXuat() {
        WebElement element = driver.findElement(btnAddRecommendedActive);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
    
    /** Kiểm tra form 'Write Your Review' có hiển thị tại trang chi tiết sản phẩm hay không. */
    public boolean isFormWriteYourReviewHienThi() {
        return driver.findElement(By.xpath("//a[@href='#reviews']")).isDisplayed();
    }

    /** Điền thông tin đánh giá nhận xét sản phẩm. */
    public void dienFormReview(String name, String email, String comment) {
        sendKeys(txtReviewName, name);
        sendKeys(txtReviewEmail, email);
        sendKeys(txtReviewComment, comment);
    }

    /** Nhấn nút gửi đánh giá lên hệ thống. */
    public void clickSubmitReview() {
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnReviewSubmit));
        } catch (Exception e) {
            click(btnReviewSubmit);
        }
    }

    /** Lấy nội dung thông báo gửi nhận xét thành công. */
    public String layThongBaoReviewThanhCong() {
        return driver.findElement(lblReviewSuccessAlert).getText().trim();
    }
    
    /** Kiểm tra khối tiêu đề Subscription chân trang có xuất hiện hay không. */
    public boolean isChunghNhankSubscriptionHienThi() {
        return driver.findElement(lblSubscriptionFooter).isDisplayed();
    }

    /** Click nút mũi tên điều hướng cuộn nhanh lên đầu trang. */
    public void clickNutMuiTenCuonLen() {
        click(btnScrollUpArrow);
    }

    /** Kiểm tra sự hiển thị của văn bản Banner Header Slide. */
    public boolean isBannerHeaderHienThi() {
        return driver.findElement(lblHeaderCarouselTitle).isDisplayed();
    }

    /** Trích xuất text hiển thị trên Banner Slide đầu trang. */
    public String layTextBannerHeader() {
        return driver.findElement(lblHeaderCarouselTitle).getText().trim();
    }

    /** Giả lập hành động cuộn chuột thủ công ngược lên đỉnh trang bằng JavaScript. */
    public void cuonLenDauTrangBangChuot() {
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
    }
    
    /** Kích hoạt tải tệp hóa đơn bán hàng về bộ nhớ máy trạm. */
    public void clickDownloadInvoice() {
        try {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//a[text()='Download Invoice']")));
        } catch (Exception e) {
            driver.findElement(By.xpath("//a[text()='Download Invoice']")).click();
        }
    }
    
    /** Trích xuất thông tin tổng hợp hiển thị trong khối Địa chỉ giao hàng (Delivery Address). */
    public String layThongTinDiaChiGiaoHang() {
        List<WebElement> lines = driver.findElements(lblDeliveryAddressBlock);
        StringBuilder addressText = new StringBuilder();
        for (WebElement line : lines) {
            addressText.append(line.getText().trim()).append(" ");
        }
        return addressText.toString().trim();
    }

    /** Trích xuất thông tin tổng hợp hiển thị trong khối Địa chỉ hóa đơn (Billing Address). */
    public String layThongTinDiaChiHoaDon() {
        List<WebElement> lines = driver.findElements(lblBillingAddressBlock);
        StringBuilder addressText = new StringBuilder();
        for (WebElement line : lines) {
            addressText.append(line.getText().trim()).append(" ");
        }
        return addressText.toString().trim();
    }
}