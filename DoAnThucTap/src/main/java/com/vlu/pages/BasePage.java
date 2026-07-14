package com.vlu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * BasePage - Lớp nền cấu trúc (Base Class) chứa các hàm tương tác cốt lõi.
 * Áp dụng mô hình Page Object Model (POM) trong đồ án Kiểm thử tự động tại VLU.
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Hàm khởi tạo thiết lập cấu trúc WebDriverWait dùng chung.
     * @param driver Đối tượng điều khiển trình duyệt từ Testcase truyền xuống.
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Hàm click chuột (Chờ phần tử sẵn sàng click rồi mới bấm).
     * @param by Định vị Locator của phần tử cần tương tác.
     */
    public void click(By by) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
    }

    /**
     * Hàm điền văn bản (Chờ ô nhập hiển thị, xóa chữ cũ rồi mới điền chữ mới).
     * @param by Định vị Locator của ô nhập liệu (Input/Textarea).
     * @param text Chuỗi dữ liệu kiểm thử được truyền vào từ TestData.
     */
    public void sendKeys(By by, String text) {
        if (text != null && !text.isEmpty()) { 
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element.clear();
            element.sendKeys(text);
        }
    }

    /**
     * Hàm lấy chữ trên Web (Dùng để lấy kết quả Actual Result để so sánh đối sánh).
     * @param by Định vị Locator của phần tử cần trích xuất chuỗi text.
     * @return Chuỗi văn bản đã được cắt bỏ khoảng trắng thừa ở hai đầu.
     */
    public String getText(By by) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return element.getText().trim();
    }

    /**
     * Hàm cuộn chuột đến phần tử (Phục vụ cho các kịch bản kiểm thử cuộn trang).
     * @param by Định vị Locator của phần tử mục tiêu cần cuộn màn hình đến.
     */
    public void scrollToElement(By by) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}