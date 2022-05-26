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

$privateKeyFileContent = '
{
    "type": "service_account",
    "project_id": "bustling-bot-350614",
    "private_key_id": "ac1cfadb096aa5b43a8e5c4e5af0193c98c7c1f7",
    "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDgP+EXMG2MEMMI\nPckHz7cNW71c4fFRNk9qB1yxkTb2dOiPL+l1WKPeNIsHJQiwmIyLKyOa83K1ff0h\n0v3KcO9SKFtmauSv22QIePxmpncblnk8N+/+7JJ8IifGGu5oph7MbbtI8H5UohCD\nE+2uMtuod8/uRv4PtgOGnM2LxvjEkUK76o4kF6BNlP4cj11120nSBQx/q62JhGnP\nmJS3p5qiaKzmw+z74mq5myVaLO/JH8EUhifIac6WmaDa+dBo8bbCfe5bYIFpnMSP\nXyYJnOUL/r46xBtlYfSSqnBYjUcluGDhJcQe6Xy47SgLXUN4zHasUYo+GO9rIWzl\nP1CZL4dNAgMBAAECggEAaQNBAzz4b9kBnABi/bd1v1mwqahc+2z1b2+XJsgb9vn0\nVelX6E3kHxB38paTJS+q0RkYiV1r4DXprkdB9eVBDTY85w8znG6ev6D4gZF+wXqL\nt3lyd+yNBHhyK8E81+DDF3Rjedq1LijelqNkcGj8mD3TL4ArRWw05nrTfptkr5d+\nQw7TVuWfwGHwMoseObAPC3iQZzfZSc/dGsXYHB0zK48ARuUZ1C+EBNzDmAiAjiVY\nybcPl9I9sLMlUA8Qo4ggLj/m9er6Hl3VoT3tWijLpicXZbQavbBvDjlJ6uCAV6+q\nsShZXa8MvspLSyhslZ1wAHbwWWpWUOzEdj7xU3AXOQKBgQDvv00I+uWT7Ldy2VPW\nRXQ7s2CTLv+LWz+MV+CDXWSlbta75oBajdjXIEoe8Xhsr+zJQ8pTea5cKmMUOmXw\nsBNNK/i/Kay4B3hEu9Wnad3A6ZoGyH8Qf1fIeQSMKjylh6B6FXUMo9+364zcKRan\n7gdkfPkI8Zu2YuKDaOTmn1AkzwKBgQDvc59aGsG3cgSgmJrdYhlF6VO2dcGqwBGG\nJMv62P22e99ilFjaeQl1RCB1Buzt0FyzdNhhMBIAkgJ7USWcRJlQZMEZdS91Betp\nB0ZOsXIvK6m4l0KNmDh6Wr32spc07S/r/m5hg1fCDQeXqZbnofnCTskGW+6NZ3JE\nbst+oFtRIwKBgQCkVHLAGgFsoE+p6C8f6qFeHniuygMdCSGI198rC8Pl5RBI4RjP\nePTNl/NWPFs33GooFlBDYV9TCza4ccLX9Eh2mBI7LZyW03/rVPiAdP8rsLMDvT/j\nFfYckl68mGLTbyBfLf/59wVscoctjPhbiLsGdObZ64m4vRTXz/k93qs3XwKBgQDZ\nOe0wUuG2SAuGnaqwd9QxpjC5cC6XdtLu/ObH1J6mM849ohLRb8hyx7EiOXwrakEO\n1EuQL4nFVrBFqyIRl/H2CtFbBQJeiVY828qejT/nJxc7TI2l4pgp97Qp325pJ1u4\nHgOJFxxAAI4IumMaE8l+yvZ3fGL3iQcSqeiWIWd2PQKBgQCGZoXdsfm9ec1nEWQG\ni9u869RAf0x5C7X3x7xOndwymudpbFS4zdoB6OqPw+K0/vE2WfDzzz+mkMYsmbwg\ngoaJYtBygKenPBlDqx7iPrL0H+B/zzgDEHCViArYZA3eUtn34bcRhInK57j6EmDQ\n20PWke9frYa8QK/YSuZdOZU0wA==\n-----END PRIVATE KEY-----\n",
    "client_email": "gcs-upload@bustling-bot-350614.iam.gserviceaccount.com",
    "client_id": "115371056263346899793",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/gcs-upload%40bustling-bot-350614.iam.gserviceaccount.com"
  }';

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