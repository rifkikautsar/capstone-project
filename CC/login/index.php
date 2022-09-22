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
            $password = "K,=QZF]H`e[3jz~X";
            $dbName = 'incubation';
            $dbHost = '35.226.57.173';
            
            // $username = getenv('MYSQL_USER');
            // $password = getenv('MYSQL_PASSWORD');
            // $dbName = getenv('MYSQL_DATABASE');
            // $dbHost = getenv('MYSQL_HOST');
            $conn = new PDO("mysql:host=".$dbHost.";dbname=".$dbName, $username, $password);
            $conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
        } catch (PDOException $e) {
            // tampilkan pesan kesalahan jika koneksi gagal
            return new Response(200, [], json_encode($e->getMessage()));
            exit();
        }
        $obj = json_decode($request->getBody()->getContents());
        $stmt = $conn->prepare("SELECT * FROM user WHERE email = :email");
        $stmt->bindParam(":email", $obj->email);
        $stmt->execute();
        $data = $stmt->fetch(PDO::FETCH_ASSOC);
        if ($stmt->rowCount() > 0) {
            if (password_verify($obj->pass,$data['pass'])) {
                $array['userId'] = $data['userId'];
                $array['nama'] = $data['nama'];
                $array['email'] = $data['email'];
                $array['jenisKelamin'] = $data['jenisKelamin'];
                $array['jenisKulit'] = $data['jenisKulit'];
                $array['tanggalLahir'] = $data['tanggalLahir'];
                $array['apiKey'] = $data['apiKey'];
                
                $response['code'] = 200;
                $response['data']['message'] = "Login Success";
                $response['data']['user'] = $array;
                return new Response(200, [], json_encode($response));
                } 
                else {
                    $response['code'] = 400;
                    $response['data']['message'] = "Password Salah. Silakan ulangi";
                    return new Response(400, [], json_encode($response));
                }
        } else {
            $response['code'] = 400;
            $response['data']['message'] = "Email atau Password Salah. Silakan ulangi";
            return new Response(400, [], json_encode($response));
        }
    }
}