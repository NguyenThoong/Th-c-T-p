package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * RegisterPage - Quản lý giao diện, định vị phần tử và các hành động nghiệp vụ 
 * trên trang Đăng ký thông tin tài khoản chi tiết (Account & Address Information).
 * Thiết kế tuân thủ mô hình kiến trúc Page Object Model (POM) tại VLU.
 */
public class RegisterPage extends BasePage {

    // --- 1. Danh sách các Element (Xpath/Id) trên form Đăng ký chi tiết ---
    
    // Khối thông tin tài khoản (Enter Account Information)
    private By rdoMr = By.id("id_gender1");
    private By rdoMrs = By.id("id_gender2");
    private By txtPassword = By.id("password");
    private By drpDay = By.id("days");
    private By drpMonth = By.id("months");
    private By drpYear = By.id("years");
    private By chkNewsletter = By.id("newsletter");
    private By chkOffers = By.id("optin");

    // Khối thông tin địa chỉ & Liên hệ (Address Information)
    private By txtFirstName = By.id("first_name");
    private By txtLastName = By.id("last_name");
    private By txtCompany = By.id("company");
    private By txtAddress1 = By.id("address1");
    private By txtAddress2 = By.id("address2");
    private By drpCountry = By.id("country");
    private By txtState = By.id("state");
    private By txtCity = By.id("city");
    private By txtZipcode = By.id("zipcode");
    private By txtMobile = By.id("mobile_number");
    private By btnCreateAccount = By.xpath("//button[@data-qa='create-account']");
    
    // Tiêu đề thông báo thành công
    private By lblSuccessMessage = By.xpath("//*[@data-qa='account-created']");

    /**
     * Hàm khởi tạo liên kết Driver của RegisterPage kế thừa từ BasePage.
     * @param driver Đối tượng WebDriver điều khiển trình duyệt hiện hành.
     */
    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    // --- 2. Các hàm hành động nghiệp vụ (Actions) ---

    /**
     * Hàm lựa chọn Giới tính (Title) dựa vào chuỗi dữ liệu đầu vào.
     * @param title Chuỗi giới tính truyền vào ("Mr." hoặc "Mrs.").
     */
    public void chonGioiTinh(String title) {
        if (title != null) {
            if (title.equalsIgnoreCase("Mr.")) {
                click(rdoMr);
            } else if (title.equalsIgnoreCase("Mrs.")) {
                click(rdoMrs);
            }
        }
    }

    /**
     * Thực hiện điền trọn gói phần thông tin tài khoản (Account Information).
     * Tích hợp Select Dropdown cho ngày/tháng/năm và JavaScript Click cho checkbox chống kẹt.
     * @param title Danh xưng giới tính ("Mr." hoặc "Mrs.").
     * @param password Mật khẩu tài khoản mới.
     * @param day Ngày sinh (Chuỗi số).
     * @param month Tháng sinh (Chuỗi chữ tiếng Anh, ví dụ: "January").
     * @param year Năm sinh (Chuỗi số).
     * @throws InterruptedException Lỗi ngắt luồng khi dừng Thread.sleep.
     */
    public void dienThongTinTaiKhoan(String title, String password, String day, String month, String year) throws InterruptedException {
        chonGioiTinh(title);
        Thread.sleep(500);
        sendKeys(txtPassword, password);
        
        // Chọn ngày, tháng, năm sinh bằng Select Dropdown
        if (day != null && !day.isEmpty()) new Select(driver.findElement(drpDay)).selectByValue(day);
        Thread.sleep(500);
        if (month != null && !month.isEmpty()) new Select(driver.findElement(drpMonth)).selectByVisibleText(month);
        Thread.sleep(500);
        if (year != null && !year.isEmpty()) new Select(driver.findElement(drpYear)).selectByValue(year);
        Thread.sleep(500);
        
        // Ép click chọn Newsletter và Offers bằng JavaScript để phòng hờ quảng cáo che khuất
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            WebElement elementNewsletter = driver.findElement(chkNewsletter);
            js.executeScript("arguments[0].click();", elementNewsletter);
            
            WebElement elementOffers = driver.findElement(chkOffers);
            js.executeScript("arguments[0].click();", elementOffers);
            
            System.out.println("-> Đã ép click chọn Newsletter và Offers bằng JS thành công.");
        } catch (Exception e) {
            System.out.println("Lưu ý: Không click được checkbox bằng JS, thử click thông thường.");
            click(chkNewsletter);
            click(chkOffers);
        }
    }

    /**
     * Thực hiện điền trọn gói phần địa chỉ (Address Information) và kích hoạt nút Đăng ký.
     * Tích hợp tự động cuộn màn hình bằng JavaScript cuộn đến vị trí nút Submit cuối form.
     * @param fName Tên (First Name).
     * @param lName Họ (Last Name).
     * @param company Tên công ty.
     * @param addr1 Địa chỉ dòng 1.
     * @param addr2 Địa chỉ dòng 2.
     * @param country Quốc gia lựa chọn trên Dropdown.
     * @param state Bang / Tỉnh thành.
     * @param city Thành phố.
     * @param zipcode Mã bưu chính.
     * @param mobile Số điện thoại di động liên hệ.
     * @throws InterruptedException Lỗi ngắt luồng khi dừng Thread.sleep.
     */
    public void dienThongTinDiaChi(String fName, String lName, String company, String addr1, String addr2, 
                                   String country, String state, String city, String zipcode, String mobile) throws InterruptedException {
        sendKeys(txtFirstName, fName);
        Thread.sleep(500);
        sendKeys(txtLastName, lName);
        Thread.sleep(500);
        sendKeys(txtCompany, company);
        Thread.sleep(500);
        sendKeys(txtAddress1, addr1);
        Thread.sleep(500);
        sendKeys(txtAddress2, addr2);
        
        // Chọn quốc gia bằng Select Dropdown
        if (country != null && !country.isEmpty()) {
            new Select(driver.findElement(drpCountry)).selectByVisibleText(country);
        }
        
        sendKeys(txtState, state);
        sendKeys(txtCity, city);
        sendKeys(txtZipcode, zipcode);
        sendKeys(txtMobile, mobile);
        
        // Ép trình duyệt cuộn màn hình xuống đúng vị trí nút bấm để click an toàn
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(btnCreateAccount));
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Lưu ý: Không cuộn được màn hình bằng JS, thử click trực tiếp.");
        }
        
        click(btnCreateAccount);
    }

    /**
     * Trích xuất văn bản tiêu đề phản hồi thành công sau khi hoàn tất đăng ký biểu mẫu.
     * Phục vụ bước quyết định Assert xem tài khoản đã được khởi tạo hay chưa.
     * @return Chuỗi văn bản phản hồi thành công từ hệ thống (Ví dụ: "ACCOUNT CREATED!").
     */
    public String layThongBaoDangKyThanhCong() {
        return getText(lblSuccessMessage);
    }
}