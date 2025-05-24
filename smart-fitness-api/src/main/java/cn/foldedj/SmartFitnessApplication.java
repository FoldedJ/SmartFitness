package cn.foldedj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@MapperScan("cn.foldedj.mapper")
@SpringBootApplication
public class SmartFitnessApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartFitnessApplication.class, args);
    }
}
