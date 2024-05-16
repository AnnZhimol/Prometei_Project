package com.example.prometei;

import com.example.prometei.models.Ticket;
import com.example.prometei.models.TicketType;
import com.example.prometei.models.User;
import com.example.prometei.services.TicketService;
import com.example.prometei.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrometeiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UserService userService;

	@Autowired
	TicketService ticketService;

	@Test
	void userAdd(){
		User user = User.builder()
				.email("defsdgfsdthgf")
				.password("fdsvcdderggdfgc")
				.build();
		userService.add(user);
	}

	@Test
	void ticketAdd(){
		Ticket ticket = Ticket.builder()
				.ticketType(TicketType.BUSINESS)
				.build();
		ticketService.add(ticket);
	}
}
