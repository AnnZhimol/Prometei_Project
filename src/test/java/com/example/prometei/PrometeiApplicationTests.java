package com.example.prometei;

import com.example.prometei.services.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrometeiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	TicketService ticketService;
}
