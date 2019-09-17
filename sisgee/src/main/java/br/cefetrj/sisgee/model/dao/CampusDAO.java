package br.cefetrj.sisgee.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import br.cefetrj.sisgee.model.entity.Campus;
import br.cefetrj.sisgee.model.entity.Curso;

public class CampusDAO extends GenericDAO<Campus> {
	
	public CampusDAO() {
		super(Campus.class, PersistenceManager.getEntityManager());
	}
	
	public Campus buscarCampusByNome(String nome) {
		manager.clear();
		TypedQuery<Campus> query = manager.createQuery("SELECT c FROM Campus c WHERE c.nomecampus LIKE :nomecampus",
				Campus.class);
		query.setParameter("nomecampus", nome);
		query.setMaxResults(1);
		List<Campus> resultado = query.getResultList();
		return resultado.isEmpty() ? null : resultado.get(0);
	}

}
