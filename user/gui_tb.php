<?php
require "dbCon.php";

// Nhận dữ liệu từ yêu cầu POST
$tb = isset($_POST['tb']) ? $_POST['tb'] : null;
$tg = isset($_POST['tg']) ? $_POST['tg'] : null;
$phamvi = isset($_POST['phamvi']) ? $_POST['phamvi'] : null;

// Kiểm tra nếu là GuiTbAll hoặc GuiTbChoice
if ($phamvi == 'all') {
    // GuiTbAll
    $query = "INSERT INTO thongbao (tb, tg, phamvi) VALUES ('$tb', '$tg', 'all')";
} else {
    // GuiTbChoice
    $selectedMaNvListJson = isset($_POST['manv']) ? $_POST['manv'] : null;
    $selectedMaNvList = json_decode($selectedMaNvListJson, true);

    if ($selectedMaNvList !== null) {
        // Kiểm tra xem $selectedMaNvList có giá trị hợp lệ không
        if (!empty($selectedMaNvList)) {
            $manvList = implode(",", $selectedMaNvList);
            $query = "INSERT INTO thongbao (tb, tg, phamvi) VALUES ('$tb', '$tg', '$manvList')";
        } else {
            // Trường hợp $selectedMaNvList rỗng
            echo "rong";
            exit();
        }
    } else {
        // Xử lý khi $selectedMaNvList không hợp lệ
        echo "error";
        exit();
    }
}

if (mysqli_query($connect, $query)) {
    // Thành công
    echo "success";
} else {
    // Lỗi
    echo "fail";
}
?>
