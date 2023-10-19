package com.gr.censusmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SourceSystem extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	@Column(length = 45, unique = true)
	private String name;

	@Column(length = 100, unique = true)
	private String apiKey;

	public String getNick() {
		return this.name.equalsIgnoreCase("grid") ? "grid" : "ss";
	}
}
