package cn.edu.xmu.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XC 3304
 * Created at 2020-12-26 23:29
 * Modified at 2020-12-26 23:29
 */

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.goods"})
@MapperScan("cn.edu.xmu.goods.mapper")
public class GoodsServiceTempApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceTempApplication.class, args);
    }
}
