package br.com.bank;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.moneymovements.MMovementsApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MMovementsApplication.class})
public class MMovementsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
