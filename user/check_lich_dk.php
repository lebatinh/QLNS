<?php
require "dbCon.php";

$manv = $_POST['manv'];

// Sử dụng prepared statement để tránh SQL injection
$query = "SELECT manv FROM dk_lich WHERE manv = ?";

$stmt = mysqli_prepare($connect, $query);
mysqli_stmt_bind_param($stmt, "s", $manv);
mysqli_stmt_execute($stmt);

// Kiểm tra số dòng kết quả trả về
mysqli_stmt_store_result($stmt);
$rowCount = mysqli_stmt_num_rows($stmt);

if ($rowCount > 0) {
    // Có dữ liệu, trả về success
    echo "success";
} else {
    // Không có dữ liệu, trả về fail
    echo "fail";
}

// Đóng statement
mysqli_stmt_close($stmt);

// Đóng kết nối
mysqli_close($connect);
?>
