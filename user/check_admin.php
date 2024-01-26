<?php
require "dbCon.php";

$query = "SELECT * FROM admin";

$data = mysqli_query($connect, $query);

// 1. Tạo class Admin
class Admin{

    public $MaNv;
    public $Email;
    public $Pass;
    
    function __construct($email, $manv, $pass){
        $this -> Email = $email;
        $this -> MaNv = $manv;
        $this -> Pass = $pass;
    }
}
//2. Tạo mảng 
$arrAdmin = array();
//3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrAdmin, new Admin($row['email'], $row['manv'], $row['pass']));
}
//4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrAdmin);

?>
