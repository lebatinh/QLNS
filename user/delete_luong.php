<?php
require "dbCon.php";

$maNv = $_POST['maNv'];

$query = "DELETE FROM luong WHERE maNv = '$maNv'";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>