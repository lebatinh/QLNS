<?php
require "dbCon.php";

// Lấy dữ liệu từ yêu cầu POST
$manv = $_POST['manv'];
$t2 = $_POST['t2'];
$t3 = $_POST['t3'];
$t4 = $_POST['t4'];
$t5 = $_POST['t5'];
$t6 = $_POST['t6'];
$t7 = $_POST['t7'];
$cn = $_POST['cn'];

$query = "UPDATE dk_lich SET t2 = '$t2', t3 = '$t3', t4 = '$t4', t5 = '$t5', t6 = '$t6', t7 = '$t7', cn = '$cn' WHERE manv = '$manv' ";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>