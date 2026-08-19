-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 19, 2025 at 02:06 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cbs`
--

-- --------------------------------------------------------

--
-- Table structure for table `menuitems`
--

CREATE TABLE `menuitems` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `price` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menuitems`
--

INSERT INTO `menuitems` (`id`, `name`, `category`, `price`) VALUES
(1, 'Tuna Sandwich', 'Pastry and Sandwiches', 50),
(2, 'Grilled Cheese', 'Pastry and Sandwiches', 35),
(3, 'Ham and Cheese', 'Pastry and Sandwiches', 45),
(4, 'Cucumber and Egg', 'Pastry and Sandwiches', 50),
(5, 'Baguette', 'Pastry and Sandwiches', 75),
(6, 'Croissant', 'Pastry and Sandwiches', 75),
(7, 'Caramel Macchiato', 'Beverages', 60),
(8, 'Hot Chocolate', 'Beverages', 35),
(9, 'Spanish Latte', 'Beverages', 60),
(10, 'Iced Latte', 'Beverages', 55),
(11, 'Apple Juice', 'Beverages', 20),
(12, 'Orange Juice', 'Beverages', 20),
(13, 'Mozzarella Sticks', 'Appetizers', 35),
(14, 'Greek Salad Cups', 'Appetizers', 50),
(15, 'Caprese Skewers', 'Appetizers', 50),
(16, 'Bruschetta', 'Appetizers', 55),
(17, 'Devilled Eggs', 'Appetizers', 55),
(18, 'French Onion Soup', 'Appetizers', 65);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `Order_ID` int(11) NOT NULL,
  `SubTotal` int(11) NOT NULL,
  `Tax` int(11) NOT NULL,
  `Total` int(11) NOT NULL,
  `Cash` decimal(10,2) NOT NULL,
  `Change_amount` decimal(10,2) NOT NULL,
  `Order_time` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`Order_ID`, `SubTotal`, `Tax`, `Total`, `Cash`, `Change_amount`, `Order_time`) VALUES
(1, 55, 10, 65, 70.00, 5.00, '2025-05-09 10:42:50'),
(2, 120, 10, 130, 150.00, 20.00, '2025-05-09 10:43:29'),
(3, 40, 10, 50, 50.00, 0.00, '2025-05-09 10:46:26'),
(4, 235, 10, 245, 250.00, 5.00, '2025-05-09 15:19:45'),
(5, 85, 10, 95, 100.00, 5.00, '2025-05-09 16:42:09'),
(6, 395, 10, 405, 505.00, 100.00, '2025-05-10 14:51:29'),
(7, 120, 10, 130, 150.00, 20.00, '2025-05-13 12:34:58'),
(8, 150, 10, 160, 200.00, 40.00, '2025-05-13 12:43:04'),
(9, 130, 10, 140, 140.00, 0.00, '2025-05-13 12:45:05'),
(10, 150, 10, 160, 500.00, 340.00, '2025-05-13 12:48:35'),
(11, 210, 10, 220, 250.00, 30.00, '2025-05-13 12:51:30'),
(12, 210, 10, 220, 250.00, 30.00, '2025-05-13 12:51:49'),
(13, 210, 10, 220, 250.00, 30.00, '2025-05-13 12:52:30'),
(14, 75, 10, 85, 100.00, 15.00, '2025-05-13 18:41:35'),
(15, 320, 10, 330, 350.00, 20.00, '2025-05-14 10:41:22');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `OrderItem_ID` int(11) NOT NULL,
  `Order_ID` int(11) NOT NULL,
  `MenuItem_ID` int(11) DEFAULT NULL,
  `Item_Name` varchar(100) DEFAULT NULL,
  `Quantity` int(11) DEFAULT NULL,
  `Line_Subtotal` int(11) DEFAULT NULL,
  `Tax` int(11) DEFAULT NULL,
  `Total` int(11) DEFAULT NULL,
  `Cash` decimal(10,2) DEFAULT NULL,
  `Change_Amount` decimal(10,2) DEFAULT NULL,
  `Order_Time` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`OrderItem_ID`, `Order_ID`, `MenuItem_ID`, `Item_Name`, `Quantity`, `Line_Subtotal`, `Tax`, `Total`, `Cash`, `Change_Amount`, `Order_Time`) VALUES
(1, 1, 10, 'Iced Latte', 1, 45, 10, 55, 70.00, 5.00, '2025-05-09 10:42:51'),
(2, 2, 1, 'Tuna Sandwich', 1, 40, 10, 50, 150.00, 20.00, '2025-05-09 10:43:29'),
(3, 2, 2, 'Grilled Cheese', 2, 60, 10, 70, 150.00, 20.00, '2025-05-09 10:43:29'),
(4, 3, 11, 'Apple Juice', 2, 30, 10, 40, 50.00, 0.00, '2025-05-09 10:46:26'),
(5, 4, 1, 'Tuna Sandwich', 1, 40, 10, 50, 250.00, 5.00, '2025-05-09 15:19:45'),
(6, 4, 2, 'Grilled Cheese', 4, 130, 10, 140, 250.00, 5.00, '2025-05-09 15:19:45'),
(7, 4, 3, 'Ham and Cheese', 1, 35, 10, 45, 250.00, 5.00, '2025-05-09 15:19:45'),
(8, 5, 8, 'Hot Chocolate', 1, 25, 10, 35, 100.00, 5.00, '2025-05-09 16:42:09'),
(9, 5, 9, 'Spanish Latte', 1, 40, 10, 50, 100.00, 5.00, '2025-05-09 16:42:09'),
(10, 6, 4, 'Cucumber and Egg', 1, 35, 10, 45, 505.00, 100.00, '2025-05-10 14:51:29'),
(11, 6, 9, 'Spanish Latte', 1, 40, 10, 50, 505.00, 100.00, '2025-05-10 14:51:29'),
(12, 6, 9, 'Spanish Latte', 1, 40, 10, 50, 505.00, 100.00, '2025-05-10 14:51:29'),
(13, 6, 1, 'Tuna Sandwich', 5, 240, 10, 250, 505.00, 100.00, '2025-05-10 14:51:29'),
(14, 7, 1, 'Tuna Sandwich', 1, 40, 10, 50, 150.00, 20.00, '2025-05-13 12:34:58'),
(15, 7, 2, 'Grilled Cheese', 1, 25, 10, 35, 150.00, 20.00, '2025-05-13 12:34:58'),
(16, 7, 2, 'Grilled Cheese', 1, 25, 10, 35, 150.00, 20.00, '2025-05-13 12:34:58'),
(17, 8, 10, 'Iced Latte', 1, 45, 10, 55, 200.00, 40.00, '2025-05-13 12:43:04'),
(18, 8, 9, 'Spanish Latte', 1, 40, 10, 50, 200.00, 40.00, '2025-05-13 12:43:04'),
(19, 8, 4, 'Cucumber and Egg', 1, 35, 10, 45, 200.00, 40.00, '2025-05-13 12:43:04'),
(20, 9, 1, 'Tuna Sandwich', 1, 40, 10, 50, 140.00, 0.00, '2025-05-13 12:45:05'),
(21, 9, 2, 'Grilled Cheese', 1, 25, 10, 35, 140.00, 0.00, '2025-05-13 12:45:05'),
(22, 9, 3, 'Ham and Cheese', 1, 35, 10, 45, 140.00, 0.00, '2025-05-13 12:45:05'),
(23, 10, 1, 'Tuna Sandwich', 1, 40, 10, 50, 500.00, 340.00, '2025-05-13 12:48:35'),
(24, 10, 1, 'Tuna Sandwich', 1, 40, 10, 50, 500.00, 340.00, '2025-05-13 12:48:35'),
(25, 10, 1, 'Tuna Sandwich', 1, 40, 10, 50, 500.00, 340.00, '2025-05-13 12:48:35'),
(26, 11, 6, 'Croissant', 2, 140, 10, 150, 250.00, 30.00, '2025-05-13 12:51:30'),
(27, 11, 7, 'Caramel Macchiato', 1, 50, 10, 60, 250.00, 30.00, '2025-05-13 12:51:30'),
(28, 12, 6, 'Croissant', 2, 140, 10, 150, 250.00, 30.00, '2025-05-13 12:51:49'),
(29, 12, 7, 'Caramel Macchiato', 1, 50, 10, 60, 250.00, 30.00, '2025-05-13 12:51:49'),
(30, 13, 6, 'Croissant', 2, 140, 10, 150, 250.00, 30.00, '2025-05-13 12:52:30'),
(31, 13, 7, 'Caramel Macchiato', 1, 50, 10, 60, 250.00, 30.00, '2025-05-13 12:52:30'),
(32, 14, 10, 'Iced Latte', 1, 45, 10, 55, 100.00, 15.00, '2025-05-13 18:41:35'),
(33, 14, 11, 'Apple Juice', 1, 10, 10, 20, 100.00, 15.00, '2025-05-13 18:41:35'),
(34, 15, 1, 'Tuna Sandwich', 1, 40, 10, 50, 350.00, 20.00, '2025-05-14 10:41:22'),
(35, 15, 12, 'Orange Juice', 1, 10, 10, 20, 350.00, 20.00, '2025-05-14 10:41:22'),
(36, 15, 14, 'Greek Salad Cups', 5, 240, 10, 250, 350.00, 20.00, '2025-05-14 10:41:22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `menuitems`
--
ALTER TABLE `menuitems`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`Order_ID`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`OrderItem_ID`),
  ADD KEY `Order_ID` (`Order_ID`),
  ADD KEY `MenuItem_ID` (`MenuItem_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `menuitems`
--
ALTER TABLE `menuitems`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `Order_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `OrderItem_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`Order_ID`) REFERENCES `orders` (`Order_ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`MenuItem_ID`) REFERENCES `menuitems` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
