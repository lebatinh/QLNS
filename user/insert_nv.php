<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$manv = $_POST['manv'];
$hoten = $_POST['hoten'];
$ngaysinh = $_POST['ngaysinh'];
$gioitinh = $_POST['gioitinh'];
$quequan = $_POST['quequan'];
$diachi = $_POST['diachi'];
$chucvu = $_POST['chucvu'];
$sdt = $_POST['sdt'];
$hinhanh = $_POST['hinhanh'];

// Truy vấn SQL với tham số 
$query = "INSERT INTO nhanvien VALUES ('$manv', '$hoten', '$ngaysinh', '$gioitinh', '$quequan', '$diachi', '$chucvu', '$sdt', '$hinhanh')";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}

?>