<?php
if(isset($_FILES["upload"])){
    //save to object
    $image = (object) @$_FILES['upload'];
    //create error message
    $errorMsg = [];

    if(!@$image->name){
        array_push($errorMsg, "Upload Your Image First");
    }
    if($image->size > 1000*10000){
        array_push($errorMsg, "Maximum Image Size 10 Mb");
    }

    //filter image type
    $ext = [
        'image/png',
        'image/jpg',
        'image/jpeg',
        'image/webp'
    ];
    if(!in_array(mime_content_type($image->tmp_name), $ext)){
        array_push($errorMsg, "Image Only!");
    }
    if(empty($errorMsg)){
        echo "Upload Success";
    } else {
        foreach($errorMsg as $msg){
            echo $msg;
            die;
        }
    }
    // echo "<pre>";
    // // var_dump($_FILES);
    // var_dump($image);
    // // var_dump($errorMsg);
} else {
    echo "No Uploaded File";
}