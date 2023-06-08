<?php
include 'conexion.php';
$json=array();
$result=array();
 $sql = "select * from add_me ORDER BY `id_contacto` DESC";
        $resultado = mysqli_query($conexion, $sql);
  
        if (mysqli_num_rows($resultado) != 0) {
        //$json['status']="success";
            while($row = mysqli_fetch_assoc($resultado)){
            
   			//$result["image"] = base64_encode($row['image']);
                        $result["name"] = $row['name'];
                        $result["country"] = $row['country'];
                        $result["phone"] = $row['phone'];
			$result["instagram"] = $row['instagram'];	
                        $result["facebook"] = $row['facebook'];	
                $json[]=$result;
                }
           
           
           mysqli_close($conexion);
           header('Content-Type: application/json; charset=utf-8');
           print json_encode($json);
             
        }
?>