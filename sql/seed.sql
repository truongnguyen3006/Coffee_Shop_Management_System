-- Lightweight demo seed for quick local setup
-- Recommended for portfolio/demo use because it avoids large BLOB inserts.
-- Default accounts:
--   admin / Admin@123
--   demo_user / User@123

USE `coffee`;

SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `users`
(`userID`,`userName`,`email`,`fullName`,`gender`,`province`,`district`,`ward`,`address`,`phoneNumber`,`userType`,`password`,`date`)
VALUES
(1,'admin','admin@example.com','System Administrator','Nam','Thành phố Cần Thơ','Ninh Kiều','An Khánh','01 Demo Street','0900000001','admin','PBKDF2$65536$YBkVhKzDw4OHyXIQvKh8Dw==$eaNCFk7/DKmXQdVHGf/mvhxCvmLgJ5r9k0PhcSVakSI=',NOW()),
(2,'demo_user','user@example.com','Demo Customer','Nữ','Thành phố Cần Thơ','Ninh Kiều','Xuân Khánh','02 Demo Street','0900000002','user','PBKDF2$65536$MWILLP3YFvQX1jH7ilKGmw==$aKL0nn350SgS/YmeyV/1AkPSEQ61tr+glTmF+nrYj/w=',NOW());

INSERT INTO `toppings` (`toppingID`,`toppingName`,`toppingPrice`) VALUES
(1,'Trân châu trắng',10000),
(2,'Foam phô mai',12000),
(3,'Thạch cà phê',10000),
(4,'Sốt caramel',12000),
(5,'Hạt sen',12000);

INSERT INTO `products`
(`productID`,`productCode`,`thumbnail`,`productName`,`typeProduct`,`price`,`description`,`status`,`bestSeller`,`date`)
VALUES
(1,'CF001',NULL,'Cà Phê Sữa Đá','Cà phê',29000,'Vietnamese coffee with condensed milk.','Đang hoạt động',1,NOW()),
(2,'CF002',NULL,'Bạc Xỉu','Cà phê',32000,'Sweet milk coffee for casual drinkers.','Đang hoạt động',NULL,NOW()),
(3,'TS001',NULL,'Trà Sữa Trân Châu','Trà sữa',39000,'Classic milk tea with pearls.','Đang hoạt động',1,NOW()),
(4,'TS002',NULL,'Trà Đào Cam Sả','Trà trái cây',42000,'Peach tea with orange and lemongrass.','Đang hoạt động',NULL,NOW()),
(5,'FR001',NULL,'Freeze Chocolate','Freeze',49000,'Blended chocolate ice drink.','Đang hoạt động',NULL,NOW()),
(6,'CK001',NULL,'Bánh Mì Gà Xé','Bánh',25000,'Savory shredded chicken bread.','Đang hoạt động',NULL,NOW());

INSERT INTO `orders`
(`orderID`,`userID`,`addressOrder`,`phoneOrder`,`notes`,`orderDate`,`totalPrice`)
VALUES
(1,2,'02 Demo Street, Xuân Khánh, Ninh Kiều, Cần Thơ','0900000002','Ít đá',NOW(),81000);

INSERT INTO `order_items`
(`orderItemID`,`orderID`,`productID`,`quantity`,`size`,`toppingName`,`unitPrice`)
VALUES
(1,1,3,1,'M','Trân châu trắng',49000),
(2,1,6,1,'M','',32000);

INSERT INTO `chat_messages` (`id`,`sender`,`receiver`,`content`,`timestamp`) VALUES
(1,'demo_user','Admin','Xin chào, mình muốn hỏi về menu hôm nay.',NOW()),
(2,'Admin','demo_user','Chào bạn, mình có thể hỗ trợ bạn chọn đồ uống phù hợp.',NOW());

SET FOREIGN_KEY_CHECKS=1;
