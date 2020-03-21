import com.my.iot.domain.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class RedisTemplateTest {

    private RedisTemplate<String, Data> redisTemplate;
    private ClassPathXmlApplicationContext applicationContext;

    @Before
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
    }

    @Test
    public void testPush() {
        System.out.println(redisTemplate);
        ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        listOperations.leftPush("datas", new Data(4, -25.23f, new Date(), 4));
    }

    @Test
    public void testGetKeys() {
        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);
    }

    @Test
    public void testLrange() {
        ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        List<Data> datas = listOperations.range("datas", 0, -1);
        System.out.println(datas);
    }

    @Test
    public void testLeftPop() {
        ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        Data data = listOperations.leftPop("datas");
        System.out.println(data);
    }


    @After
    public void destory() {
        applicationContext.close();
    }
}
