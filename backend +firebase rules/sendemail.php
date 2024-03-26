<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
require $_SERVER['DOCUMENT_ROOT'] . '/mail/Exception.php';
require $_SERVER['DOCUMENT_ROOT'] . '/mail/PHPMailer.php';
require $_SERVER['DOCUMENT_ROOT'] . '/mail/SMTP.php';


if(isset($_POST['email'])&&$_POST['email'])
{
$verificationcode=mt_rand(100000,999999);
$to=$_POST['email'];
$mail = new PHPMailer();
$mail->isSMTP(); 
//$mail->SMTPDebug = 3; // 0 = off (for production use) - 1 = client messages - 2 = client and server messages
$mail->Host = "smtp.gmail.com"; // use $mail->Host = gethostbyname('smtp.gmail.com'); // if your network does not support SMTP over IPv6
$mail->Port = 587; // TLS only
$mail->SMTPSecure = 'tls'; // ssl is deprecated
$mail->SMTPAuth = true;
$mail->Username = ' '; // email
$mail->Password = ' '; // password
$mail->setFrom(' ', ' '); // From email and name
$mail->addAddress($to); // to email and name
$mail->Subject = 'Verification code for Elearning app';
$mail->msgHTML("verification code:'$verificationcode'"); //$mail->msgHTML(file_get_contents('contents.html'), __DIR__); //Read an HTML message body from an external file, convert referenced images to embedded,
$mail->AltBody = 'HTML messaging not supported'; // If html emails is not supported by the receiver, show this body
// $mail->addAttachment('images/phpmailer_mini.png'); //Attach an image file
$mail->SMTPOptions = array(
                    'ssl' => array(
                        'verify_peer' => false,
                        'verify_peer_name' => false,
                        'allow_self_signed' => true
                    )
                );
if($mail->send()){
    echo $verificationcode;
}else{
    echo 0;

}
}
 
?>
