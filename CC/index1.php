<?php
require_once('vendor/autoload.php');

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use Google\Cloud\Storage\StorageClient;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

FunctionsFramework::http('uploads', 'uploads');

function uploads(ServerRequestInterface $request): ResponseInterface
{
    header('Content-Type: application/json; charset=utf-8');
    //Create Array for response
    $response = [];
    if($request->getMethod()=='POST')
    {
        if(isset($request->getUploadedFiles()['uploads']))
                {
                    if($request->getUploadedFiles()['uploads']->getSize()!=0)
                    {
                        $type = $request->getUploadedFiles()['uploads']->getClientMediaType();
                        $name = $request->getUploadedFiles()['uploads']->getClientFilename();
                        $size = $request->getUploadedFiles()['uploads']->getSize();
                        $tmpFile = $request->getUploadedFiles()['uploads']->getStream()->getMetadata('uri');
                        $data = file_get_contents($tmpFile);
                        //Array type of image allow
                        $ext = [
                            'image/png',
                            'image/jpg',
                            'image/jpeg',
                            'image/webp'
                        ];
                        //Validation
                        //Size Validation. Max 10Mb
                        if($size > 1000*10000)
                        {
                            $response['code'] = 400;
                            $response['data']['message'] = "Maximum Image Size 10 Mb";
                            return json_encode($response);
                        }
                        //Mime Type Validation
                        if(!in_array(mime_content_type($tmpFile), $ext))
                        {
                            $response['code'] = 400;
                            $response['data']['message'] = "Image Only!";
                            return json_encode($response);
                        }
                        //If Validation passed
                        if(empty($response)){
                            try {
                                $storage = new StorageClient([
                                    'keyFilePath' => 'bustling-bot-350614-c80cc103165a.json'
                                ]);
                                $bucketName = 'kulitku-capstone';
                                $cloudPath = 'images/' . $name;
                                $bucket = $storage->bucket($bucketName);
                                $object = $bucket->upload($data, [
                                    'name' => $cloudPath
                                ]);
                                $response['code'] = 200;
                                $response['data']['message'] = "Image Success to Upload";
                                return json_encode($response);
                            } catch(Exception $e) {
                                $response['code'] = 400;
                                $response['data']['message'] = $e->getMessage();
                                return json_encode($response);
                            }
                        }
                    } else {
                    $response['code'] = 400;
                    $response['data']['message'] = "Upload Your Image!";
                    return json_encode($response);
                    }
            } else {
                $response['code'] = 400;
                $response['data']['message'] = "POST Request must be FILE!";
                return json_encode($response);
            }
    } else {
        return new Response(405, [], 'Method Not Allowed: expected POST, found ' . $request->getMethod());
        // http_response_code(400);
        // $response['code'] = 400;
        // $response['data']['message'] = "REQUEST method must be POST!";
        // return json_encode($response);
    }
}