package com.gr.censusmanagement.model.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class BaseMapper {

	public static ModelMapper getModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}

	public static <D> D map(Object source, Class<D> destinationType) {
		return getModelMapper().map(source, destinationType);
	}

	public static <D> D mapStrict(Object source, Class<D> destinationType) {
		ModelMapper mapper = getModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper.map(source, destinationType);
	}

}
