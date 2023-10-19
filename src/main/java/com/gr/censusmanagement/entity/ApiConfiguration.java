package com.gr.censusmanagement.entity;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ApiConfiguration extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	private String userName;
	private String password;
	private String partnerGuid;
	private Boolean isActive;
	private String apiKey;

}
