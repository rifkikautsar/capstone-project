<?php
require_once('vendor/autoload.php');

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Google\Cloud\Storage\StorageClient;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

FunctionsFramework::http('uploads', 'uploadFile');

function uploadFile(ServerRequestInterface $request): ResponseInterface
{
    $headers = ['Access-Control-Allow-Origin' => '*'];

    if ($request->getMethod() === 'OPTIONS') {
        // Send response to OPTIONS requests
        $headers = array_merge($headers, [
            'Access-Control-Allow-Methods' => 'POST',
            'Access-Control-Allow-Headers' => 'Content-Type',
            'Access-Control-Max-Age' => '3600'
        ]);
        return new Response(204, $headers, '');
    } else {
        header('Content-Type: application/json; charset=utf-8');
        if ($request->getMethod() != 'POST') {
            $response['code'] = 405;
            $response['data']['message'] = 'Method Not Allowed: expected POST, found ' . $request->getMethod();
            return new Response(405, [], json_encode($response));
        }

        $contentType = $request->getHeader('Content-Type')[0];
        if (strpos($contentType, 'multipart/form-data') !== 0) {
            $response['code'] = 400;
            $response['data']['message'] = 'Bad Request: content of type "multipart/form-data" not provided, found ' . $contentType;
            return new Response(400, [], json_encode($response));
        }
        if($request->getUploadedFiles()['uploads']){
            if($request->getUploadedFiles()['uploads']->getSize()==0) {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: No Photo Uploaded. (Size : ' . $request->getUploadedFiles()['uploads']->getSize(). ")";
                return new Response(400, [], json_encode($response));
            }
            $type = explode("/", $request->getUploadedFiles()['uploads']->getClientMediaType())[1];
            $name = uniqid("img-", true) . "." . $type;
            $size = $request->getUploadedFiles()['uploads']->getSize();
            $tmpFile = $request->getUploadedFiles()['uploads']->getStream()->getMetadata('uri');
            //Array type of image allow
            $ext = [
                'image/png',
                'image/jpg',
                'image/jpeg',
                'image/webp'
            ];
            //Validation
            //Maximum Size 10Mb
            if($size > 1000*10000)
            {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: Maximum Size is 10 Mb!. (Size : ' . $request->getUploadedFiles()['uploads']->getSize(). ")";
                return new Response(400, [], json_encode($response));
            }
            if(!in_array(mime_content_type($tmpFile), $ext))
            {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: Only Accept Image Types!. (current types : ' . $type . ')';
                return new Response(400, [], json_encode($response));
            }
            try {
                $data = file_get_contents($tmpFile);
                $storage = new StorageClient([
                    'projectId' => 'bustling-bot-350614',
                    'keyFile' => json_decode(file_get_contents('bustling-bot-350614-c80cc103165a.json'), true)
                ]);
                // var_dump($str);die;
                // $storage = new StorageClient([
                //     'projectId' => 'bustling-bot-350614',
                //     'keyFile' => json_decode(file_get_contents('bustling-bot-350614-c80cc103165a.json'), true)
                // ]);
                $bucketName = 'kulitku-capstone';
                $cloudPath = 'images/' . $name;
                $bucket = $storage->bucket($bucketName);
                $object = $bucket->upload($data, [
                    'name' => $cloudPath
                ]);
                $object->update(['acl' => []], ['predefinedAcl' => 'PUBLICREAD']);
                $fields = [
                    'image' => $name,
                ];
                $payload = json_encode($fields);
                $ch = curl_init();
                // curl_setopt($ch,CURLOPT_URL, 'http://192.168.0.113:8080/');
                curl_setopt($ch,CURLOPT_URL, 'https://predicts-gnkxwtwa6q-et.a.run.app');
                curl_setopt($ch,CURLOPT_POST, true);
                curl_setopt($ch,CURLOPT_POSTFIELDS, $payload);
                curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
                curl_setopt($ch,CURLOPT_RETURNTRANSFER, true);
                $result = curl_exec($ch);
                curl_close($ch);
                $response['code'] = 200;
                $response['data']['message'] = 'Image '. $name . ' Success upload to Cloud Storage Bucket.';
                $response['data']['url'] = 'https://storage.googleapis.com/'. $bucketName .'/images'.'/'. $name;
                $response['data']['result'] = json_decode($result);
                return new Response(200, [], json_encode($response));
            } catch(Exception $e) {
                $response['code'] = 404;
                $response['data']['message'] = json_decode($e->getMessage());
                return new Response(404, [], json_encode($response));
            }
        } else {
            $response['code'] = 400;
            $response['data']['message'] = 'Bad Request: Key in body request not appropriate. Key name should be "uploads"';
            return new Response(400, [], json_encode($response));
        }  
    }
}