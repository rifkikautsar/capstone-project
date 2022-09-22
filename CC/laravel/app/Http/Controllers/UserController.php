<?php

namespace App\Http\Controllers;
use PDO;
use Illuminate\Http\Request;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Message\UriInterface;
use Psr\Http\Message\UploadedFileInterface;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Psr7\Response;

class UserController extends Controller
{
    //
    public function activation(ServerRequestInterface $request): ResponseInterface
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
            var_dump($request);
        }
    }
}