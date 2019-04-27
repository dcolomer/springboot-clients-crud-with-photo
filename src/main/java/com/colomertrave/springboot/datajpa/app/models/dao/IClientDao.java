package com.colomertrave.springboot.datajpa.app.models.dao;

import java.util.List;

import com.colomertrave.springboot.datajpa.app.models.entities.Client;

public interface IClientDao {

	List<Client> findAll();
	
	void save(Client client);
	
	Client findOne(long id);
	
	void delete(long id);
}
