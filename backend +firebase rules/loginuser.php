<?php
require("conn.php");

if(isset($_POST['un'])&&$_POST['un']!=''
&&$_POST['pswd']!=''&&isset($_POST['pswd'])&&$_POST['usertype']!=''&&isset($_POST['usertype']))
 {  
     $usertype=$_POST['usertype'];
   $un=mysqli_real_escape_string($con,$_POST['un']);
   $pswd=mysqli_real_escape_string($con,$_POST['pswd']);
  //handle lgoin for admin
   if($usertype=="Admin")
   {

$res=mysqli_query($con,"select password,verified,status from elearn_admin where username='$un'");// or die("user not found");


 }
 //handle login for teacher
 else if($usertype=="Teacher")
 {
    $res=mysqli_query($con,"select password,verified,status from elearn_teach where username='$un'");// or die("user not found");

     
 }
 //handle login for student 
 else if($usertype=="Student")
 {
     $res=mysqli_query($con,"select password,verified,status from elearn_stud where username='$un'");// or die("user not found");
     
 }
 
 
 if(mysqli_num_rows($res)>0)
{  
   while($row=mysqli_fetch_assoc($res))
  {  
      
  
    if(md5($pswd)==$row['password']&&$row['verified']=="True"&&$row['status']=="Accepted")
    //to check password matches and user has received and entered the verification code
    { 
       
           echo "Login success";
       
    }
   
   else
    {
      echo "invalid credentials";
     }
  }  //while
}
  else
    {
     echo "username not found";
    }
mysqli_close($con);
}
?>