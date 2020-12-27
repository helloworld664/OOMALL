package cn.edu.xmu.presale.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.presale.PresaleServiceApplication;
import cn.edu.xmu.presale.model.po.PresalePo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author XC
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PresaleServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
class PresaleDaoTest {
    @Autowired
    private PresaleDao presaleDao;

    @Test
    void getAllState() {
        ReturnObject returnObject = presaleDao.getAllState();
        return;
    }

    @Test
    void getExamplePresale() {
    }

    @Test
    void getAllValidPresale() {
        //List<PresalePo> presalePoList = presaleDao.getAllValidPresale(1L, 2, 273L, 1, 10);
        return;
    }

    @Test
    void getAllPresaleBySKUId() {
    }

    @Test
    void createPresale() {
    }

    @Test
    void updateState() {
        ReturnObject returnObject1 = presaleDao.updateState(1L, 1L, (byte) 2);
        ReturnObject returnObject2 = presaleDao.updateState(2L, 2L, (byte) 2);
        ReturnObject returnObject3 = presaleDao.updateState(1L, 3L, (byte) 1);
        ReturnObject returnObject4 = presaleDao.updateState(2L, 4L, (byte) 0);
        ReturnObject returnObject5 = presaleDao.updateState(1L, 6L, (byte) 1);
    }

    @Test
    void afterPropertiesSet() {
    }
}