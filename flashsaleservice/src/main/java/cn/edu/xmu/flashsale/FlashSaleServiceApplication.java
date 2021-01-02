package cn.edu.xmu.flashsale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author XC 3304
 * Created at 2020-12-07 10:37
 * Modified at 2020-12-07 10:37
 */

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.flashsale"})
@MapperScan("cn.edu.xmu.flashsale.mapper")
public class FlashSaleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashSaleServiceApplication.class, args);
    }
}
