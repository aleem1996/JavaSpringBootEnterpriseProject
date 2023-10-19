package com.gr.censusmanagement.model.mapper;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.AccountPoc;
import com.gr.censusmanagement.external.model.AccountPocDto;

public class AccountPocMapper extends BaseMapper {
	public static AccountPoc toEntity(AccountPocDto accountPocDto) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		AccountPoc accountPoc = modelMapper.map(accountPocDto, AccountPoc.class);
		accountPoc.setId(null);
		accountPoc.setAccount(null);
		return accountPoc;
	}
}