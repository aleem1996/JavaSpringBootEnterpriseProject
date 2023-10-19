package com.gr.censusmanagement.util;

import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import com.gr.censusmanagement.model.response.IdDto;

public class ProjectionUtil {

	private static final SpelAwareProxyProjectionFactory spelAwareProxyProjectionFactory = new SpelAwareProxyProjectionFactory();

	public static <E, P> P toProjection(Class<P> projectionClass, E entity) {
		return spelAwareProxyProjectionFactory.createProjection(projectionClass, entity);
	}

	public static <E> IdDto toIdDto(E entity) {
		return toProjection(IdDto.class, entity);
	}
}
