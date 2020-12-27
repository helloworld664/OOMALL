package cn.edu.xmu.groupon.dao;

import cn.edu.xmu.groupon.GrouponServiceApplication;
import cn.edu.xmu.groupon.model.bo.Groupon;
import cn.edu.xmu.groupon.model.po.GrouponPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrouponServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class GrouponDaoTest {
    @Autowired
    GrouponDao grouponDao;

    @Test
    public void selectTest() {
        GrouponPo grouponPo = grouponDao.selectGrouponById(153L);
        return;
    }
}