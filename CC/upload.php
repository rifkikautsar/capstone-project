<?php
if($_SERVER["REQUEST_METHOD"]=="POST"){
    if(isset($_FILES['uploads'])){
        //save to object
        // var_dump($_FILES['uploads']);die;
        $image = (object) $_FILES['uploads'];
        if($image->size!=0){
        //create error message array
        $response = [];
        //filter image type
        $ext = [
            'image/png',
            'image/jpg',
            'image/jpeg',
            'image/webp'
        ];
        //validation
        if($image->size > 1000*10000){
            http_response_code(400);
            $response['code'] = 400;
            $response['data']['message'] = "Maximum Image Size 10 Mb";
        }
        if(!in_array(mime_content_type($image->tmp_name), $ext)){
            http_response_code(400);
            $response['code'] = 400;
            $response['data']['message'] = "Image Only!";
        }
        if($response==[]){
            define('BASEURL', $_SERVER['DOCUMENT_ROOT']."/data/project/CC/");
            $path = BASEURL."uploads/";
            $uploadedFile = $path . basename($_FILES['uploads']['name']);
            if(move_uploaded_file($_FILES['uploads']['tmp_name'], $uploadedFile)) {
                http_response_code(200);
                $response['code'] = 200;
                $response['data']['message'] = "Upload Success";
            } else{
                http_response_code(500);
                $response['code'] = 500;
                $response['data']['message'] = "Upload Fail";
            }

        }
            echo json_encode($response);
        } else {
            http_response_code(400);
            $response['code'] = 400;
            $response['data']['message'] = "Upload Your Image First!";
            echo json_encode($response);
        }
    }
}