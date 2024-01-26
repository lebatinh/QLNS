<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$maNv = $_POST['maNv'];
$hoTen = $_POST['hoTen'];
$luongCoBan = $_POST['luongCoBan'];
$lyDoThuong = $_POST['lyDoThuong'];
$tienThuong = $_POST['tienThuong'];
$lyDoPhat = $_POST['lyDoPhat'];
$tienPhat = $_POST['tienPhat'];
$chiPhiKhac = $_POST['chiPhiKhac'];
$tong = $_POST['tong'];
$thoiGian = $_POST['thoiGian'];

// Truy vấn SQL với tham số 
$query = "INSERT INTO luong VALUES ('$maNv', '$hoTen', '$luongCoBan', '$lyDoThuong', '$tienThuong', '$lyDoPhat', '$tienPhat', '$chiPhiKhac', '$tong', '$thoiGian')";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}

?>