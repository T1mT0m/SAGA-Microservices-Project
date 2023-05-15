package com.github.bwar.sb.ms.saga.userservice.projection;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.github.bwar.sb.ms.saga.commonservice.model.CardDetails;
import com.github.bwar.sb.ms.saga.commonservice.model.User;
import com.github.bwar.sb.ms.saga.commonservice.queries.GetUserPaymentDetailsQuery;

@Component
public class userProjection {
	
	@QueryHandler
	public User getUserPaymentQueryDetails(GetUserPaymentDetailsQuery query) {
		// get the data from DB
		CardDetails cardDetails = CardDetails.builder()
				.name("Biju Warrier")
				.validUntilMonth(03)
				.validUntilYear(2024)
				.cardnumber("123456789")
				.cvv(321)
				.build();
		
		User user = User.builder()
				.userId(query.getUserId())
				.firstName("Biju")
				.lastname("Warrier")
				.cardDetails(cardDetails)
				.build();
		
		return user;
		
	}

}
