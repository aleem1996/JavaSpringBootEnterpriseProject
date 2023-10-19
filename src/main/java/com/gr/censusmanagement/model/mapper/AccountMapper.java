package com.gr.censusmanagement.model.mapper;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.external.model.AccountDto;

public class AccountMapper extends BaseMapper {

	public static Account toEntity(AccountDto accountDto) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.typeMap(AccountDto.class, Account.class).addMappings(mapper -> {
			mapper.skip(Account::setId);
		});
		return modelMapper.map(accountDto, Account.class);
	}
}
