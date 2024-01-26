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
$query = "UPDATE nhanvien SET hoten = '$hoten', ngaysinh = '$ngaysinh', gioitinh = '$gioitinh', quequan = '$quequan', diachi = '$diachi', chucvu = '$chucvu', sdt = '$sdt', hinhanh = '$hinhanh' WHERE manv = '$manv' ";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>