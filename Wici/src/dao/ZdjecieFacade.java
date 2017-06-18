/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;

import java.io.Serializable;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entity.Kategoria;
import entity.Wydarzenie;
import entity.Zdjecie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Xaoo
 */
public class ZdjecieFacade implements Serializable {

    public ZdjecieFacade(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Zdjecie zdjecie) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Wydarzenie wydarzenie = zdjecie.gtetWydarzenie();
            if (wydarzenie != null) {
                wydarzenie = em.getReference(wydarzenie.getClass(), wydarzenie.getIdWydarzenie());
                zdjecie.setWydarzenie(wydarzenie);
            }
            em.persist(zdjecie);
            if (wydarzenie != null) {
                wydarzenie.gtetZdjecia().add(zdjecie);
                wydarzenie = em.merge(wydarzenie);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Zdjecie zdjecie) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Zdjecie persistentZdjecie = em.find(Zdjecie.class, zdjecie.getIdZdjecie());
            Wydarzenie wydarzenieOld = persistentZdjecie.gtetWydarzenie();
            Wydarzenie wydarzenieNew = zdjecie.gtetWydarzenie();
            if (wydarzenieNew != null) {
                wydarzenieNew = em.getReference(wydarzenieNew.getClass(), wydarzenieNew.getIdWydarzenie());
                zdjecie.setWydarzenie(wydarzenieNew);
            }
            zdjecie = em.merge(zdjecie);
            if (wydarzenieOld != null && !wydarzenieOld.equals(wydarzenieNew)) {
                wydarzenieOld.gtetZdjecia().remove(zdjecie);
                wydarzenieOld = em.merge(wydarzenieOld);
            }
            if (wydarzenieNew != null && !wydarzenieNew.equals(wydarzenieOld)) {
                wydarzenieNew.gtetZdjecia().add(zdjecie);
                wydarzenieNew = em.merge(wydarzenieNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = zdjecie.getIdZdjecie();
                if (findZdjecie(id) == null) {
                    throw new NonexistentEntityException("The zdjecie with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Zdjecie zdjecie;
            try {
                zdjecie = em.getReference(Zdjecie.class, id);
                zdjecie.getIdZdjecie();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The zdjecie with id " + id + " no longer exists.", enfe);
            }
            Wydarzenie wydarzenie = zdjecie.gtetWydarzenie();
            if (wydarzenie != null) {
                wydarzenie.gtetZdjecia().remove(zdjecie);
                wydarzenie = em.merge(wydarzenie);
            }
            em.remove(zdjecie);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Zdjecie> findZdjecieEntities() {
        return findZdjecieEntities(true, -1, -1);
    }

    public List<Zdjecie> findZdjecieEntities(int maxResults, int firstResult) {
        return findZdjecieEntities(false, maxResults, firstResult);
    }

    private List<Zdjecie> findZdjecieEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Zdjecie.class));
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

    public Zdjecie findZdjecie(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Zdjecie.class, id);
        } finally {
            em.close();
        }
    }

    public int getZdjecieCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Zdjecie> rt = cq.from(Zdjecie.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Zdjecie> noweZdjecia(List<Integer> id){
    	System.out.println(id);
    	//try {
    		List<Zdjecie> lst = new ArrayList<>();//getEntityManager().createNamedQuery("Zdjecie.noweZdjecia", Zdjecie.class).setParameter("id", (int)1).getResultList();
    		
    		
    		//System.out.println("asdasdasasdadasdasddfhdfhfgas " + getEntityManager().createNamedQuery("Zdjecie.noweZdjecia", Zdjecie.class).setParameter("id", 1).getFirstResult());
    		for (Integer integer : id) {
    			for (Zdjecie zdjecie : findZdjecieEntities()) {
    				if(zdjecie.gtetWydarzenie().getIdWydarzenie() == integer)
    					lst.add(zdjecie);
				}
				//lst.addAll(getEntityManager().createNamedQuery("Zdjecie.noweZdjecia", Zdjecie.class).setParameter("id", integer).getResultList());
			}
    		System.out.println("asdssfghkjl;lolkiuyjyhtgrff" + lst);    		

            return lst;
//        } catch (Exception e) {
//        	System.out.println("catch");
//            return null;
//        }
    }
    
}
