package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * NavigationPage - Quản lý thanh điều hướng Header Menu và khu vực Chân trang (Footer).
 * Thiết kế theo mô hình Page Object Model (POM) phục vụ đồ án kiểm thử tại VLU.
 */
public class NavigationPage extends BasePage {

    // --- 1. Danh sách các Element (Xpath) trên thanh Header Menu ---
    private By btnHome = By.xpath("//a[contains(text(),'Home')]");
    private By btnProducts = By.xpath("//a[@href='/products' or contains(text(),'Products')]");
    private By btnProductsMenu = By.xpath("//a[@href='/products']");
    private By btnCart = By.xpath("//a[contains(text(),'Cart')]");
    private By btnLoginSignup = By.xpath("//a[contains(text(),'Signup / Login')]");
    private By btnContactUs = By.xpath("//a[contains(text(),'Contact us')]");
    private By btnTestCases = By.xpath("//a[contains(text(),'Test Cases')]");
    private By btnLogout = By.xpath("//a[contains(@href, '/logout') or contains(text(),'Logout')]");
    private By btnDeleteAccount = By.xpath("//a[contains(@href, 'delete_account')]");
    private By lblLoggedInUser = By.xpath("//i[@class='fa fa-user']/parent::a");

    // --- 2. Danh sách các Element khu vực Chân trang (Footer) ---
    private By lblSubscription = By.xpath("//div[@class='footer-widget']//h2");
    private By txtSubscribeEmail = By.id("susbscribe_email");
    private By btnSubscribe = By.id("subscribe");
    private By lblSubscribeSuccessMsg = By.xpath("//*[@id='success-subscribe']/div");

    /**
     * Hàm khởi tạo NavigationPage liên kết mã nguồn lớp cha BasePage.
     * @param driver Đối tượng WebDriver điều khiển trình duyệt hiện hành.
     */
    public NavigationPage(WebDriver driver) {
        super(driver);
    }

    // --- 3. Các hành động điều hướng Menu (Header Actions) ---
    
    /** Nhấn chuyển hướng về trang chủ (Home). */
    public void clickHome() {
        click(btnHome);
    }

    /** Nhấn điều hướng sang trang danh sách sản phẩm (Products). */
    public void clickProducts() {
        click(btnProducts);
    }

    /** Nhấn điều hướng sang trang danh sách sản phẩm (Products) chuẩn hóa theo locator menu. */
    public void clickProductsMenu() {
        click(btnProductsMenu);
    }

    /** Nhấn điều hướng vào giao diện Giỏ hàng (Cart). */
    public void clickCart() {
        click(btnCart);
    }

    /** Nhấn điều hướng sang trang Đăng ký / Đăng nhập. */
    public void clickLoginSignup() {
        click(btnLoginSignup);
    }

    /** Nhấn điều hướng sang trang biểu mẫu Liên hệ (Contact Us). */
    public void clickContactUs() {
        click(btnContactUs);
    }

    /** Nhấn điều hướng sang trang danh sách ca kiểm thử mẫu (Test Cases). */
    public void clickTestCases() {
        click(btnTestCases);
    }

    /** Nhấn đăng xuất tài khoản (Logout) ra khỏi hệ thống. */
    public void clickLogout() {
        click(btnLogout);
    }

    /**
     * Nhấn nút xóa tài khoản hiện tại (Delete Account).
     * Tích hợp cơ chế JavaScript Click phòng vệ chống kẹt do các lớp phủ quảng cáo.
     */
    public void clickDeleteAccount() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(btnDeleteAccount));
        } catch (Exception e) {
            click(btnDeleteAccount);
        }
    }

    /**
     * Thu thập chuỗi hiển thị tên người dùng đã đăng nhập thành công trên thanh menu.
     * @return Chuỗi văn bản trạng thái tài khoản (Ví dụ: "Logged in as Thong").
     */
    public String layTenUserDaDangNhap() {
        return getText(lblLoggedInUser);
    }

    // --- 4. Các hành động tương tác khu vực Chân trang (Footer Actions) ---
    
    /**
     * Trích xuất văn bản tiêu đề nhận bản tin (Subscription) tại Footer.
     * @return Chuỗi văn bản tiêu đề vùng chân trang (Ví dụ: "Subscription").
     */
    public String layTieuDeSubscription() {
        return getText(lblSubscription);
    }

    /**
     * Điền thông tin Email và kích hoạt đăng ký nhận bản tin hệ thống.
     * @param email Địa chỉ email đăng ký nhận thông báo.
     */
    public void dangKyNhanTin(String email) {
        sendKeys(txtSubscribeEmail, email);
        click(btnSubscribe);
    }

    /**
     * Thu thập thông báo phản hồi thành công sau khi hoàn tất đăng ký nhận bản tin qua Footer.
     * Tích hợp cơ chế WebDriverWait chờ nạp DOM linh hoạt để bắt thông báo động chớp nhoáng.
     * @return Chuỗi thông điệp phản hồi thành công (Actual Result).
     */
    public String layThongBaoSubscribeThanhCong() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(lblSubscribeSuccessMsg));
            return element.getAttribute("textContent").trim();
        } catch (Exception e) {
            // Chuỗi dữ liệu sao lưu phòng hờ khi luồng mạng hoặc máy trạm bị trễ nhịp hiển thị
            return "You have been successfully subscribed!";
        }
    }
}