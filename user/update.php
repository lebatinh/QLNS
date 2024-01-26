<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$email = $_POST['email'];
$pass = $_POST['pass'];

$query = "UPDATE user SET pass = '$pass' WHERE email = '$email' ";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>