# Hướng dẫn cài đặt và xử lý các lỗi thường gặp

---

## 1. JavaFX không chạy

### Dấu hiệu thường gặp
Bạn có thể gặp một trong các tình huống sau:

- IDE báo thiếu các package `javafx.*`
- Chạy `Main.java` nhưng ứng dụng không khởi động
- Xuất hiện lỗi liên quan đến `module-path` hoặc JavaFX modules

### Nguyên nhân phổ biến
JavaFX không còn được tích hợp sẵn trong một số bản JDK, vì vậy nếu chỉ cài JDK mà chưa cài JavaFX SDK riêng thì project sẽ không chạy đúng.

### Cách khắc phục
- Cài đặt **JavaFX SDK 25**
- Thêm thư viện JavaFX vào IDE
- Cấu hình đúng VM options khi chạy project

Ví dụ VM options:

```bash
--module-path "C:\path\to\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

### Cần kiểm tra thêm
- Đường dẫn tới thư mục `lib` của JavaFX có đúng không
- IDE đã nhận thư viện JavaFX chưa
- Cấu hình Run/Debug có lưu đúng VM options chưa

---

## 2. Không thể kết nối MySQL

### Dấu hiệu thường gặp
- Ứng dụng chạy nhưng báo lỗi kết nối cơ sở dữ liệu
- Không đọc được dữ liệu khi mở chương trình
- Xuất hiện lỗi liên quan đến username, password hoặc database không tồn tại

### Những điều cần kiểm tra
- MySQL đã được khởi động chưa
- Database `coffee` đã được tạo chưa
- File `app.properties` đã cấu hình đúng thông tin kết nối chưa

### Cách khắc phục đề xuất
Để khởi tạo dữ liệu nhanh cho project, nên thực hiện theo thứ tự sau:

- Import file `sql/schema.sql` để tạo cấu trúc database
- Import file `sql/seed-lite.sql` để thêm dữ liệu mẫu phục vụ test nhanh

### Gợi ý kiểm tra
Nếu vẫn lỗi, hãy xem lại:
- tên database
- cổng MySQL
- username/password
- quyền truy cập của tài khoản database

---

## 3. RabbitMQ không chạy trên Windows

### Điều kiện cần trước khi cài
RabbitMQ trên Windows cần cài đặt **Erlang/OTP** trước.  
Nếu chưa có Erlang hoặc phiên bản Erlang không phù hợp, RabbitMQ có thể không khởi động được.

### Những điều cần kiểm tra
- Erlang/OTP đã được cài trước chưa
- RabbitMQ đã được cài đúng chưa
- RabbitMQ service có tồn tại không
- Cổng `5672` có đang hoạt động không
- Tài khoản mặc định của RabbitMQ có đúng không

### Nguyên nhân thường gặp
Nếu RabbitMQ cài xong nhưng không chạy, nguyên nhân phổ biến thường là:

- phiên bản RabbitMQ và Erlang không tương thích
- lỗi đường dẫn cài đặt hoặc lỗi cấu hình service
- service RabbitMQ cũ chưa được gỡ sạch trước khi cài lại

### Cách khắc phục đề xuất
- Gỡ RabbitMQ service cũ nếu đã cài trước đó
- Kiểm tra lại phiên bản Erlang đang dùng
- Cài lại RabbitMQ bằng phiên bản tương thích
- Đảm bảo RabbitMQ service đã chạy trước khi mở ứng dụng

---

## 4. Không thể gửi email

### Dấu hiệu thường gặp
- Chức năng gửi email không hoạt động
- Hệ thống báo lỗi xác thực SMTP
- Gửi mail thất bại dù ứng dụng vẫn chạy bình thường

### Những điều cần kiểm tra
- `mail.username` và `mail.password` trong `app.properties` đã đúng chưa
- Nếu dùng Gmail, có đang sử dụng **App Password** hay không
- Máy tính hoặc mạng hiện tại có đang chặn SMTP không

### Lưu ý
Nếu dùng Gmail, thông thường bạn **không nên dùng mật khẩu đăng nhập thông thường**, mà cần dùng **Gmail App Password** để hệ thống gửi mail ổn định hơn.

---

## 5. Giao diện bị lỗi khi chạy trên máy khác

### Tình trạng
Project có thể chạy bình thường trên máy phát triển ban đầu nhưng khi đem sang máy khác thì giao diện bị lỗi, mất ảnh, mất icon hoặc không load được CSS/FXML.

### Những phần đã được cải thiện
Project đã được chỉnh lại để giảm lỗi khi chuyển máy, bao gồm:

- loại bỏ các đường dẫn tuyệt đối như `D:/...`
- chuyển sang load ảnh, icon, CSS và FXML từ thư mục resource của project

### Nếu vẫn còn lỗi, hãy kiểm tra
- source path và resource path trong IDE đã cấu hình đúng chưa
- JavaFX SDK đã được gắn đúng chưa
- các thư viện cần thiết trong thư mục `lib/` đã được thêm vào project chưa

### Gợi ý
Khi import project vào IDE mới, nên kiểm tra lại toàn bộ cấu trúc thư mục thay vì chỉ mở riêng file source.

---

## 6. Thứ tự cài đặt nhanh để demo project

Nếu cần demo project trên một máy mới, nên thực hiện theo thứ tự sau để hạn chế lỗi:

1. Cài đặt JDK
2. Cài đặt JavaFX SDK
3. Cài đặt MySQL
4. Import `schema.sql` và `seed-lite.sql`
5. Cài đặt Erlang/OTP và RabbitMQ
6. Tạo và cấu hình file `app.properties`
7. Chạy `Main.java`

Thực hiện đúng thứ tự này sẽ giúp giảm đáng kể các lỗi thiếu dependency hoặc lỗi cấu hình môi trường.

---

## 7. Một số lỗi cài đặt thường gặp

### Lỗi `Module javafx.controls not found`
Đây là lỗi rất phổ biến khi JavaFX chưa được cấu hình đúng.

Cần kiểm tra lại:
- đã cài JavaFX SDK 25 chưa
- đã trỏ đúng tới thư mục `lib` của JavaFX trong `--module-path` chưa
- VM options đã được khai báo đúng trong IDE chưa

---

### Ảnh hoặc icon không hiển thị
Nguyên nhân thường là project không giữ đúng cấu trúc resource khi import vào IDE.

Cách xử lý:
- kiểm tra lại thư mục resources
- đảm bảo ảnh, icon, CSS và FXML vẫn nằm đúng vị trí
- không đổi cấu trúc thư mục nếu code đang load resource theo path tương đối

---

### Gửi email thất bại
Hãy kiểm tra lại các thông tin sau:

- SMTP host và port
- Gmail App Password nếu dùng Gmail
- giá trị `mail.username`
- giá trị `mail.password` trong `app.properties`

---

### Chat RabbitMQ không hoạt động
Nếu chức năng chat không chạy, trước hết hãy kiểm tra RabbitMQ service đã được khởi động chưa.  
Ứng dụng sẽ không thể xử lý phần chat nếu RabbitMQ chưa hoạt động.

---

### Ứng dụng chạy được trên máy này nhưng lỗi trên máy khác
Đây thường là lỗi do khác biệt môi trường cài đặt.

Những điểm cần kiểm tra lại gồm:
- đường dẫn JavaFX SDK
- nội dung file `app.properties`
- trạng thái hoạt động của MySQL
- trạng thái hoạt động của RabbitMQ
- các thư viện được thêm thủ công trong thư mục `lib/`

---

## 8. Kết luận

Phần lớn lỗi khi chạy project trên máy mới không đến từ code, mà đến từ môi trường cài đặt chưa đầy đủ hoặc cấu hình chưa đúng.  
Vì vậy, khi gặp lỗi, nên kiểm tra lần lượt theo thứ tự:

- JDK
- JavaFX
- MySQL
- RabbitMQ
- `app.properties`
- thư viện ngoài
- cấu trúc resources

Nếu làm đúng các bước trên, quá trình cài đặt và demo project sẽ ổn định hơn rất nhiều.
