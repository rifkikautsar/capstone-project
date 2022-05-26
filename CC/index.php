<?php
require_once('vendor/autoload.php');

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use Google\Cloud\Storage\StorageClient;

// Register the function with Functions Framework.
// This enables omitting the `FUNCTIONS_SIGNATURE_TYPE=http` environment
// variable when deploying. The `FUNCTION_TARGET` environment variable should
// match the first parameter.
FunctionsFramework::http('uploads', 'uploads');
function uploads(ServerRequestInterface $request)
{
    if($_SERVER["REQUEST_METHOD"]=="POST"){


        if(isset($_FILES['uploads'])){
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
                        $response['code'] = 400;
                        $response['data']['message'] = "Maximum Image Size 10 Mb";
                    }
                    if(!in_array(mime_content_type($image->tmp_name), $ext)){
                        $response['code'] = 400;
                        $response['data']['message'] = "Image Only!";
                    }
                    //if no error
                    if($response==[]){
                        $privateKeyFileContent = $GLOBALS['privateKeyFileContent'];
                        try {
                            $storage = new StorageClient([
                                'keyFile' => json_decode($privateKeyFileContent, true)
                            ]);
                            $bucketName = 'kulitku-capstone';
                            $fileContent = file_get_contents($_FILES["uploads"]["tmp_name"]);
                            $cloudPath = 'images/' . $_FILES["uploads"]["name"];
                            $bucket = $storage->bucket($bucketName);
                            $object = $bucket->upload($fileContent, [
                                'name' => $cloudPath
                            ]);
                            $response['code'] = 200;
                            $response['data']['message'] = "Image Success to Upload";
                        } catch(Exception $e) {
                            $response['code'] = 400;
                            $response['data']['message'] = $e->getMessage();
                        }
                    }
                } else {
                $response['code'] = 400;
                $response['data']['message'] = "Upload Your Image First!";
                }
        } else {
            $response['code'] = 400;
            $response['data']['message'] = "POST Request must be FILE!";
        }
    } else {
        $response['code'] = 400;
        $response['data']['message'] = "No POST REQUEST!";
    }
    header('Content-Type: application/json; charset=utf-8');
    return json_encode($response);
}