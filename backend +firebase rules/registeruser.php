<?php
require("conn.php");
echo "hello";

if(isset($_POST['pswd'])&&$_POST['pswd']!=null&&isset($_POST['un'])&&$_POST['un']!=''&&isset($_POST['name'])&&$_POST['name']!=''&&isset($_POST['email'])&&$_POST['email']!=''&&isset($_POST['usertype'])&&$_POST['usertype']!='')
 { 
     echo "hello";
     $usertype=$_POST['usertype'];
     $name=mysqli_real_escape_string($con,$_POST['name']);
     $email=mysqli_real_escape_string($con,$_POST['email']);
     $un=mysqli_real_escape_string($con,$_POST['un']);
     $pswd=md5(mysqli_real_escape_string($con,$_POST['pswd']));
    
if($usertype=="Admin")
{
  

$res=mysqli_query($con,"SELECT * FROM elearn_admin where username='$un'") or die("<br>query unsuccessful for select");

if(mysqli_num_rows($res)>0)
{    echo "username already taken";
exit();
         }

else{
 mysqli_query($con,"INSERT INTO elearn_admin(name, email, username, password,verified,status) VALUES ('$name','$email','$un','$pswd',NULL,NULL)") or die("<br>query unsuccessful for insert");
 echo "Record Inserted";
}
   
}
else if($usertype=="Teacher")
{
$res=mysqli_query($con,"SELECT * FROM elearn_teach where username='$un'") or die("<br>query unsuccessful for select");

if(mysqli_num_rows($res)>0)
{    echo "username already taken";
exit();
         }

else{
 mysqli_query($con,"INSERT INTO elearn_teach(name, email, username, password,verified,status) VALUES ('$name','$email','$un','$pswd',NULL,NULL)") or die("<br>query unsuccessful for insert");
 echo "Record Inserted";
}

}
else if($usertype=="Student")
{


$res=mysqli_query($con,"SELECT * FROM elearn_stud where username='$un'") or die("<br>query unsuccessful for select");

if(mysqli_num_rows($res)>0)
{    echo "username already taken";
exit();
         }

else{
 mysqli_query($con,"INSERT INTO elearn_stud(name, email, username, password,verified,status) VALUES ('$name','$email','$un','$pswd',NULL,NULL)") or die("<br>query unsuccessful for insert");
 echo "Record Inserted";
}
   

}



mysqli_close($con);
 }