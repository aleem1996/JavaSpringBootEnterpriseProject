package com.gr.censusmanagement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@JsonIgnoreProperties(value = { "createdOn", "modifiedOn" }, allowGetters = true)
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(columnDefinition = "DATETIME(3)", nullable = false, updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@UpdateTimestamp
	@Column(columnDefinition = "DATETIME(3)", nullable = false)
	private Date modifiedOn;
}
