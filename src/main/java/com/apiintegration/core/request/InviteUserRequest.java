package com.apiintegration.core.request;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

	@NotBlank(message = "Email should not blank")
	private String email;
	@NotBlank(message = "Role should not blank")
	private String role;
	@NotNull(message = "Account_id should not blank")
	private Long accountId;

	private List<Long> projectsId = new LinkedList<>();

}