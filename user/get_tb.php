<?php
require "dbCon.php";

// Truy vấn cơ sở dữ liệu để lấy hoten
$query = "SELECT tb, tg, phamvi FROM thongbao ORDER BY tg DESC";

$data = mysqli_query($connect, $query);

// 1. Tạo class TB
class TB{
    public $ThongBao;
    public $ThoiGian;
    public $PhamVi;
    
    function __construct($tb, $tg, $phamvi){
        $this -> ThongBao = $tb;
        $this -> ThoiGian = $tg;
        $this -> PhamVi = $phamvi;
    }
}

// 2. Tạo mảng 
$arrTB = array();

// 3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrTB, new TB($row['tb'], $row['tg'], $row['phamvi']));
}

// 4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrTB);

?>
