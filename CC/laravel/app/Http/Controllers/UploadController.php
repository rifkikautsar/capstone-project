<?php

namespace App\Http\Controllers;

use Google\Cloud\Storage\StorageClient;
use PDO;
use Illuminate\Http\Request;
use Illuminate\Support\Facades;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

class UploadController extends Controller
{
    public function index(ServerRequestInterface $request): ResponseInterface
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
            $headers = ['Content-Type' =>  'application/json'];            
            if ($request->getMethod() != 'POST') {
                $response['code'] = 405;
                $response['data']['message'] = 'Method Not Allowed: expected POST, found ' . $request->getMethod();
                return new Response(405, $headers, json_encode($response));
            }
    
            $contentType = $request->getHeader('Content-Type')[0];
            if (strpos($contentType, 'multipart/form-data') !== 0) {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: Invalid Content-Type';
                return new Response(400, $headers, json_encode($response));
            }
            if($request->getUploadedFiles()['uploads']){
                if($request->getUploadedFiles()['uploads']->getSize()==0) {
                    $response['code'] = 400;
                    $response['data']['message'] = 'Bad Request: No Photo Uploaded. (Size : ' . $request->getUploadedFiles()['uploads']->getSize(). ")";
                    return new Response(400, $headers, json_encode($response));
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
                    return new Response(400, $headers, json_encode($response));
                }
                if(!in_array(mime_content_type($tmpFile), $ext))
                {
                    $response['code'] = 400;
                    $response['data']['message'] = 'Bad Request: Only Accept Image Types!. (current types : ' . $type . ')';
                    return new Response(400, $headers, json_encode($response));
                }
                try {
                    if ($request->getParsedBody()['class'] == null){
                        $response['code'] = 400;
                        $response['data']['message'] = 'Bad Request: Tidak ada kelas';
                        return new Response(400, $headers, json_encode($response));
                        die;
                    }
                    if ($request->getParsedBody()['apiKey'] == null){
                        $response['code'] = 400;
                        $response['data']['message'] = 'Bad Request: Tidak ada apiKey';
                        return new Response(400, $headers, json_encode($response));
                        die;
                    }
                    if ($request->getParsedBody()['userId'] == null){
                        $response['code'] = 400;
                        $response['data']['message'] = 'Bad Request: Tidak ada userId';
                        return new Response(400, $headers, json_encode($response));
                        die;
                    }
                    $username = getenv('DB_USERNAME');
                    $password = getenv('DB_PASSWORD');
                    $dbName = getenv('DB_DATABASE');
                    $dbHost = getenv('DB_HOST');
                    $conn = new PDO("mysql:host=".$dbHost.";dbname=".$dbName, $username, $password);
                    $conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
                } catch(PDOException $e) {
                    return new Response(200, $headers, json_encode("Gagal Koneksi ke Database ", $e->getMessage()));
                    die();
                }
                $userId = $request->getParsedBody()['userId'];
                $apiKey = $request->getParsedBody()['apiKey'];
                $class = $request->getParsedBody()['class'];
                $stmt = $conn->prepare("SELECT apiKey FROM user where userId = :userId");
                $stmt->bindParam(":userId",$userId);
                $stmt->execute();
                $data = $stmt->fetch(PDO::FETCH_ASSOC);
                if($stmt->rowCount() > 0){
                    if ($apiKey === $data['apiKey']){
                        $data = file_get_contents($tmpFile);
                        $storage = new StorageClient([
                            'projectId' => 'bustling-bot-350614',
                            'keyFile' => json_decode(file_get_contents('bustling-bot-350614-5dab7679f2d4.json'), true)
                        ]);
                        $bucketName = 'kulitku-incubation';
                        $cloudPath = $class. '/' . $name;
                        $bucket = $storage->bucket($bucketName);
                        $object = $bucket->upload($data, [
                            'name' => $cloudPath
                        ]);
                        $fields = [
                            'image' => $name,
                        ];
                        $response['code'] = 200;
                        $response['data']['message'] = 'Foto berhasil diupload';
                        return new Response(200, $headers, json_encode($response));
                    } else {
                        $response['code'] = 403;
                        $response['data']['message'] = 'Akses anda dilarang. API Key tidak sesuai';
                        return new Response(403, $headers, json_encode($response));
                    }
                } else {
                    $response['code'] = 400;
                    $response['data']['message'] = 'Akun tidak terdaftar di database';
                    return new Response(400, $headers, json_encode($response));
                }
                $stmt = null;
            } else {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: Invalid Key Field';
                return new Response(400, $headers, json_encode($response));
            }  
        }
    }
}