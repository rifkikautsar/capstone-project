<?php
require_once('vendor/autoload.php');

use Google\CloudFunctions\FunctionsFramework;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Google\Cloud\Storage\StorageClient;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

FunctionsFramework::http('register', 'register');

function register(ServerRequestInterface $request): ResponseInterface
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
            //will be write in env variable
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
            return new Response(200, [], json_encode("Gagal Koneksi ke Database ", $e->getMessage()));
            die();
        }
        //local time
        $dt = new DateTime("now", new DateTimeZone('Asia/Jakarta'));
        $timestamps = $dt->format('Y-m-d H:i:s');
        $obj = json_decode($request->getBody()->getContents());
        $stmt = $conn->prepare("SELECT email FROM user WHERE email = :email");
        $stmt->bindParam(":email", $obj->email);
        $stmt->execute();
        if ($stmt->rowCount() > 0) {
            $response['code'] = 400;
            $response['data']['message'] = "Email telah terdaftar. Silakan Login";
            return new Response(400, [], json_encode($response));
            $stmt->close();
        } else {
            $hashPasswd = password_hash($obj->pass, PASSWORD_DEFAULT);
            $token=hash('sha256', md5(date('Y-m-d')));  
            $apiKey = implode('', str_split(substr(hash('sha256',microtime().rand(1000, 9999)), 0, 45), 6));
            $stmt = $conn->prepare("INSERT INTO user(nama,email,jenisKelamin,jenisKulit,tanggalLahir,pass,apiKey,token,aktif,created_at,updated_at) values(:nama,:email,:jenisKelamin,:jenisKulit,:tanggalLahir,:pass,:apiKey,:created_at,:updated_at)");
            $stmt->bindParam(":nama", $obj->nama);
            $stmt->bindParam(":email", $obj->email);
            $stmt->bindParam(":jenisKelamin", $obj->jenisKelamin);
            $stmt->bindParam(":jenisKulit", $obj->jenisKulit);
            $stmt->bindParam(":tanggalLahir", $obj->tanggalLahir);
            $stmt->bindParam(":pass", $hashPasswd);
            $stmt->bindParam(":apiKey", $apiKey);
            $stmt->bindParam(":token", $token);
            $stmt->bindParam(":aktif", 0);
            $stmt->bindParam("created_at", $timestamps);
            $stmt->bindParam("updated_at", $timestamps);
            $stmt->execute();
            $stmt->close();
            $response['code'] = 200;
            $response['data']['message'] = "Registrasi Berhasil. Silakan Cek Email untuk verifikasi";
            return new Response(200, [], json_encode($response));
            
        }
    }
}