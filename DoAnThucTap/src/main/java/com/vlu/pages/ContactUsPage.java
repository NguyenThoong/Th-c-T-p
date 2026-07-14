package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ContactUsPage - Quản lý giao diện và các hành động trên trang Liên hệ (Contact Us).
 * Thiết kế theo mô hình Page Object Model (POM) chuẩn tại VLU.
 */
public class ContactUsPage extends BasePage {

    // --- 1. Danh sách Locators định vị phần tử ---
    private By lblGetInTouch = By.xpath("//h2[text()='Get In Touch']");
    private By txtName = By.xpath("//input[@data-qa='name']");
    private By txtEmail = By.xpath("//input[@data-qa='email']");
    private By txtSubject = By.xpath("//input[@data-qa='subject']");
    private By txtMessage = By.xpath("//textarea[@data-qa='message']");
    private By btnUploadFile = By.xpath("//input[@name='upload_file']");
    private By btnSubmit = By.xpath("//input[@data-qa='submit-button']");
    private By lblSuccessMessage = By.xpath("//div[contains(@class,'status') and contains(@class,'alert-success')]");

    /**
     * Hàm khởi tạo liên kết Driver với lớp nền BasePage.
     * @param driver Đối tượng WebDriver điều khiển trình duyệt.
     */
    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    // --- 2. Các hàm hành động nghiệp vụ (Actions) ---

    /**
     * Lấy tiêu đề biểu mẫu Contact Us để thực hiện verify giao diện.
     * @return Chuỗi văn bản tiêu đề (Ví dụ: "Get In Touch").
     */
    public String layTieuDeFormContact() {
        return getText(lblGetInTouch);
    }

    /**
     * Thực hiện điền toàn bộ thông tin bắt buộc vào form liên hệ Contact Us.
     * @param name Tên người gửi.
     * @param email Địa chỉ email liên hệ.
     * @param subject Tiêu đề thư.
     * @param message Nội dung tin nhắn chi tiết.
     */
    public void dienFormContact(String name, String email, String subject, String message) {
        sendKeys(txtName, name);
        sendKeys(txtEmail, email);
        sendKeys(txtSubject, subject);
        sendKeys(txtMessage, message);
    }

    /**
     * Thực hiện tải tệp tin đính kèm từ máy tính lên form bằng đường dẫn tuyệt đối.
     * @param absoluteFilePath Đường dẫn tuyệt đối dẫn tới file cần upload (txt, pdf, jpg,...).
     */
    public void taiTepDinhKem(String absoluteFilePath) {
        sendKeys(btnUploadFile, absoluteFilePath);
    }

    /**
     * Nhấn nút Submit để gửi thông tin liên hệ lên hệ thống.
     */
    public void clickSubmit() {
        click(btnSubmit);
    }

    /**
     * Lấy nội dung thông báo phản hồi từ hệ thống sau khi gửi form thành công.
     * @return Chuỗi văn bản thông báo thành công (Actual Result).
     */
    public String layThongBaoGửiThanhCong() {
        return getText(lblSuccessMessage);
    }
}