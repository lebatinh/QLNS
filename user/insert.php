<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$manv = $_POST['manv'];
$email = $_POST['email'];
$pass = $_POST['pass'];

// Kiểm tra xem `manv` đã tồn tại trong bảng `nhanvien` chưa
$checkQuery = "SELECT COUNT(*) as count FROM nhanvien WHERE manv = '$manv'";
$checkResult = mysqli_query($connect, $checkQuery);

if ($checkResult) {
    $row = mysqli_fetch_assoc($checkResult);
    $count = $row['count'];

    if ($count > 0) {
        // `manv` đã tồn tại trong bảng `nhanvien`, tiến hành thêm vào bảng `user`
        $insertQuery = "INSERT INTO user VALUES ('$manv', '$email', '$pass')";

        if (mysqli_query($connect, $insertQuery)) {
            // Thành công
            echo "success";
        } else {
            // Lỗi khi thêm vào bảng `user`
            echo "fail";
        }
    } else {
        // `manv` không tồn tại trong bảng `nhanvien`
        echo "not_exists";
    }
} else {
    // Lỗi khi kiểm tra `manv`
    echo "fail";
}

mysqli_close($connect);
?>
