<?php
require("conn.php");

if(isset($_POST['name']) && $_POST['name'] != '' &&
   $_POST['username'] != '' && isset($_POST['username']) &&
   $_POST['usertype'] != '' && isset($_POST['usertype'])) {  
      
    $name = mysqli_real_escape_string($con, $_POST['name']);
    $username = mysqli_real_escape_string($con, $_POST['username']);
    $usertype = $_POST['usertype'];

    if($usertype == "Admin") { 
        $res = mysqli_query($con, "UPDATE elearn_admin SET status=NULL WHERE username='$username'");
    } else if($usertype == "Teacher") {
        $res = mysqli_query($con, "UPDATE elearn_teach SET status=NULL WHERE username='$username'");
    } else if($usertype == "Student") {
        $res = mysqli_query($con, "UPDATE elearn_stud SET status=NULL WHERE username='$username'");
    }

    if(mysqli_affected_rows($con) > 0) {  
        echo "Undo Request successful";
    } else {
        echo "Undo Request failed";
    }

    mysqli_close($con);
}
?>
