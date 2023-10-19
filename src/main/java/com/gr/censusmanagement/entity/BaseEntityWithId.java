package com.gr.censusmanagement.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@MappedSuperclass
@ToString
public abstract class BaseEntityWithId extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 36)
	@GeneratedValue(generator = "uuid", strategy = GenerationType.AUTO)
	@GenericGenerator(name = "uuid", strategy = "com.gr.censusmanagement.entity.generator.IdGenerator")
	private String id;
}
