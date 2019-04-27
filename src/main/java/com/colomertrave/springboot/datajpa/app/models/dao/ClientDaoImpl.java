package com.colomertrave.springboot.datajpa.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.colomertrave.springboot.datajpa.app.models.entities.Client;

@Repository
public class ClientDaoImpl implements IClientDao {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Client findOne(long id) {
		return em.find(Client.class, id);
	}

	@Override
	public List<Client> findAll() {
		return em.createQuery("from Client", Client.class).getResultList();
	}

	@Override
	public void save(Client client) {
		if (client.getId() != null && client.getId() > 0)
			em.merge(client);		
		else
			em.persist(client);
	}

	@Override
	public void delete(long id) {		
		Client client = findOne(id);
		em.remove(client);		
	}

}
