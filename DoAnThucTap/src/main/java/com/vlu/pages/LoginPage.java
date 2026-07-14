package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // --- 1. Danh sách các Element (Xpath) trên trang Login/Signup ---
    
    // Form Đăng ký ban đầu (New User Signup! - Nằm bên phải giao diện)
    private By txtSignupName = By.xpath("//input[@data-qa='signup-name']");
    private By txtSignupEmail = By.xpath("//input[@data-qa='signup-email']");
    private By btnSignup = By.xpath("//button[@data-qa='signup-button']");
    private By lblSignupErrorMessage = By.xpath("//form[@action='/signup']/p");
    
    // Đã gom lên trên: Locator của tiêu đề form Đăng ký "New User Signup!"
    private By lblSignupTitle = By.xpath("//div[@class='signup-form']/h2");

    // Form Đăng nhập (Login to your account - Nằm bên trái giao diện)
    private By txtLoginEmail = By.xpath("//input[@data-qa='login-email']");
    private By txtLoginPassword = By.xpath("//input[@data-qa='login-password']");
    private By btnLogin = By.xpath("//button[@data-qa='login-button']");
    private By lblLoginErrorMessage = By.xpath("//form[@action='/login']/p");

    // Locator của tiêu đề form đăng nhập "Login to your account"
    private By lblLoginTitle = By.xpath("//div[@class='login-form']/h2");

    // --- 2. Hàm khởi tạo (Constructor) ---
    public LoginPage(WebDriver driver) {
        super(driver); // Gọi lại Constructor của BasePage để truyền Driver
    }

    // --- 3. Các hành động (Methods) thực thi trên trang ---

    // Hàm lấy tiêu đề form login để xác thực (Dùng cho TC_02 và TC_04 khi Logout)
    public String layTieuDeFormLogin() {
        return getText(lblLoginTitle);
    }

    // Hàm lấy tiêu đề form Đăng ký để phục vụ Step 5 của TC_005
    public String layTieuDeFormSignup() {
        return getText(lblSignupTitle);
    }

    // Hàm thực hiện Đăng ký bước đầu (Nhập Name + Email rồi nhấn Signup)
    public void dienFormSignupBanDau(String name, String email) {
        sendKeys(txtSignupName, name);
        sendKeys(txtSignupEmail, email);
        click(btnSignup);
    }

    // Hàm thực hiện Đăng nhập (Nhập Email + Password rồi nhấn Login)
    public void dienFormLogin(String email, String password) {
        sendKeys(txtLoginEmail, email);
        sendKeys(txtLoginPassword, password);
        click(btnLogin);
    }

    // Hàm lấy thông báo lỗi khi đăng nhập sai (Dùng cho TC_03)
    public String layLoiDangNhapSai() {
        return getText(lblLoginErrorMessage);
    }

    // Hàm lấy thông báo lỗi khi đăng ký trùng email (Dùng cho TC_05)
    public String layLoiTrungEmail() {
        return getText(lblSignupErrorMessage);
    }
}