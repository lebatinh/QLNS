<?php
require "dbCon.php";

$manv = $_POST['manv'];

$query = "DELETE FROM lich_lv WHERE manv = '$manv'";

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}

?>
