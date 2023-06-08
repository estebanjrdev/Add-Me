<?php
include '../conexion.php';
$json=array();
$result=array();
$search = $_GET["search"];
 $sql = "select * from add_me WHERE `phone` = '".$search."' OR `name` LIKE '".$search."' ";
        $resultado = mysqli_query($conexion, $sql);
  
        if (mysqli_num_rows($resultado) != 0) {
        //$json['status']="success";
            while($row = mysqli_fetch_assoc($resultado)){
            
   			$result["name"]=$row['name'];
                        $result["country"]=$row['country'];
                        $result["phone"]=$row['phone'];
			$result["instagram"]=$row['instagram'];	
                        $result["facebook"]=$row['facebook'];	
                $json[]=$result;
                }
           
           
          
           header('Content-Type: application/json; charset=utf-8');
           print json_encode($json);
             
        } else {
        
           //$json[]=$result;
           header('Content-Type: application/json; charset=utf-8');
           print json_encode($json);
           
        }
         mysqli_close($conexion);
?>