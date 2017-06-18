/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import entity.Kategoria;

import java.io.Serializable;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entity.Wydarzenie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Xaoo
 */
public class KategoriaFacade implements Serializable {

	public KategoriaFacade(EntityManagerFactory emf) {
		this.emf = emf;
	}

	private EntityManagerFactory emf = null;

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(Kategoria kategoria) {
		if (kategoria.gtetWydarzenia() == null) {
			kategoria.setWydarzenia(new ArrayList<Wydarzenie>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			List<Wydarzenie> attachedWydarzenieCollection = new ArrayList<Wydarzenie>();
			for (Wydarzenie wydarzenieCollectionWydarzenieToAttach : kategoria
					.gtetWydarzenia()) {
				wydarzenieCollectionWydarzenieToAttach = em.getReference(
						wydarzenieCollectionWydarzenieToAttach.getClass(),
						wydarzenieCollectionWydarzenieToAttach
								.getIdWydarzenie());
				attachedWydarzenieCollection
						.add(wydarzenieCollectionWydarzenieToAttach);
			}
			kategoria.setWydarzenia(attachedWydarzenieCollection);
			em.persist(kategoria);
			for (Wydarzenie wydarzenieCollectionWydarzenie : kategoria
					.gtetWydarzenia()) {
				Kategoria oldKategoriaOfWydarzenieCollectionWydarzenie = wydarzenieCollectionWydarzenie
						.gtetKategorie();
				wydarzenieCollectionWydarzenie.setKategorie(kategoria);
				wydarzenieCollectionWydarzenie = em
						.merge(wydarzenieCollectionWydarzenie);
				if (oldKategoriaOfWydarzenieCollectionWydarzenie != null) {
					oldKategoriaOfWydarzenieCollectionWydarzenie
							.gtetWydarzenia().remove(
									wydarzenieCollectionWydarzenie);
					oldKategoriaOfWydarzenieCollectionWydarzenie = em
							.merge(oldKategoriaOfWydarzenieCollectionWydarzenie);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(Kategoria kategoria) throws IllegalOrphanException,
			NonexistentEntityException, Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Kategoria persistentKategoria = em.find(Kategoria.class,
					kategoria.getIdKategoria());
			List<Wydarzenie> wydarzenieCollectionOld = persistentKategoria
					.gtetWydarzenia();
			List<Wydarzenie> wydarzenieCollectionNew = kategoria
					.gtetWydarzenia();
			List<String> illegalOrphanMessages = null;
			for (Wydarzenie wydarzenieCollectionOldWydarzenie : wydarzenieCollectionOld) {
				if (!wydarzenieCollectionNew
						.contains(wydarzenieCollectionOldWydarzenie)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain Wydarzenie "
							+ wydarzenieCollectionOldWydarzenie
							+ " since its kategoria field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
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
			kategoria.setWydarzenia(wydarzenieCollectionNew);
			kategoria = em.merge(kategoria);
			for (Wydarzenie wydarzenieCollectionNewWydarzenie : wydarzenieCollectionNew) {
				if (!wydarzenieCollectionOld
						.contains(wydarzenieCollectionNewWydarzenie)) {
					Kategoria oldKategoriaOfWydarzenieCollectionNewWydarzenie = wydarzenieCollectionNewWydarzenie
							.gtetKategorie();
					wydarzenieCollectionNewWydarzenie.setKategorie(kategoria);
					wydarzenieCollectionNewWydarzenie = em
							.merge(wydarzenieCollectionNewWydarzenie);
					if (oldKategoriaOfWydarzenieCollectionNewWydarzenie != null
							&& !oldKategoriaOfWydarzenieCollectionNewWydarzenie
									.equals(kategoria)) {
						oldKategoriaOfWydarzenieCollectionNewWydarzenie
								.gtetWydarzenia().remove(
										wydarzenieCollectionNewWydarzenie);
						oldKategoriaOfWydarzenieCollectionNewWydarzenie = em
								.merge(oldKategoriaOfWydarzenieCollectionNewWydarzenie);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Integer id = kategoria.getIdKategoria();
				if (findKategoria(id) == null) {
					throw new NonexistentEntityException(
							"The kategoria with id " + id
									+ " no longer exists.");
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
			Kategoria kategoria;
			try {
				kategoria = em.getReference(Kategoria.class, id);
				kategoria.getIdKategoria();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The kategoria with id "
						+ id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			List<Wydarzenie> wydarzenieCollectionOrphanCheck = kategoria
					.gtetWydarzenia();
			for (Wydarzenie wydarzenieCollectionOrphanCheckWydarzenie : wydarzenieCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages
						.add("This Kategoria ("
								+ kategoria
								+ ") cannot be destroyed since the Wydarzenie "
								+ wydarzenieCollectionOrphanCheckWydarzenie
								+ " in its wydarzenieCollection field has a non-nullable kategoria field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			em.remove(kategoria);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<Kategoria> findKategoriaEntities() {
		return findKategoriaEntities(true, -1, -1);
	}

	public List<Kategoria> findKategoriaEntities(int maxResults, int firstResult) {
		return findKategoriaEntities(false, maxResults, firstResult);
	}

	private List<Kategoria> findKategoriaEntities(boolean all, int maxResults,
			int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Kategoria.class));
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

	public Kategoria findKategoria(Integer id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Kategoria.class, id);
		} finally {
			em.close();
		}
	}

	public int getKategoriaCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<Kategoria> rt = cq.from(Kategoria.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

	public List<Kategoria> noweKategorie(int id) {
		try {
			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("Wici");
			AktualizacjaFacade aktualizacjaFacade = new AktualizacjaFacade(emf);
			List<Kategoria> lst = null;
			if (id == 0) {
				lst = findKategoriaEntities();
			} else {

				Date dataAktualizacji = aktualizacjaFacade.findAktualizacja(id)
						.gtetDataDodania();

				lst = getEntityManager()
						.createNamedQuery("Kategoria.noweKategorie",Kategoria.class)
						.setParameter("data", dataAktualizacji).getResultList();
			}
			return lst;
		} catch (Exception e) {
			return null;
		}
	}

}
