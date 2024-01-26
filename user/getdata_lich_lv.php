<?php
require "dbCon.php";

$query = "SELECT * FROM lich_lv";

$data = mysqli_query($connect, $query);

// 1. Tạo class LichLv
class LichLv {
        public $HinhAnh;
        public $MaNv;
        public $HoTen;
        public $T2;
        public $T3;
        public $T4;
        public $T5;
        public $T6;
        public $T7;
        public $Cn;
        public $StartDate;
        public $EndDate;

    function __construct($hinhanh, $manv ,$hoten, $t2, $t3, $t4, $t5, $t6, $t7, $cn, $startDate, $endDate){
            $this->HinhAnh = $hinhanh;
            $this->MaNv = $manv;
            $this->HoTen = $hoten;
            $this->T2 = $t2;
            $this->T3 = $t3;
            $this->T4 = $t4;
            $this->T5 = $t5;
            $this->T6 = $t6;
            $this->T7 = $t7;
            $this->Cn = $cn;
            $this->StartDate = $startDate;
            $this->EndDate = $endDate;
        }
    }

// 2. Tạo mảng
$arrLichLv = array();
// 3. Thêm phần tử vào mảng
while ($row = mysqli_fetch_assoc($data)) {
        array_push($arrLichLv, new LichLv($row['hinhanh'], $row['manv'], $row['hoten'], $row['t2'], $row['t3'], $row['t4'], $row['t5'], $row['t6'], $row['t7'], $row['cn'], $row['startDate'], $row['endDate']));
}


        // 4. Chuyển định dạng mảng sang JSON
        echo json_encode($arrLichLv);

?>
