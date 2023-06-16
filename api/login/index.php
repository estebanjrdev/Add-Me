<?php
include '../conexion.php';
$json=array();
$result=array();
$resultado = false;

        $sql = "select * from add_me where phone = '" . $_POST['phone'] . "'";
        $resultado = mysqli_query($conexion, $sql);
        $row = mysqli_fetch_assoc($resultado);
        
        if (mysqli_num_rows($resultado) != 0) {
        
             $dbphone = $row['phone'];
             $dbpassword = $row['password'];
             if ($dbphone == $_POST['phone'] && password_verify($_POST['password'], $dbpassword)) {
         
              $result["success"]=true;
              $result["message"]="Sesión Iniciada";
              $result["id_contacto"] = $row['id_contacto'];
              $result["name"] = $row['name'];
              $result["country"] = $row['country'];
              $result["phone"] = $row['phone'];
	      $result["instagram"] = $row['instagram'];	
              $result["facebook"] = $row['facebook'];
              $result["password"] = $_POST['password'];
              $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
             
             } else {
             
             $result["success"]=false;
             $result["message"]="Contraseña Incorrecta";
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
             
             }
             
        } else {
        
             $result["success"]=false;
             $result["message"]="No existe el contacto";
             $json[]=$result;
             header('Content-Type: application/json; charset=utf-8');
             print json_encode($json);
        }
        mysqli_close($conexion);
?>