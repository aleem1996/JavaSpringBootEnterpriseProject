package com.gr.censusmanagement.entity.generator;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.gr.censusmanagement.entity.BaseEntityWithId;

public class IdGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
		String id = null;
		if (o instanceof BaseEntityWithId) {
			BaseEntityWithId baseEntityWithId = (BaseEntityWithId) o;
			if (baseEntityWithId.getId() != null) {
				id = baseEntityWithId.getId();
			}
		}

		if (id == null) {
			id = UUID.randomUUID().toString();
		}

		return id;
	}
}