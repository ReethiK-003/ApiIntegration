package com.apiintegration.core.request;

import java.util.LinkedList;
import java.util.List;

import com.apiintegration.core.model.Project;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Getter
@Setter
@ToString
public class AddProjectToUserRequest {

	private List<Project> projectsList = new LinkedList<>();

	private Long addToUserId;
}