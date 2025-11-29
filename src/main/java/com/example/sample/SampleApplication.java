package com.example.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.sample.mapper")
public class SampleApplication {
	/**
	 * アプリケーションのエントリポイント。
	 *
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

}
