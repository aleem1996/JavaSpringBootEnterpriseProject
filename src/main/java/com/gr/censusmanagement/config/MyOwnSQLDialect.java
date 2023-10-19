package com.gr.censusmanagement.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MyOwnSQLDialect extends MySQL5Dialect {

	public MyOwnSQLDialect() {
		super();
		this.registerFunction("group_concat", new SQLFunctionTemplate(StandardBasicTypes.STRING, "group_concat(DISTINCT ?1)"));
	}
}
