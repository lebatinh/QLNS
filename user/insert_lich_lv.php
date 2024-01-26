<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$hinhanh = $_POST['hinhanh'];
$manv = $_POST['manv'];
$hoten = $_POST['hoten'];
$t2 = $_POST['t2'];
$t3 = $_POST['t3'];
$t4 = $_POST['t4'];
$t5 = $_POST['t5'];
$t6 = $_POST['t6'];
$t7 = $_POST['t7'];
$cn = $_POST['cn'];
$startDate = $_POST['startDate'];
$endDate = $_POST['endDate'];

// Truy vấn SQL với tham số 
$query = "INSERT INTO lich_lv VALUES ('$hinhanh', '$manv', '$hoten', '$t2', '$t3', '$t4', '$t5', '$t6', '$t7', '$cn', '$startDate', '$endDate')";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}

?>