package com.gr.censusmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DefaultFieldsConfig extends BaseEntityWithId {
	
	private static final long serialVersionUID = 1L;
	
	@Column(length = 45)
	private String label;

	@Column(length = 45)
	private String attribute;
	
	private String fieldInfo;
	private Boolean isRequired;
	private Integer sortOrder;
	private Boolean isHiddenInForm;
	private Boolean isHiddenInTable;
	private String fieldRegex;
	private Boolean searchable;
	private Boolean disableInEdit;
	@Lob
	private String options;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private DataType dataType;
	
	public enum DataType {

		Date("Date"), ShortText("ShortText"), Email("Email"), DropDown("DropDown");

		private String value;

		private DataType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static DataType fromValue(String value) {

			if (value != null) {
				for (DataType type : values()) {

					if (value.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		public static class Fields {
			public static final String VALUE = "value";
		}
	}


}
