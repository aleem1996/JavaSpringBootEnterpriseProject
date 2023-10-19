package com.gr.censusmanagement.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Batch implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 36)
	@GeneratedValue(generator = "uuid", strategy = GenerationType.AUTO)
	@GenericGenerator(name = "uuid", strategy = "com.gr.censusmanagement.entity.generator.IdGenerator")
	private String id;

	private String entityName;
	private Integer totalRecords;
	private String status;
	private String filename;

}
