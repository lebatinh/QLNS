<?php
require "dbCon.php";

$query = "SELECT nhanvien.hinhanh, nhanvien.manv, nhanvien.hoten FROM nhanvien LEFT JOIN lich_lv ON nhanvien.manv = lich_lv.manv WHERE lich_lv.manv IS NULL";

$data = mysqli_query($connect, $query);

// 1. Tạo class NV
class NV {
    public $HinhAnh;
    public $MaNv;
    public $HoTen;

    function __construct($hinhanh, $manv, $hoten) {
        $this->HinhAnh = $hinhanh;
        $this->MaNv = $manv;
        $this->HoTen = $hoten;
    }
}

// 2. Tạo mảng 
$arrNV = array();

// 3. Thêm phần tử vào mảng
while ($row = mysqli_fetch_assoc($data)) {
    array_push($arrNV, new NV($row['hinhanh'], $row['manv'], $row['hoten']));
}

// 4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrNV);
?>
