<?php
require("conn.php");

if(isset($_POST['verify'])&&$_POST['verify']!=null&&isset($_POST['username'])&&$_POST['username']!=null&&isset($_POST['usertype'])&&$_POST['usertype']!=null)
 { 
   
     $un=mysqli_real_escape_string($con,$_POST['username']);
     $verify=$_POST['verify'];
     $usertype=$_POST['usertype'];   
  
if($usertype=="Admin")
{
  $res=mysqli_query($con,"SELECT * FROM elearn_admin where username='$un'") or die("<br>query unsuccessful for select");

    mysqli_query($con,"Update elearn_admin set verified='$verify' where username='$un'") or die("<br>query unsuccessful for update");
   echo "Admin verified";      
}
else if($usertype=="Teacher")
{
     $res=mysqli_query($con,"SELECT * FROM elearn_teach where username='$un'") or die("<br>query unsuccessful for select");

    mysqli_query($con,"Update elearn_teach set verified='$verify' where username='$un'") or die("<br>query unsuccessful for update");
    echo "Teacher verified";      
    
}
else if($usertype=="Student")
{
     $res=mysqli_query($con,"SELECT * FROM elearn_stud where username='$un'") or die("<br>query unsuccessful for select");

    mysqli_query($con,"Update elearn_stud set verified='$verify' where username='$un'") or die("<br>query unsuccessful for update");
    echo "Student verified";      
    
}



mysqli_close($con);
 }