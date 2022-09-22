<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateUsersTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('users', function (Blueprint $table) {
            $table->id();
            $table->string('name')->nullable();
            $table->string('email')->unique()->nullable();
            $table->enum('jenisKelamin', ['L', 'P'])->nullable();
            $table->string('jenisKulit', 25)->nullable();
            $table->date('tanggalLahir')->nullable();
            $table->string('pass', 255)->nullable();
            $table->enum('aktif', ['1', '0'])->nullable();
            $table->enum('status', ['1', '0'])->nullable();
            $table->string('apiKey', 255)->nullable();
            $table->rememberToken();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('users');
    }
}