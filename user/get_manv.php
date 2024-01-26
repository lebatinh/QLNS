<?php
require "dbCon.php";

// Truy vấn cơ sở dữ liệu để lấy hoten
$query = "SELECT manv, hoten, chucvu, sdt, hinhanh FROM nhanvien";

$data = mysqli_query($connect, $query);

// 1. Tạo class NV
class NV{
    public $MaNv;
    public $HoTen;
    public $ChucVu;
    public $SDT;
    public $HinhAnh;
    
    function __construct($manv, $hoten, $chucvu, $sdt, $hinhanh){
        $this -> MaNv = $manv;
        $this -> HoTen = $hoten;
        $this -> ChucVu = $chucvu;
        $this -> SDT = $sdt;
        $this -> HinhAnh = $hinhanh;
    }
}

// 2. Tạo mảng 
$arrNV = array();

// 3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrNV, new NV($row['manv'], $row['hoten'], $row['chucvu'], $row['sdt'], $row['hinhanh']));
}

// 4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrNV);

?>
