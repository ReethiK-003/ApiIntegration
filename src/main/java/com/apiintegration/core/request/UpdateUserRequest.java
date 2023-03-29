package com.apiintegration.core.request;

import java.util.List;

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
public class UpdateUserRequest {

	@NotNull
	private Long userId;
	private String role;
	private List<Long> projectsId;

}