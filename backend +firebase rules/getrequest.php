<?php

require("conn.php");
header('Cache-Control: max-age=10');
if($_POST['usertype'] != '' && isset($_POST['usertype'])) {
    $usertype=$_POST['usertype'];
    if($usertype == "Admin") {
       $res =mysqli_query($con,"call GetAdminUsers()");
    //$res = mysqli_query($con, "SELECT name, username FROM elearn_admin WHERE status IS null");
    } else if($usertype == "Teacher") {
         $res =mysqli_query($con,"call GetTeacherUsers()");
       //   $res = mysqli_query($con, "SELECT name, username FROM elearn_teach WHERE status IS null");
    } else if($usertype == "Student") {
        $res =mysqli_query($con,"call GetStudentUsers()");
       // $res = mysqli_query($con, "SELECT name, username FROM elearn_stud WHERE status IS null");
    }

    $rows = []; // Initialize an empty array to store the rows

    if(mysqli_num_rows($res) > 0) {  
        while ($row = mysqli_fetch_assoc($res)) {  
            $rows[] = $row; // Append the row to the array
        }
           // Convert $rows array to JSON
    $jsonData = json_encode($rows); //converts array of PHP into JSON array

    echo $jsonData; // Output the JSON data
    }
    else
    {
        echo "no new request";
    }
   

    mysqli_close($con);

 
    
}

?>
