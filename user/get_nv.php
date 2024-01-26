<?php
require "dbCon.php";

    // Truy vấn cơ sở dữ liệu để lấy thông tin nhân viên
    $query = "SELECT manv, hoten, chucvu, sdt, hinhanh FROM nhanvien";
    $data = mysqli_query($connect, $query);

    if ($data) {
        // Tạo class NV
        class Nv
        {
            public $MaNv;
            public $HoTen;
            public $ChucVu;
            public $SDT;
            public $HinhAnh;

            function __construct($manv, $hoten, $chucvu, $sdt, $hinhanh)
            {
                $this->MaNv = $manv;
                $this->HoTen = $hoten;
                $this->ChucVu = $chucvu;
                $this->SDT = $sdt;
                $this->HinhAnh = $hinhanh;
            }
        }

        // Tạo mảng
        $arrNv = array();

        // Thêm phần tử vào mảng
        while ($row = mysqli_fetch_assoc($data)) {
            array_push($arrNv, new Nv($row['manv'], $row['hoten'], $row['chucvu'], $row['sdt'], $row['hinhanh']));
        }

        // Chuyển định dạng mảng sang JSON
        echo json_encode($arrNv);
    } else {
        // Truy vấn SQL không thành công
        echo "Lỗi truy vấn SQL: " . mysqli_error($connect);
    }


?>
