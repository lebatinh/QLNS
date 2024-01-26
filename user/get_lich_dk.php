<?php
require "dbCon.php";

$query = "SELECT * FROM dk_lich";

$data = mysqli_query($connect, $query);

// 1. Tạo class LichDK
class LichDK{

    public $HinhAnh;
    public $MaNv;
    public $HoTen;
    public $ChucVu;
    public $SDT;
    public $LyDo;
    public $T2;
    public $T3;
    public $T4;
    public $T5;
    public $T6;
    public $T7;
    public $Cn;
    public $Tg;
    
    function __construct($hinhanh, $manv, $hoten, $chucvu, $sdt, $lydo, $t2, $t3, $t4, $t5, $t6, $t7, $cn, $tg){
        $this -> HinhAnh = $hinhanh;
        $this -> MaNv    = $manv;
        $this -> HoTen   = $hoten;
        $this -> ChucVu  = $chucvu;
        $this -> SDT     = $sdt;
        $this -> LyDo    = $lydo;
        $this -> T2      = $t2;
        $this -> T3      = $t3;
        $this -> T4      = $t4;
        $this -> T5      = $t5;
        $this -> T6      = $t6;
        $this -> T7      = $t7;
        $this -> Cn      = $cn;
        $this -> Tg      = $tg;
    }
}
//2. Tạo mảng 
$arrLichDK = array();
//3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrLichDK, new LichDK($row['hinhanh'], $row['manv'], $row['hoten'], $row['chucvu'], $row['sdt'], $row['lydo'], $row['t2'], $row['t3'], $row['t4'], $row['t5'], $row['t6'], $row['t7'], $row['cn'], $row['tg']));
}
//4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrLichDK);

?>
