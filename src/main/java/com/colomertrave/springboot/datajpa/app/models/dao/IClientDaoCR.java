package com.colomertrave.springboot.datajpa.app.models.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;

import com.colomertrave.springboot.datajpa.app.models.entities.Client;

public interface IClientDaoCR extends JpaRepository<Client, Serializable>{

}
