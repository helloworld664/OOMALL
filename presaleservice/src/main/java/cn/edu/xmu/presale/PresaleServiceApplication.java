package cn.edu.xmu.presale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XC 3304
 * Created at 2020-12-06 08:45
 * Modified at 2020-12-06 08:46
 */

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.presale"})
@MapperScan("cn.edu.xmu.presale.mapper")
public class PresaleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PresaleServiceApplication.class, args);
    }
}