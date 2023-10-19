package com.gr.censusmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CustomField extends BaseEntityWithId {

	private static final long serialVersionUID = 1L;

	@Column(length = 45)
	private String label;

	@Column(length = 45)
	private String attribute;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private DataType dataType;

	private Boolean isRequired;
	private Boolean sortable;
	private Boolean searchable;
	private String status;
	private String options;
	private String fieldInfo;
	private Integer sortOrder;
	private Boolean isHiddenInForm;
	private Boolean isHiddenInTable;
	private String fieldRegex;
	private Boolean disableInEdit;
	
	public enum DataType {

		ShortText("ShortText"), LongText("LongText"), RadioButton("RadioButton"), CheckBox("CheckBox"), PhoneNumber("PhoneNumber"), DropDown("DropDown"), Date("Date"),
		Numeric("Numeric"), Email("Email"), Address("Address");

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
