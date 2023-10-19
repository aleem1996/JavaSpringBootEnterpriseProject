package com.gr.censusmanagement.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.gr.censusmanagement.entity.Address;
import com.gr.censusmanagement.external.model.AddressDto;

public class AddressMapper extends BaseMapper {

	public static AddressDto toDto(Address address) {
		ModelMapper modelMapper = getModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		if (address != null) {
			AddressDto addressDto = modelMapper.map(address, AddressDto.class);
			return addressDto;			
		} 
		return null;
	}

	public static List<AddressDto> toAddressDtoList(List<Address> addressList) {
		List<AddressDto> addressDtoList = addressList.stream()
				.map(address -> new AddressDto(address.getId(), address.getCity(), address.getState(), address.getCountry(), address.getLineOne(), address.getLineTwo(),
						address.getZipCode(), address.getLatitude(), address.getLongitude(), address.getIsMilitaryAddress(), address.getIsManual(), address.getIsOptional()))
				.collect(Collectors.toList());
		return addressDtoList;
	}
}
