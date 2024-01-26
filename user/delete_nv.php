<?php
require "dbCon.php";

$manv = $_POST['manv'];

$query = "DELETE FROM nhanvien WHERE manv = '$manv'";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>
