package cn.edu.xmu.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.goods"})
@MapperScan("cn.edu.xmu.goods.mapper")
public class GoodServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodServiceApplication.class, args);
    }

}
