# Coffee Shop Management System (Desktop Application)

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange)

## 📖 Giới thiệu
Đây là ứng dụng Desktop **Quản lý Kinh doanh Quán Cà phê** được phát triển nhằm số hóa quy trình đặt hàng và vận hành quán.  
Hệ thống không chỉ giúp khách hàng đặt món từ xa tiện lợi mà còn cung cấp cho người quản lý công cụ theo dõi doanh thu và tương tác với khách hàng theo thời gian thực.

Dự án áp dụng mô hình kiến trúc **MVC (Model-View-Controller)** để đảm bảo mã nguồn rõ ràng, dễ bảo trì và mở rộng.

## 🚀 Tính năng nổi bật

### 👤 Dành cho Khách hàng (Client App)
* **Đặt hàng trực quan:** Xem menu, tìm kiếm sản phẩm, tùy chỉnh đồ uống (Size, Topping, Số lượng) và thêm vào giỏ hàng.
* **Thanh toán & Hóa đơn:** Hỗ trợ nhập địa chỉ giao hàng 3 cấp (Tỉnh/Huyện/Xã), nhập mã giảm giá và nhận hóa đơn chi tiết qua Email sau khi thanh toán.
* **Chat Hỗ trợ (Real-time):** Nhắn tin trực tiếp với Admin để được hỗ trợ ngay lập tức thông qua giao thức **RabbitMQ**.
* **Bảo mật:** Đăng ký, Đăng nhập, Quên mật khẩu xác thực bằng mã **OTP qua Email**.
* **Lịch sử:** Xem lại danh sách các đơn hàng đã đặt.

### 🛠 Dành cho Quản lý (Admin Dashboard)
* **Dashboard Thống kê:** Biểu đồ trực quan về doanh thu theo ngày/tháng/năm, tổng số khách hàng và sản phẩm bán chạy.
* **Quản lý Sản phẩm:** Thêm, xóa, sửa thông tin sản phẩm và cập nhật trạng thái (Còn hàng/Hết hàng).
* **Quản lý Đơn hàng:** Xem danh sách đơn hàng chi tiết của từng khách.
* **Hỗ trợ Khách hàng:** Nhận và phản hồi tin nhắn của khách hàng theo thời gian thực.

## 🛠 Công nghệ sử dụng

| Thành phần | Công nghệ | Chi tiết |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java | Core logic |
| **Giao diện** | JavaFX / FXML | CSS styling, Scene Builder |
| **Cơ sở dữ liệu** | MySQL | Lưu trữ dữ liệu bền vững |
| **Kết nối DB** | JDBC | Java Database Connectivity |
| **Messaging** | RabbitMQ | Xử lý chat Real-time bất đồng bộ |
| **Tiện ích** | JavaMail | Gửi Email OTP & Hóa đơn |
| **Thư viện khác** | Gson, JXMaps | Xử lý JSON, Bản đồ |

## 📐 Kiến trúc Hệ thống (MVC)

Hệ thống được tổ chức theo mô hình MVC tiêu chuẩn:
* **View:** Các file FXML và giao diện JavaFX.
* **Controller:** Xử lý sự kiện từ người dùng và điều hướng logic.
* **Model:** Các thực thể dữ liệu (Product, User, Order) và lớp truy xuất DB (Repository).
* **External Services:** Module xử lý riêng cho RabbitMQ và JavaMail.

*(Bạn có thể chèn hình ảnh sơ đồ MVC từ báo cáo Hình 2.1 vào đây)*

## ⚙️ Hướng dẫn Cài đặt & Chạy

### 1. Yêu cầu hệ thống
* JDK 17 trở lên.
* MySQL Server (XAMPP hoặc MySQL Workbench).
* RabbitMQ Server (Đã cài đặt và bật Plugin Management).

### 2. Cài đặt Cơ sở dữ liệu
1.  Mở phpMyAdmin hoặc MySQL Workbench.
2.  Tạo database tên `coffee`.
3.  Import file `coffee.sql` (nằm trong thư mục database của dự án).

### 3. Cấu hình RabbitMQ
* Đảm bảo RabbitMQ đang chạy tại port mặc định `5672`.
* Cấu hình thông tin kết nối trong file config của source code (nếu có).

### 4. Chạy ứng dụng
Có 2 cách để chạy:

**Cách 1: Chạy từ IDE (Eclipse/IntelliJ)**
* Import dự án vào Eclipse.
* Cấu hình Build Path add các thư viện trong thư mục `lib`.
* Chạy file `Main.java`.

**Cách 2: Chạy bằng dòng lệnh (CMD)**
```bash
java --module-path "Đường_dẫn_đến_JavaFX_SDK\lib" --add-modules javafx.controls,javafx.fxml -jar "CoffeeShop.jar"

(Thay thế đường dẫn phù hợp với máy của bạn)

🔐 Tài khoản Quản trị (Admin)
Username: admin
Password: Admin*12345

📝 License
Dự án là Niên luận cơ sở ngành Mạng máy tính & Truyền thông dữ liệu.
