<?php
include '../conexion.php';
$json=array();
$result=array();
$resultado = false;
$password = password_hash($_POST['password'], PASSWORD_DEFAULT);

        $sql1 = "select * from add_me where phone = '" . $_POST['phone'] . "'";
        $resultado1 = mysqli_query($conexion, $sql1);
        if (mysqli_num_rows($resultado1) != 0) {
        
             $result["success"]=false;
             $result["message"]="Ya existe este contacto";
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        
        } else {
        $sql = "INSERT INTO add_me (name, country, phone, instagram, facebook, password) VALUES ('" . $_POST['name'] . "','" . $_POST['country'] . "','" . $_POST['phone'] . "','" . $_POST['instagram'] . "','" . $_POST['facebook']. "','" . $password  . "')";
        $resultado = mysqli_query($conexion, $sql);
        
        if ($resultado) {
             $result["success"]=true;
             $result["message"]="Contacto Agregado";
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        } else {
            $result["success"]=false;
            $result["message"]="Contacto no Agregado";
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        }
        }
         mysqli_close($conexion);
?>