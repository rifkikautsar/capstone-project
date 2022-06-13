<?php
require_once('vendor/autoload.php');

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Google\Cloud\Storage\StorageClient;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

FunctionsFramework::http('login', 'login');

function login(ServerRequestInterface $request): ResponseInterface
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
        if (strpos($contentType, 'application/json') !== 0) {
            $response['code'] = 400;
            $response['data']['message'] = 'Bad Request: content of type "application/json" not provided, found ' . $contentType;
            return new Response(400, [], json_encode($response));
        }
        try{
            $username = 'root';
            $password = "NJHMT}Jc'P&JhirT";
            $dbName = 'capstone';
            $connectionName = "bustling-bot-350614:asia-southeast2:db-capstone";
            $dbHost = '34.128.115.93';
            
            $conn = new PDO("mysql:host=".$dbHost.";dbname=".$dbName, $username, $password);
            $conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
            $obj = json_decode($request->getBody()->getContents());
            // $hashPasswd = password_hash($obj->password, PASSWORD_DEFAULT);
            $stmt = $conn->prepare("SELECT * FROM user WHERE email = :email");
            $stmt->bindParam(":email", $obj->email);

            $stmt->execute();
            $data = $stmt->fetch(PDO::FETCH_ASSOC);
            if ($stmt->rowCount() > 0) {
                $array['id'] = $data['id'];
                $array['nama_user'] = $data['nama_user'];
                $array['username'] = $data['username'];
                $array['email'] = $data['email'];
                if (password_verify($obj->password,$data['password'])) {
                        $response['code'] = 200;
                        $response['data']['message'] = "Login Success";
                        $response['data']['user'] = $array;
                        return new Response(200, [], json_encode($response));
                    } 
                    else {
                        $response['code'] = 400;
                        $response['data']['message'] = "Wrong Password";
                        return new Response(400, [], json_encode($response));
                    }
            } else {
                $response['code'] = 400;
                $response['data']['message'] = "Check Password or Email!";
                return new Response(400, [], json_encode($response));
            }

        } catch (PDOException $e) {
            // tampilkan pesan kesalahan jika koneksi gagal
            return new Response(200, [], json_encode($e->getMessage()));
        }
    }
}