/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;

import java.io.Serializable;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entity.Miejsce;
import entity.Kategoria;
import entity.Wydarzenie;

import java.util.ArrayList;
import java.util.Date;

import entity.Zdjecie;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Xaoo
 */
public class WydarzenieFacade implements Serializable {

	public WydarzenieFacade(EntityManagerFactory emf) {
		this.emf = emf;
	}

	private EntityManagerFactory emf = null;

	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

	public void create(Wydarzenie wydarzenie) {
		if (wydarzenie.gtetZdjecia() == null) {
			wydarzenie.setZdjecia(new ArrayList<Zdjecie>());
		}
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Miejsce miejsce = wydarzenie.gtetMiejsce();
			if (miejsce != null) {
				miejsce = em.getReference(miejsce.getClass(),
						miejsce.getIdMiejsce());
				wydarzenie.setMiejsce(miejsce);
			}
			Kategoria kategoria = wydarzenie.gtetKategorie();
			if (kategoria != null) {
				kategoria = em.getReference(kategoria.getClass(),
						kategoria.getIdKategoria());
				wydarzenie.setKategorie(kategoria);
			}
			List<Zdjecie> attachedZdjecieCollection = new ArrayList<Zdjecie>();
			for (Zdjecie zdjecieCollectionZdjecieToAttach : wydarzenie
					.gtetZdjecia()) {
				zdjecieCollectionZdjecieToAttach = em.getReference(
						zdjecieCollectionZdjecieToAttach.getClass(),
						zdjecieCollectionZdjecieToAttach.getIdZdjecie());
				attachedZdjecieCollection.add(zdjecieCollectionZdjecieToAttach);
			}
			wydarzenie.setZdjecia(attachedZdjecieCollection);
			em.persist(wydarzenie);
			if (miejsce != null) {
				miejsce.gtetWydarzenia().add(wydarzenie);
				miejsce = em.merge(miejsce);
			}
			if (kategoria != null) {
				kategoria.gtetWydarzenia().add(wydarzenie);
				kategoria = em.merge(kategoria);
			}
			for (Zdjecie zdjecieCollectionZdjecie : wydarzenie.gtetZdjecia()) {
				Wydarzenie oldWydarzenieOfZdjecieCollectionZdjecie = zdjecieCollectionZdjecie
						.gtetWydarzenie();
				zdjecieCollectionZdjecie.setWydarzenie(wydarzenie);
				zdjecieCollectionZdjecie = em.merge(zdjecieCollectionZdjecie);
				if (oldWydarzenieOfZdjecieCollectionZdjecie != null) {
					oldWydarzenieOfZdjecieCollectionZdjecie.gtetZdjecia()
							.remove(zdjecieCollectionZdjecie);
					oldWydarzenieOfZdjecieCollectionZdjecie = em
							.merge(oldWydarzenieOfZdjecieCollectionZdjecie);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public void edit(Wydarzenie wydarzenie) throws IllegalOrphanException,
			NonexistentEntityException, Exception {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Wydarzenie persistentWydarzenie = em.find(Wydarzenie.class,
					wydarzenie.getIdWydarzenie());
			Miejsce miejsceOld = persistentWydarzenie.gtetMiejsce();
			Miejsce miejsceNew = wydarzenie.gtetMiejsce();
			Kategoria kategoriaOld = persistentWydarzenie.gtetKategorie();
			Kategoria kategoriaNew = wydarzenie.gtetKategorie();
			List<Zdjecie> zdjecieCollectionOld = persistentWydarzenie
					.gtetZdjecia();
			List<Zdjecie> zdjecieCollectionNew = wydarzenie.gtetZdjecia();
			List<String> illegalOrphanMessages = null;
			for (Zdjecie zdjecieCollectionOldZdjecie : zdjecieCollectionOld) {
				if (!zdjecieCollectionNew.contains(zdjecieCollectionOldZdjecie)) {
					if (illegalOrphanMessages == null) {
						illegalOrphanMessages = new ArrayList<String>();
					}
					illegalOrphanMessages.add("You must retain Zdjecie "
							+ zdjecieCollectionOldZdjecie
							+ " since its wydarzenie field is not nullable.");
				}
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			if (miejsceNew != null) {
				miejsceNew = em.getReference(miejsceNew.getClass(),
						miejsceNew.getIdMiejsce());
				wydarzenie.setMiejsce(miejsceNew);
			}
			if (kategoriaNew != null) {
				kategoriaNew = em.getReference(kategoriaNew.getClass(),
						kategoriaNew.getIdKategoria());
				wydarzenie.setKategorie(kategoriaNew);
			}
			List<Zdjecie> attachedZdjecieCollectionNew = new ArrayList<Zdjecie>();
			for (Zdjecie zdjecieCollectionNewZdjecieToAttach : zdjecieCollectionNew) {
				zdjecieCollectionNewZdjecieToAttach = em.getReference(
						zdjecieCollectionNewZdjecieToAttach.getClass(),
						zdjecieCollectionNewZdjecieToAttach.getIdZdjecie());
				attachedZdjecieCollectionNew
						.add(zdjecieCollectionNewZdjecieToAttach);
			}
			zdjecieCollectionNew = attachedZdjecieCollectionNew;
			wydarzenie.setZdjecia(zdjecieCollectionNew);
			wydarzenie = em.merge(wydarzenie);
			if (miejsceOld != null && !miejsceOld.equals(miejsceNew)) {
				miejsceOld.gtetWydarzenia().remove(wydarzenie);
				miejsceOld = em.merge(miejsceOld);
			}
			if (miejsceNew != null && !miejsceNew.equals(miejsceOld)) {
				miejsceNew.gtetWydarzenia().add(wydarzenie);
				miejsceNew = em.merge(miejsceNew);
			}
			if (kategoriaOld != null && !kategoriaOld.equals(kategoriaNew)) {
				kategoriaOld.gtetWydarzenia().remove(wydarzenie);
				kategoriaOld = em.merge(kategoriaOld);
			}
			if (kategoriaNew != null && !kategoriaNew.equals(kategoriaOld)) {
				kategoriaNew.gtetWydarzenia().add(wydarzenie);
				kategoriaNew = em.merge(kategoriaNew);
			}
			for (Zdjecie zdjecieCollectionNewZdjecie : zdjecieCollectionNew) {
				if (!zdjecieCollectionOld.contains(zdjecieCollectionNewZdjecie)) {
					Wydarzenie oldWydarzenieOfZdjecieCollectionNewZdjecie = zdjecieCollectionNewZdjecie
							.gtetWydarzenie();
					zdjecieCollectionNewZdjecie.setWydarzenie(wydarzenie);
					zdjecieCollectionNewZdjecie = em
							.merge(zdjecieCollectionNewZdjecie);
					if (oldWydarzenieOfZdjecieCollectionNewZdjecie != null
							&& !oldWydarzenieOfZdjecieCollectionNewZdjecie
									.equals(wydarzenie)) {
						oldWydarzenieOfZdjecieCollectionNewZdjecie
								.gtetZdjecia().remove(
										zdjecieCollectionNewZdjecie);
						oldWydarzenieOfZdjecieCollectionNewZdjecie = em
								.merge(oldWydarzenieOfZdjecieCollectionNewZdjecie);
					}
				}
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() == 0) {
				Integer id = wydarzenie.getIdWydarzenie();
				if (findWydarzenie(id) == null) {
					throw new NonexistentEntityException(
							"The wydarzenie with id " + id
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
			Wydarzenie wydarzenie;
			try {
				wydarzenie = em.getReference(Wydarzenie.class, id);
				wydarzenie.getIdWydarzenie();
			} catch (EntityNotFoundException enfe) {
				throw new NonexistentEntityException("The wydarzenie with id "
						+ id + " no longer exists.", enfe);
			}
			List<String> illegalOrphanMessages = null;
			List<Zdjecie> zdjecieCollectionOrphanCheck = wydarzenie
					.gtetZdjecia();
			for (Zdjecie zdjecieCollectionOrphanCheckZdjecie : zdjecieCollectionOrphanCheck) {
				if (illegalOrphanMessages == null) {
					illegalOrphanMessages = new ArrayList<String>();
				}
				illegalOrphanMessages
						.add("This Wydarzenie ("
								+ wydarzenie
								+ ") cannot be destroyed since the Zdjecie "
								+ zdjecieCollectionOrphanCheckZdjecie
								+ " in its zdjecieCollection field has a non-nullable wydarzenie field.");
			}
			if (illegalOrphanMessages != null) {
				throw new IllegalOrphanException(illegalOrphanMessages);
			}
			Miejsce miejsce = wydarzenie.gtetMiejsce();
			if (miejsce != null) {
				miejsce.gtetWydarzenia().remove(wydarzenie);
				miejsce = em.merge(miejsce);
			}
			Kategoria kategoria = wydarzenie.gtetKategorie();
			if (kategoria != null) {
				kategoria.gtetWydarzenia().remove(wydarzenie);
				kategoria = em.merge(kategoria);
			}
			em.remove(wydarzenie);
			em.getTransaction().commit();
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

	public List<Wydarzenie> findWydarzenieEntities() {
		return findWydarzenieEntities(true, -1, -1);
	}

	public List<Wydarzenie> findWydarzenieEntities(int maxResults,
			int firstResult) {
		return findWydarzenieEntities(false, maxResults, firstResult);
	}

	private List<Wydarzenie> findWydarzenieEntities(boolean all,
			int maxResults, int firstResult) {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			cq.select(cq.from(Wydarzenie.class));
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

	public Wydarzenie findWydarzenie(Integer id) {
		EntityManager em = getEntityManager();
		try {
			return em.find(Wydarzenie.class, id);
		} finally {
			em.close();
		}
	}

	public int getWydarzenieCount() {
		EntityManager em = getEntityManager();
		try {
			CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
			Root<Wydarzenie> rt = cq.from(Wydarzenie.class);
			cq.select(em.getCriteriaBuilder().count(rt));
			Query q = em.createQuery(cq);
			return ((Long) q.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

	public List<Integer> noweWydarzeniaId(int id) {
		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("Wici");
			AktualizacjaFacade aktualizacjaFacade = new AktualizacjaFacade(emf);
			List<Integer> lst = null;
			if(id == 0){
				lst = getEntityManager().createNamedQuery("Wydarzenie.przyszleWydarzeniaId",Integer.class).setParameter("sysDate", new Date()).getResultList();
			} else {
				Date dataAktualizacji = aktualizacjaFacade.findAktualizacja(id).gtetDataDodania();
	
				lst = getEntityManager().createNamedQuery("Wydarzenie.noweWydarzeniaId",Integer.class)
						.setParameter("data", dataAktualizacji)
						.setParameter("sysDate", new Date()).getResultList();
				System.out.println("noweWydarzeniaId");
			}
			return lst;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Wydarzenie> noweWydarzenia(int id) {
		try {
			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("Wici");
			AktualizacjaFacade aktualizacjaFacade = new AktualizacjaFacade(emf);
			List<Wydarzenie> lst = null;
			if (id == 0) {
				lst = getEntityManager()
						.createNamedQuery("Wydarzenie.przyszleWydarzenia",
								Wydarzenie.class)
						.setParameter("sysDate", new Date()).getResultList();
			} else {
				Date dataAktualizacji = aktualizacjaFacade.findAktualizacja(id)
						.gtetDataDodania();

				lst = getEntityManager()
						.createNamedQuery("Wydarzenie.noweWydarzenia",
								Wydarzenie.class)
						.setParameter("data", dataAktualizacji)
						.setParameter("sysDate", new Date()).getResultList();
			}
			return lst;
		} catch (Exception e) {
			return null;
		}
	}

}
