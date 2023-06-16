<?php
include '../conexion.php';
$json=array();
$result=array();
$resultado = false;
$password = password_hash($_POST['password'], PASSWORD_DEFAULT);
       $sql = "UPDATE add_me SET name = '" . $_POST['name'] . "', phone = '" . $_POST['phone'] . "', instagram = '" . $_POST['instagram'] . "', facebook = '" . $_POST['facebook']. "', password = '" . $password  . "' WHERE id_contacto = '" . $_POST['id_contacto']. "'";
       $resultado = mysqli_query($conexion, $sql);
        
        if ($resultado) {
             $result["success"]=true;
             $result["message"]="Contacto Actualizado";
             $result["id_contacto"] = $_POST['id_contacto'];
              $result["name"] = $_POST['name'];
              $result["country"] = $_POST['country'];
              $result["phone"] = $_POST['phone'];
	      $result["instagram"] = $_POST['instagram'];	
              $result["facebook"] = $_POST['facebook'];
              $result["password"] = $_POST['password'];
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        } else {
            $result["success"]=false;
            $result["message"]="Contacto no Actualizado";
            $result["name"] = $_POST['name'];
              $result["country"] = $_POST['country'];
              $result["phone"] = $_POST['phone'];
	      $result["instagram"] = $_POST['instagram'];	
              $result["facebook"] = $_POST['facebook'];
              $result["password"] = $_POST['password'];
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        }
         mysqli_close($conexion);
?>