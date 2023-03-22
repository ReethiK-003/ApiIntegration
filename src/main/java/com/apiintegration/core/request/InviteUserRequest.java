package com.apiintegration.core.request;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@Service
@ToString
public class InviteUserRequest {

	private String email;
	private String role;
	private Long accountId;
	private List<Long> projectsId = new LinkedList<>();
}