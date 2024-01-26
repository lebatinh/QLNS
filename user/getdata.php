<?php
require "dbCon.php";

$query = "SELECT * FROM user";

$data = mysqli_query($connect, $query);

// 1. Tạo class User
class User{

    public $Email;
    public $Manv;
    public $Pass;
    
    function __construct($email, $manv, $pass){
        $this -> Email = $email;
        $this -> Manv = $manv;
        $this -> Pass = $pass;
    }
}
//2. Tạo mảng 
$arrUser = array();
//3. Thêm phần tử vào mảng
while($row = mysqli_fetch_assoc($data)){
    array_push($arrUser, new User($row['email'], $row['manv'], $row['pass']));
}
//4. Chuyển định dạng mảng sang JSON 
echo json_encode($arrUser);

?>
