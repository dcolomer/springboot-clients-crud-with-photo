package com.colomertrave.springboot.datajpa.app.models.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.colomertrave.springboot.datajpa.app.models.dao.IClientDao;
import com.colomertrave.springboot.datajpa.app.models.dao.IClientDaoCR;

import com.colomertrave.springboot.datajpa.app.models.entities.Client;

@Service("clientServiceJpa")
public class ClientServiceImpl implements IClientService {

	@Autowired
	//IClientDao clientDao;
	IClientDaoCR clientDao;	
	
	@Override @Transactional(readOnly=true)
	public Optional<Client> findOne(long id) {
		//return clientDao.findOne(id);
		return clientDao.findById(id);
	}
	
	@Override @Transactional(readOnly=true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}

	@Override @Transactional
	public void save(Client client) {
		clientDao.save(client);		
	}
	
	@Override @Transactional
	public void delete(long id) {
		clientDao.deleteById(id);		
	}

	@Override
	public Page<Client> findAll(Pageable pageable) {
		return clientDao.findAll(pageable);
	}

}
