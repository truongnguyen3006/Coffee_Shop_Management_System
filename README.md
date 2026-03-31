# Hệ thống Quản lý Quán Cà Phê

Ứng dụng desktop quản lý quán cà phê được xây dựng bằng **Java**, **JavaFX**, **FXML**, **MySQL**, **RabbitMQ** và **JavaMail**.

Dự án này được tạo ra để hỗ trợ các nghiệp vụ phổ biến của quán cà phê như xác thực, duyệt menu, giỏ hàng, đặt món, lịch sử đơn hàng, quản trị viên và chat hỗ trợ khách hàng.

## Bài toán đặt ra

Các hệ thống quán cà phê quy mô nhỏ thường bị tách rời qua nhiều quy trình thủ công:
- khách hàng đặt món thủ công,
- quản trị viên theo dõi sản phẩm và đơn hàng trên các màn hình riêng biệt,
- giao tiếp hỗ trợ bị rời rạc,
- xác nhận đơn hàng chưa được tự động hóa.

Dự án này tập trung các quy trình đó vào một ứng dụng desktop duy nhất cho cả **khách hàng** và **quản trị viên**.

## Tính năng chính

### Tính năng cho khách hàng
- Đăng ký tài khoản
- Đăng nhập và đăng xuất
- Xác thực OTP qua email / khôi phục mật khẩu
- Duyệt menu và chi tiết sản phẩm
- Thêm sản phẩm vào giỏ hàng
- Chọn size và topping
- Đặt hàng
- Xem lịch sử đơn hàng
- Quản lý thông tin tài khoản
- Chat với admin hỗ trợ
- Nhận email xác nhận đơn hàng

### Tính năng cho quản trị viên
- Đăng nhập với vai trò admin
- Xem tổng quan dashboard
- Quản lý sản phẩm
- Cập nhật trạng thái sản phẩm
- Xem thông tin đơn hàng
- Theo dõi thống kê doanh thu
- Chat với người dùng

## Ảnh chụp màn hình Demo

### Đăng nhập
<img src="screenshots/login.png" alt="Login Screen" width="700">

### Trang chủ
<img src="screenshots/Home.png" alt="Login Screen" width="700">

### Giỏ hàng
<img src="screenshots/Cart.png" alt="Login Screen" width="700">

### Chat phía khách hàng
<img src="screenshots/ChatClient.png" alt="Login Screen" width="700">

### Dashboard quản trị
<img src="screenshots/Admin_Dashboard.png" alt="Login Screen" width="700">

### Chat hỗ trợ khách hàng
<img src="screenshots/Customer_Support_Chat(1).png" alt="Login Screen" width="700">
<img src="screenshots/Customer_Support_Chat(2).png" alt="Login Screen" width="700">

## Công nghệ sử dụng

- Java 23
- JavaFX SDK 25
- FXML
- MySQL
- RabbitMQ
- Erlang/OTP
- JavaMail
- Gson
- SLF4J

## Tổng quan kiến trúc

Sau khi refactor, dự án được tổ chức gần với kiến trúc desktop phân lớp hơn:

- `application/controller/`  
  Các controller JavaFX và lớp xử lý luồng giao diện
- `application/model/`  
  Domain models, DTOs và view models
- `application/service/`  
  Business logic như xác thực, giỏ hàng, đơn hàng, sản phẩm, dashboard, doanh thu, email, OTP
- `application/repository/`  
  Logic truy cập cơ sở dữ liệu
- `application/messaging/`  
  Các lớp liên quan đến RabbitMQ và chat
- `application/util/`  
  Validation, logging, điều hướng scene, load tài nguyên
- `application/config/`  
  Các lớp liên quan đến session/cấu hình
- `application/`  
  Bootstrap ứng dụng, helper database/config, alert dùng chung và các wrapper tương thích cũ
- `sql/`  
  File schema và seed dữ liệu
- `docs/`  
  Tài liệu portfolio, demo, phỏng vấn và hướng dẫn cài đặt
- `screenshots/`  
  Nơi đặt ảnh chụp màn hình ứng dụng cho portfolio GitHub

Các controller hiện nay chủ yếu tập trung vào:
- nhận input từ UI,
- gọi service,
- cập nhật JavaFX views.

## Cấu trúc dự án

```text
coffeeShopManagementSystem/
├─ docs/
├─ lib/
├─ screenshots/
├─ sql/
├─ src/
│  ├─ application/
│  │  ├─ config/
│  │  ├─ controller/
│  │  ├─ messaging/
│  │  ├─ model/
│  │  ├─ repository/
│  │  ├─ service/
│  │  ├─ util/
│  │  ├─ *.fxml
│  │  ├─ Main.java / các lớp bootstrap dùng chung
│  │  └─ *.css
│  ├─ Banner/
│  ├─ Image/
│  └─ Product/
├─ app.properties.example
├─ README.md
└─ .gitignore
```

## Yêu cầu môi trường

Nhấp vào các mục bên dưới để mở trang tải chính thức:

- Windows 10/11
- [JDK 23 (Eclipse Temurin)](https://adoptium.net/temurin/releases/?arch=any&os=any&package=jdk&version=23)
- [JavaFX SDK 25](https://jdk.java.net/javafx25/)
- [MySQL 8.x](https://dev.mysql.com/downloads/mysql/)
- [RabbitMQ](https://www.rabbitmq.com/docs/download)
- [Erlang/OTP](https://www.erlang.org/downloads)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) hoặc [Eclipse](https://www.eclipse.org/downloads/)

## Môi trường đã kiểm thử

Dự án này đã được kiểm thử trong môi trường sau:

- Windows 10/11
- JDK 23
- JavaFX SDK 25
- IntelliJ IDEA / Eclipse
- MySQL 8.x
- RabbitMQ 4.x
- Erlang/OTP tương thích với RabbitMQ

## Cấu hình

Tạo file `app.properties` cục bộ ở thư mục gốc dự án dựa trên `app.properties.example`.

Ví dụ:

```properties
db.url=jdbc:mysql://localhost:3306/coffee?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
db.username=root
db.password=

mail.host=smtp.gmail.com
mail.port=587
mail.username=your_email@gmail.com
mail.password=your_gmail_app_password

rabbitmq.host=localhost
rabbitmq.port=5672
rabbitmq.username=guest
rabbitmq.password=guest
```

> Không commit `app.properties` lên GitHub.

## Thiết lập cơ sở dữ liệu

### Tùy chọn 1: dữ liệu demo cho portfolio/public
Được khuyến nghị cho GitHub và demo phỏng vấn.

1. Tạo cơ sở dữ liệu `coffee`
2. Import `sql/schema.sql`
3. Import `sql/seed.sql`

Tài khoản demo:
- Admin: `admin` / `Admin@123`
- User: `demo_user` / `User@123`

### Tùy chọn 2: dữ liệu demo local nhanh
1. Tạo cơ sở dữ liệu `coffee`
2. Import `sql/schema.sql`
3. Import `sql/seed-lite.sql`

### Tùy chọn 3: dữ liệu local đầy đủ từ bản gốc
1. Tạo cơ sở dữ liệu `coffee`
2. Import `sql/schema.sql`
3. Import `sql/legacy/seed-full-from-original.sql`

## Thiết lập thủ công trong IDE

Dự án hiện tại chưa dùng Maven hoặc Gradle, nên một số dependency cần được cấu hình thủ công trong IDE.

### 1. Thêm thư viện cục bộ từ thư mục `lib/`

Thêm tất cả file `.jar` trong thư mục `lib/` vào project libraries hoặc classpath.

Ví dụ gồm:
- `amqp-client`
- `gson`
- `mysql-connector-j`
- `javax.mail`
- `slf4j`
- `fontawesomefx`
- `jxmaps`

### 2. Thêm JavaFX SDK 25 thủ công

Tải JavaFX SDK 25 và cấu hình trong IDE.

Đảm bảo thư mục `lib` của JavaFX được dùng trong:
- project libraries
- module path / VM options

Ví dụ VM options:

```bash
--module-path "C:\path\to\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

### 3. Mở và chạy class chính

Chạy:

```text
src/application/Main.java
```

## Cách chạy

1. Cài JDK 23
2. Cài JavaFX SDK 25
3. Cài MySQL và tạo cơ sở dữ liệu `coffee`
4. Import các file SQL
5. Cài Erlang/OTP
6. Cài và khởi động RabbitMQ
7. Tạo file `app.properties` cục bộ
8. Mở dự án trong IDE
9. Thêm tất cả file `.jar` trong thư mục `lib/` vào project
10. Cấu hình JavaFX SDK 25 trong IDE
11. Thiết lập JavaFX VM options
12. Chạy `src/application/Main.java`

Ví dụ JavaFX VM options:

```bash
--module-path "C:\path\to\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

## Lưu ý quan trọng

- JavaFX không còn được đóng gói sẵn trong JDK 23, vì vậy cần thêm JavaFX SDK thủ công.
- Dự án hiện vẫn phụ thuộc vào việc thiết lập thủ công trong IDE vì chưa dùng Maven hoặc Gradle.
- RabbitMQ và Erlang/OTP là bắt buộc cho tính năng chat.
- Gửi email yêu cầu cấu hình SMTP hợp lệ.
- Nếu dùng Gmail SMTP, bạn phải dùng **Gmail App Password** thay vì mật khẩu Gmail thông thường.
- `seed.sql` là bộ seed demo được khuyến nghị cho mục đích công khai.
- `seed-lite.sql` là lựa chọn nhẹ hơn cho thiết lập local nhanh.
- `sql/legacy/seed-full-from-original.sql` lớn hơn nhiều và có thể không phù hợp cho repo public.

## Tài liệu portfolio

- Xử lý sự cố cài đặt: `docs/SETUP_TROUBLESHOOTING_VI.md`

## Giới hạn

- Dự án này được thiết kế cho mục đích local và portfolio, và vẫn phụ thuộc vào việc thiết lập môi trường desktop.
- Việc thiết lập RabbitMQ, SMTP và JavaFX có thể mất thời gian trên máy mới.
- Một số file UI và resource vẫn giữ cách tổ chức từ dự án gốc thay vì layout Maven hoặc Gradle đầy đủ.

## Tác giả

- **Tên:** Nguyễn Lâm Trường
- **Email:** lamtruongnguyen2004@gmail.com
- **GitHub:** [https://github.com/truongnguyen3006](https://github.com/truongnguyen3006)
