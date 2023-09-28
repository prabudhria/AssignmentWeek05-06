package com.ria.acmetesting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.TestcontainersExtension;

@SpringBootTest
@ExtendWith({TestcontainersExtension.class})
class AcmeAdaptiveTestingSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
