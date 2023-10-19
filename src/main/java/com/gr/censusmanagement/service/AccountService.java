package com.gr.censusmanagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.censusmanagement.entity.Account;
import com.gr.censusmanagement.entity.CustomField;
import com.gr.censusmanagement.entity.CustomField.DataType;
import com.gr.censusmanagement.external.model.CustomFieldDto;
import com.gr.censusmanagement.external.model.DefaultFieldsConfigDto;
import com.gr.censusmanagement.external.model.TrcmFormDto;
import com.gr.censusmanagement.model.mapper.TrcmFormMapper;
import com.gr.censusmanagement.repository.AccountRepository;
import com.gr.censusmanagement.repository.ApiConfigurationRepository;
import com.gr.censusmanagement.repository.SourceSystemRepository;
import com.gr.censusmanagement.repository.TrcmFormRepository;
import com.gr.censusmanagement.util.NullAwareBeanUtils;
import com.gr.common.v2.exception.NotFoundException;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private SourceSystemRepository sourceSystemRepository;
	
	@Autowired
	private ApiConfigurationRepository apiConfigurationRepository;
	
	@Autowired
	private TrcmFormRepository trcmFormRepository;

	private static final String membershipOptions = "[{\"type\":\"1\", \"label\": \"Medical Membership\"},{\"type\":\"2\",\"label\": \"Medical Membership with Security Upgrade\"}]";

	public Account findBySourceAccountId(String sourceAccountId) {
		Optional<Account> oAccount = accountRepository.findBySourceAccountId(sourceAccountId);
		if (!oAccount.isPresent()) {
			throw NotFoundException.ofResource(Account.class.getSimpleName(), sourceAccountId);
		}
		return oAccount.get();
	}

	public Account saveOrUpdate(Account newAccount, String apiKey) throws Exception {
		Optional<Account> oExistingAccount = accountRepository.findBySourceAccountId(newAccount.getSourceAccountId());
		if (oExistingAccount.isPresent()) {
			Account existingAccount = oExistingAccount.get();
			NullAwareBeanUtils.copyProperties(existingAccount, newAccount);
			return accountRepository.save(existingAccount);
		} else {
			newAccount.setSourceSystem(sourceSystemRepository.findByApiKey(apiKey).get());
			newAccount.setApiConfiguration(apiConfigurationRepository.findByApiKey(apiKey).get());
			newAccount.setAllowBulkUpload(Boolean.TRUE);
			newAccount.setAddTrcmFormData(Boolean.TRUE);
			Account createdAccount = accountRepository.save(newAccount);
			TrcmFormDto defaultForm = new TrcmFormDto("Form " + createdAccount.getName(), "active", true, null, DefaultFieldsConfigDto.populateDefaultConfigList(newAccount.getSourceSystem().getName(), newAccount.getType()), createdAccount.getId(), membershipOptions, Boolean.TRUE, null);
			CustomFieldDto customField = createAddressCustomFieldForNewAccount(newAccount.getSourceSystem().getName(), newAccount.getType());
			List<CustomFieldDto> customFields = new ArrayList<>();
			customFields.add(customField);
			defaultForm.setCustomFields(customFields);
			trcmFormRepository.save(TrcmFormMapper.toEntity(defaultForm));
			return createdAccount;
		}
	}
	
	private CustomFieldDto createAddressCustomFieldForNewAccount(String source, String accountType) {
		CustomFieldDto addressField = new CustomFieldDto();
		addressField.setDataType(DataType.Address);
		addressField.setIsRequired(Boolean.TRUE);
		addressField.setIsHiddenInForm(Boolean.FALSE);
		addressField.setIsHiddenInTable(Boolean.FALSE);
		addressField.setSortOrder(8);
		addressField.setSortable(Boolean.TRUE);
		addressField.setSearchable(Boolean.TRUE);
		if ("GRID".equalsIgnoreCase(source)) {
			if ("TRCM_SUBSCRIPTION".equalsIgnoreCase(accountType)) {
				addressField.setAttribute("home_address");
				addressField.setLabel("Home");
			} else if ("TRCM_TRAVEL_DAYS".equalsIgnoreCase(accountType)) {
				addressField.setAttribute("travel_address");
				addressField.setLabel("Travel");
			}
		}
		
		if ("AP".equalsIgnoreCase(source)) {
			if ("Named".equalsIgnoreCase(accountType)) {
				addressField.setAttribute("home_address");
				addressField.setLabel("Home");
			} else if ("TRCM".equalsIgnoreCase(accountType)) {
				addressField.setAttribute("travel_address");
				addressField.setLabel("Travel");
			}
		}
		return addressField;
	}

	public Account findBySourceAccountIdOrSync(Account account, String apiKey) throws Exception {
		Optional<Account> oAccount = accountRepository.findBySourceAccountId(account.getSourceAccountId());
		if (!oAccount.isPresent()) {
			return this.saveOrUpdate(account, apiKey);
		}
		return oAccount.get();
	}
	
	public Account findById(String id) {
		Optional<Account> oAccount = accountRepository.findById(id);
		if (oAccount.isPresent()) {
			return oAccount.get();
		}
		return null;
	}
	
	public Account findActiveAccountByName(String name) {
		Optional<Account> oAccount = accountRepository.findByNameAndIsActiveTrue(name);
		if (oAccount.isPresent()) {
			return oAccount.get();
		}
		return null;
	}
}
