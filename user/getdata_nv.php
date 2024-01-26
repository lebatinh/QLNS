<?php
require "dbCon.php";

$query = "SELECT * FROM nhanvien";

$data = mysqli_query($connect, $query);

// 1. Tạo class NhanVien
class NhanVien{

    public $MaNv;
    public $HoTen;
    public $NgaySinh;
    public $GioiTinh;
    public $QueQuan;
    public $DiaChi;
    public $ChucVu;
    public $SDT;
    public $HinhAnh;
    
    function __construct($manv, $hoten, $ngaysinh, $gioitinh, $quequan, $diachi, $chucvu, $sdt, $hinhanh){
        $this -> MaNv     = $manv;
        $this -> HoTen    = $hoten;
        $this -> NgaySinh = $ngaysinh;
        $this -> GioiTinh = $gioitinh;
        $this -> QueQuan  = $quequan;
        $this -> DiaChi   = $diachi;
        $this -> ChucVu   = $chucvu;
        $this -> SDT      = $sdt;
        $this -> HinhAnh  = $hinhanh;
    }
}
//2. Tạo mảng 
$arrNhanVien = array();
//3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrNhanVien, new NhanVien($row['manv'], $row['hoten'], $row['ngaysinh'], $row['gioitinh'], $row['quequan'], $row['diachi'], $row['chucvu'], $row['sdt'], $row['hinhanh']));
}
//4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrNhanVien);

?>
