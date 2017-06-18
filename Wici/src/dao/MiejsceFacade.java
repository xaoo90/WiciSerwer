/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import entity.Miejsce;

import java.io.Serializable;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entity.Kategoria;
import entity.Uzytkownik;
import entity.Wydarzenie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Xaoo
 */
public class MiejsceFacade implements Serializable {

	public MiejsceFacade(EntityManagerFactory emf) {
		this.emf = emf;
	}

	private EntityManagerFactory emf = null;

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(Miejsce miejsce) {
		if (miejsce.gtetWydarzenia() == null) {
			miejsce.setWydarzenia(new ArrayList<Wydarzenie>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Uzytkownik uzytkownik = miejsce.gtetUzytkownik();
			if (uzytkownik != null) {
				uzytkownik = em.getReference(uzytkownik.getClass(),
						uzytkownik.getIdUzytkownik());
				miejsce.setUzytkownik(uzytkownik);
			}
			List<Wydarzenie> attachedWydarzenieCollection = new ArrayList<Wydarzenie>();
			for (Wydarzenie wydarzenieCollectionWydarzenieToAttach : miejsce
					.gtetWydarzenia()) {
				wydarzenieCollectionWydarzenieToAttach = em.getReference(
						wydarzenieCollectionWydarzenieToAttach.getClass(),
						wydarzenieCollectionWydarzenieToAttach
								.getIdWydarzenie());
				attachedWydarzenieCollection
						.add(wydarzenieCollectionWydarzenieToAttach);
			}
			miejsce.setWydarzenia(attachedWydarzenieCollection);
			em.persist(miejsce);
			if (uzytkownik != null) {
				uzytkownik.getMiejsca().add(miejsce);
				uzytkownik = em.merge(uzytkownik);
			}
			for (Wydarzenie wydarzenieCollectionWydarzenie : miejsce
					.gtetWydarzenia()) {
				Miejsce oldMiejsceOfWydarzenieCollectionWydarzenie = wydarzenieCollectionWydarzenie
						.gtetMiejsce();
				wydarzenieCollectionWydarzenie.setMiejsce(miejsce);
				wydarzenieCollectionWydarzenie = em
						.merge(wydarzenieCollectionWydarzenie);
				if (oldMiejsceOfWydarzenieCollectionWydarzenie != null) {
					oldMiejsceOfWydarzenieCollectionWydarzenie.gtetWydarzenia()
							.remove(wydarzenieCollectionWydarzenie);
					oldMiejsceOfWydarzenieCollectionWydarzenie = em
							.merge(oldMiejsceOfWydarzenieCollectionWydarzenie);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(Miejsce miejsce) throws IllegalOrphanException,
			NonexistentEntityException, Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Miejsce persistentMiejsce = em.find(Miejsce.class,
					miejsce.getIdMiejsce());
			Uzytkownik uzytkownikOld = persistentMiejsce.gtetUzytkownik();
			Uzytkownik uzytkownikNew = miejsce.gtetUzytkownik();
			List<Wydarzenie> wydarzenieCollectionOld = persistentMiejsce
					.gtetWydarzenia();
			List<Wydarzenie> wydarzenieCollectionNew = miejsce.gtetWydarzenia();
			List<String> illegalOrphanMessages = null;
			for (Wydarzenie wydarzenieCollectionOldWydarzenie : wydarzenieCollectionOld) {
				if (!wydarzenieCollectionNew
						.contains(wydarzenieCollectionOldWydarzenie)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain Wydarzenie "
							+ wydarzenieCollectionOldWydarzenie
							+ " since its miejsce field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			if (uzytkownikNew != null) {
				uzytkownikNew = em.getReference(uzytkownikNew.getClass(),
						uzytkownikNew.getIdUzytkownik());
				miejsce.setUzytkownik(uzytkownikNew);
			}
			List<Wydarzenie> attachedWydarzenieCollectionNew = new ArrayList<Wydarzenie>();
			for (Wydarzenie wydarzenieCollectionNewWydarzenieToAttach : wydarzenieCollectionNew) {
				wydarzenieCollectionNewWydarzenieToAttach = em.getReference(
						wydarzenieCollectionNewWydarzenieToAttach.getClass(),
						wydarzenieCollectionNewWydarzenieToAttach
								.getIdWydarzenie());
				attachedWydarzenieCollectionNew
						.add(wydarzenieCollectionNewWydarzenieToAttach);
			}
			wydarzenieCollectionNew = attachedWydarzenieCollectionNew;
			miejsce.setWydarzenia(wydarzenieCollectionNew);
			miejsce = em.merge(miejsce);
			if (uzytkownikOld != null && !uzytkownikOld.equals(uzytkownikNew)) {
				uzytkownikOld.getMiejsca().remove(miejsce);
				uzytkownikOld = em.merge(uzytkownikOld);
			}
			if (uzytkownikNew != null && !uzytkownikNew.equals(uzytkownikOld)) {
				uzytkownikNew.getMiejsca().add(miejsce);
				uzytkownikNew = em.merge(uzytkownikNew);
			}
			for (Wydarzenie wydarzenieCollectionNewWydarzenie : wydarzenieCollectionNew) {
				if (!wydarzenieCollectionOld
						.contains(wydarzenieCollectionNewWydarzenie)) {
					Miejsce oldMiejsceOfWydarzenieCollectionNewWydarzenie = wydarzenieCollectionNewWydarzenie
							.gtetMiejsce();
					wydarzenieCollectionNewWydarzenie.setMiejsce(miejsce);
					wydarzenieCollectionNewWydarzenie = em
							.merge(wydarzenieCollectionNewWydarzenie);
					if (oldMiejsceOfWydarzenieCollectionNewWydarzenie != null
							&& !oldMiejsceOfWydarzenieCollectionNewWydarzenie
									.equals(miejsce)) {
						oldMiejsceOfWydarzenieCollectionNewWydarzenie
								.gtetWydarzenia().remove(
										wydarzenieCollectionNewWydarzenie);
						oldMiejsceOfWydarzenieCollectionNewWydarzenie = em
								.merge(oldMiejsceOfWydarzenieCollectionNewWydarzenie);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Integer id = miejsce.getIdMiejsce();
				if (findMiejsce(id) == null) {
					throw new NonexistentEntityException("The miejsce with id "
							+ id + " no longer exists.");
				}
			}
			throw ex;
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void destroy(Integer id) throws IllegalOrphanException,
			NonexistentEntityException {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Miejsce miejsce;
			try {
				miejsce = em.getReference(Miejsce.class, id);
				miejsce.getIdMiejsce();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The miejsce with id "
						+ id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			List<Wydarzenie> wydarzenieCollectionOrphanCheck = miejsce
					.gtetWydarzenia();
			for (Wydarzenie wydarzenieCollectionOrphanCheckWydarzenie : wydarzenieCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages
						.add("This Miejsce ("
								+ miejsce
								+ ") cannot be destroyed since the Wydarzenie "
								+ wydarzenieCollectionOrphanCheckWydarzenie
								+ " in its wydarzenieCollection field has a non-nullable miejsce field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Uzytkownik uzytkownik = miejsce.gtetUzytkownik();
			if (uzytkownik != null) {
				uzytkownik.getMiejsca().remove(miejsce);
				uzytkownik = em.merge(uzytkownik);
			}
			em.remove(miejsce);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<Miejsce> findMiejsceEntities() {
		return findMiejsceEntities(true, -1, -1);
	}

	public List<Miejsce> findMiejsceEntities(int maxResults, int firstResult) {
		return findMiejsceEntities(false, maxResults, firstResult);
	}

	private List<Miejsce> findMiejsceEntities(boolean all, int maxResults,
			int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Miejsce.class));
			Query q = em.createQuery(cq);
			if (!all) {
				q.setMaxResults(maxResults);
				q.setFirstResult(firstResult);
			}
			return q.getResultList();
		} finally {
			em.close();
		}
	}

	public Miejsce findMiejsce(Integer id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Miejsce.class, id);
		} finally {
			em.close();
		}
	}

	public int getMiejsceCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<Miejsce> rt = cq.from(Miejsce.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

	public List<Miejsce> noweMiejsca(int id) {
		try {
			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("Wici");
			AktualizacjaFacade aktualizacjaFacade = new AktualizacjaFacade(emf);
			List<Miejsce> lst = null;
			if (id == 0) {
				lst = findMiejsceEntities();
			} else {
				Date dataAktualizacji = aktualizacjaFacade.findAktualizacja(id)
						.gtetDataDodania();

				lst = getEntityManager()
						.createNamedQuery("Miejsce.noweMiejsca", Miejsce.class)
						.setParameter("data", dataAktualizacji).getResultList();
			}
			return lst;
		} catch (Exception e) {
			return null;
		}
	}

}
