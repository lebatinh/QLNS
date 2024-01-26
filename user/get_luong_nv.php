<?php
require "dbCon.php";

$query = "SELECT * FROM luong";

$data = mysqli_query($connect, $query);

// 1. Tạo class Luong
class Luong {
        public $MaNv;
        public $HoTen;
        public $LuongCoBan;
        public $LyDoThuong;
        public $TienThuong;
        public $LyDoPhat;
        public $TienPhat;
        public $ChiPhiKhac;
        public $Tong;
        public $ThoiGian;

    function __construct($maNv ,$hoTen, $luongCoBan, $lyDoThuong, $tienThuong, $lyDoPhat, $tienPhat, $chiPhiKhac, $tong, $thoiGian){
            $this->MaNv = $maNv;
            $this->HoTen = $hoTen;
            $this->LuongCoBan = $luongCoBan;
            $this->LyDoThuong = $lyDoThuong;
            $this->TienThuong = $tienThuong;
            $this->LyDoPhat = $lyDoPhat;
            $this->TienPhat = $tienPhat;
            $this->ChiPhiKhac = $chiPhiKhac;
            $this->Tong = $tong;
            $this->ThoiGian = $thoiGian;
        }
    }

// 2. Tạo mảng
$arrLuong = array();
// 3. Thêm phần tử vào mảng
while ($row = mysqli_fetch_assoc($data)) {
        array_push($arrLuong, new Luong($row['maNv'], $row['hoTen'], $row['luongCoBan'], $row['lyDoThuong'], $row['tienThuong'], $row['lyDoPhat'], $row['tienPhat'], $row['chiPhiKhac'], $row['tong'], $row['thoiGian']));
}


        // 4. Chuyển định dạng mảng sang JSON
        echo json_encode($arrLuong);

?>
