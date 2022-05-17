<?php
if(isset($_FILES["upload"])){
    //save to object
    $image = (object) @$_FILES['upload'];
    //create error message
    $errorMsg = [];

    if(!@$image->name){
        array_push($errorMsg, "Upload Your Image First");
    }
    echo "Upload Success";
    echo "<pre>";
    // var_dump($_FILES);
    var_dump($image);
    var_dump($errorMsg);
} else {
    echo "No Uploaded File";
}