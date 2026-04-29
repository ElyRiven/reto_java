package com.banco.bank;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires external DB connectivity not needed for isolated unit/integration adapter tests")
class BankApplicationTests {

	@Test
	void contextLoads() {
	}

}
