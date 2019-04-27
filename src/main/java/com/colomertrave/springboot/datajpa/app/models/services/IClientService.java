package com.colomertrave.springboot.datajpa.app.models.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.colomertrave.springboot.datajpa.app.models.entities.Client;

public interface IClientService {
	
	List<Client> findAll();
	
	Page<Client> findAll(Pageable pageable);
	
	void save(Client client);
	
	Optional<Client> findOne(long id);
	
	void delete(long id);
}
