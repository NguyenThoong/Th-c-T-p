# 🚀 Automation Testing Framework - Automation Exercise

> **Báo cáo Thực tập Kiểm thử Tự động**  
> **Đơn vị thực tập:** Viện Khoa học Tính toán và Trí tuệ Nhân tạo (COSARI)  
> **Sinh viên thực hiện:** Nguyễn Tấn Hoàng Thông — MSSV: 2374802010485  
> **Trường:** Đại học Văn Lang (VLU) — Khoa Công nghệ Thông tin  

---

## 📌 1. Giới thiệu tổng quan
Dự án xây dựng một **Framework Kiểm thử Tự động (Automation Testing Framework)** hoàn chỉnh nhằm kiểm thử hệ thống thương mại điện tử thử nghiệm [Automation Exercise](https://automationexercise.com/). 

Dự án áp dụng mô hình thiết kế **Page Object Model (POM)** kết hợp với phương pháp **Kiểm thử hướng dữ liệu (Data-Driven Testing - DDT)** sử dụng tệp tin lưu trữ dữ liệu ngoại vi **JSON** giúp tách biệt hoàn toàn giữa kịch bản kiểm thử (Test Scripts) và dữ liệu đầu vào (Test Data).

---

## 🛠️ 2. Công nghệ & Thư viện sử dụng
* **Ngôn ngữ lập trình:** Java (JDK 11+)
* **Thư viện Automation:** Selenium WebDriver
* **Framework quản lý kịch bản:** TestNG
* **Trình phân tách dữ liệu JSON:** Google Gson Library
* **Kiến trúc thiết kế:** Page Object Model (POM) & Data-Driven Testing (DDT)
* **Công cụ build dự án:** Apache Maven
* **Báo cáo kết quả:** TestNG Report / ExtentReports

---

## 📁 3. Cấu trúc thư mục dự án (Project Structure)

```text
DoAnThucTap/
├── src/main/java/
│   └── com.vlu.pages/                    # Tầng chứa các Class Page Object Model (POM)
│       ├── BasePage.java                 # Lớp cha chứa các thao tác Web dùng chung
│       ├── ContactUsPage.java            # Khai báo phần tử & thao tác trang Liên hệ (TC_006)
│       ├── LoginPage.java                # Khai báo phần tử & thao tác Form Đăng nhập (TC_002, TC_003, TC_004)
│       ├── NavigationPage.java           # Khai báo các thanh điều hướng, Menu, Footer & Scroll
│       ├── ProductPage.java              # Khai báo danh sách sản phẩm, Tìm kiếm & Đánh giá
│       └── RegisterPage.java             # Khai báo Form Đăng ký tài khoản (TC_001, TC_005)
├── src/test/java/
│   ├── com.vlu.helpers/                 # Tầng chứa các lớp tiện ích bổ trợ
│   │   └── JsonHelper.java               # Lớp xử lý đọc và parse file JSON qua Google Gson
│   └── com.vlu.tests/                   # Tầng chứa các kịch bản kiểm thử (Test Scripts)
│       ├── Module_01_RegisterAndLoginTest.java  # Kiểm thử luồng Đăng ký & Đăng nhập (TC_001 -> TC_005)
│       ├── Module_02_PagesAndSearchTest.java    # Kiểm thử Trang & Tìm kiếm (TC_006 -> TC_013)
│       ├── Module_03_CheckoutAndOrderTest.java  # Kiểm thử Đặt hàng & Mua sắm (TC_014 -> TC_016, TC_023, TC_024)
│       ├── Module_04_CartAndProductsTest.java   # Kiểm thử Giỏ hàng & Sản phẩm (TC_017 -> TC_020,TC_022)
│       ├── Module_05_ProductReviewAndUITest.java# Kiểm thử Đánh giá sản phẩm & UI (TC_021, TC_025, TC_026)
│       ├── TaoAcc.java                          # Kịch bản phụ trợ tự động tạo dữ liệu tài khoản
│       └── TestDraft.java                       # Kịch bản thử nghiệm / kiểm thử đơn lẻ
└── src/test/resources/                  # Tài nguyên phục vụ kiểm thử
    ├── adblock.crx                       # Extension hỗ trợ chặn quảng cáo Google Ads
    ├── ublock.crx                        # Extension uBlock Origin tối ưu giao diện
    └── TestData.json                     # Tệp tin chứa 26 bộ dữ liệu kiểm thử ngoại vi
