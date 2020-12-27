package cn.edu.xmu.presale.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.presale.PresaleServiceApplication;
import cn.edu.xmu.presale.model.po.PresalePo;
import cn.edu.xmu.presale.model.vo.CreatePresaleVo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author XC
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PresaleServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
class PresaleServiceTest {
    @Autowired
    private PresaleService presaleService;

    @Test
    void getAllStates() {
        List<PresalePo> presalePoList = presaleService.getAllStates();
        return;
    }

    @Test
    void transfer() {
    }

    @Test
    void getAllValidPresale() {
    }

    @Test
    void getAllPresaleBySKUId() {
    }

    @Test
    void createPresale() {
        CreatePresaleVo createPresaleVo = new CreatePresaleVo();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        createPresaleVo.setAdvancePayPrice(100L);
        createPresaleVo.setBeginTime(LocalDateTime.parse("2200-12-20 15:00:00", dateTimeFormatter));
        createPresaleVo.setEndTime(LocalDateTime.parse("2200-12-31 15:00:00", dateTimeFormatter));
        createPresaleVo.setPayTime(LocalDateTime.parse("2020-12-30 15:00:00", dateTimeFormatter));
        createPresaleVo.setName("撒大噶");
        createPresaleVo.setQuantity(10);
        createPresaleVo.setRestPayPrice(900L);
        ReturnObject creation = presaleService.createPresale(1L, 273L, createPresaleVo);
        return;
    }

    @Test
    void updatePresale() {
    }

    @Test
    void deletePresale() {
    }

    @Test
    void presaleOnShelves() {
    }

    @Test
    void presaleOffShelves() {
    }
}