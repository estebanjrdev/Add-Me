<?php
$hostname='fdb31.125mb.com';
$database='4110240_revolico';
$username='4110240_revolico';
$password='Servidor9711*';

$conexion=new mysqli($hostname,$username,$password,$database);
if($conexion->connect_errno){
   // echo "El sitio web está experimentado problemas";
}else{
//echo "conexion exitosa";
}
?>