<?php
require("conn.php");

if(isset($_POST['name'])&&$_POST['name']!=''
&&$_POST['username']!=''&&isset($_POST['username'])&&$_POST['usertype']!=''&&isset($_POST['usertype']))
 {  
    $name=mysqli_real_escape_string($con,$_POST['name']);
    $username=mysqli_real_escape_string($con,$_POST['username']);
    $usertype=$_POST['usertype'];
 
 
  //handle lgoin for admin
   if($usertype=="Admin")
   {

$res=mysqli_query($con,"update elearn_admin set status='Rejected'   where username='$username'");// or die("user not found");


 }
 //handle login for teacher
 else if($usertype=="Teacher")
 {
   $res=mysqli_query($con,"update elearn_teach set status='Rejected'  where username='$username'");// or die("user not found");

     
 }
 //handle login for student 
 else if($usertype=="Student")
 {
    $res=mysqli_query($con,"update elearn_stud set status='Rejected'  where username='$username'");// or die("user not found");
     
 }
 
 
 if(mysqli_affected_rows($con)>0)
{  
  echo "Request Rejected";
}
  else
    {
     echo "username not found";
    }
mysqli_close($con);
}
?>