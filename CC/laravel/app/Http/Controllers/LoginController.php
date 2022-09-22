<?php

namespace App\Http\Controllers;

use PDO;
use Illuminate\Http\Request;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

class LoginController extends Controller
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
            if (strpos($contentType, 'application/json') !== 0) {
                $response['code'] = 400;
                $response['data']['message'] = 'Bad Request: Invalid Content-Type';
                return new Response(400, $headers, json_encode($response));
            }
            try{
                $username = getenv('MYSQL_USER');
                $password = getenv('MYSQL_PASSWORD');
                $dbName = getenv('MYSQL_DATABASE');
                $dbHost = getenv('MYSQL_HOST');
                $conn = new PDO("mysql:host=".$dbHost.";dbname=".$dbName, $username, $password);
                $conn->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
            } catch (PDOException $e) {
                // tampilkan pesan kesalahan jika koneksi gagal
                return new Response(200, $headers, json_encode("Gagal Koneksi ke Database ", $e->getMessage()));
                die();
            }
            $obj = json_decode($request->getBody()->getContents());
            $stmt = $conn->prepare("SELECT * FROM user WHERE email = :email");
            $stmt->bindParam(":email", $obj->email);
            $stmt->execute();
            $data = $stmt->fetch(PDO::FETCH_ASSOC);
            if ($stmt->rowCount() > 0) {
                if ($data['aktif'] == 0) {
                    $response['code'] = 200;
                    $response['data']['message'] = "Konfirmasi Email terlebih dahulu";
                    return new Response(200, $headers, json_encode($response));
                    exit();
                } else {
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
                        return new Response(200, $headers, json_encode($response));
                    } else {
                        $response['code'] = 400;
                        $response['data']['message'] = "Password Salah. Silakan ulangi";
                        return new Response(400, $headers, json_encode($response));
                    }
                }
            } else {
                $response['code'] = 400;
                $response['data']['message'] = "Email atau Password Salah. Silakan ulangi";
                return new Response(400, $headers, json_encode($response));
            }
        }
    }
}