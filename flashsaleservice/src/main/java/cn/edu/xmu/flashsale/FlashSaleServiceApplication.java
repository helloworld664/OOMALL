package cn.edu.xmu.flashsale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XC
 */

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.flashsale"})
@MapperScan("cn.edu.xmu.flashsale.mapper")
public class FlashSaleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashSaleServiceApplication.class, args);
    }
}
