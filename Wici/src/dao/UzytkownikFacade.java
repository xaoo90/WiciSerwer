/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Miejsce;
import entity.Uzytkownik;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Xaoo
 */
public class UzytkownikFacade implements Serializable {

    public UzytkownikFacade(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Uzytkownik uzytkownik) {
        if (uzytkownik.getMiejsca() == null) {
            uzytkownik.setMiejsca(new ArrayList<Miejsce>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Miejsce> attachedMiejsceCollection = new ArrayList<Miejsce>();
            for (Miejsce miejsceCollectionMiejsceToAttach : uzytkownik.getMiejsca()) {
                miejsceCollectionMiejsceToAttach = em.getReference(miejsceCollectionMiejsceToAttach.getClass(), miejsceCollectionMiejsceToAttach.getIdMiejsce());
                attachedMiejsceCollection.add(miejsceCollectionMiejsceToAttach);
            }
            uzytkownik.setMiejsca(attachedMiejsceCollection);
            em.persist(uzytkownik);
            for (Miejsce miejsceCollectionMiejsce : uzytkownik.getMiejsca()) {
                Uzytkownik oldUzytkownikOfMiejsceCollectionMiejsce = miejsceCollectionMiejsce.gtetUzytkownik();
                miejsceCollectionMiejsce.setUzytkownik(uzytkownik);
                miejsceCollectionMiejsce = em.merge(miejsceCollectionMiejsce);
                if (oldUzytkownikOfMiejsceCollectionMiejsce != null) {
                    oldUzytkownikOfMiejsceCollectionMiejsce.getMiejsca().remove(miejsceCollectionMiejsce);
                    oldUzytkownikOfMiejsceCollectionMiejsce = em.merge(oldUzytkownikOfMiejsceCollectionMiejsce);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Uzytkownik uzytkownik) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Uzytkownik persistentUzytkownik = em.find(Uzytkownik.class, uzytkownik.getIdUzytkownik());
            List<Miejsce> miejsceCollectionOld = persistentUzytkownik.getMiejsca();
            List<Miejsce> miejsceCollectionNew = uzytkownik.getMiejsca();
            List<String> illegalOrphanMessages = null;
            for (Miejsce miejsceCollectionOldMiejsce : miejsceCollectionOld) {
                if (!miejsceCollectionNew.contains(miejsceCollectionOldMiejsce)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Miejsce " + miejsceCollectionOldMiejsce + " since its uzytkownik field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Miejsce> attachedMiejsceCollectionNew = new ArrayList<Miejsce>();
            for (Miejsce miejsceCollectionNewMiejsceToAttach : miejsceCollectionNew) {
                miejsceCollectionNewMiejsceToAttach = em.getReference(miejsceCollectionNewMiejsceToAttach.getClass(), miejsceCollectionNewMiejsceToAttach.getIdMiejsce());
                attachedMiejsceCollectionNew.add(miejsceCollectionNewMiejsceToAttach);
            }
            miejsceCollectionNew = attachedMiejsceCollectionNew;
            uzytkownik.setMiejsca(miejsceCollectionNew);
            uzytkownik = em.merge(uzytkownik);
            for (Miejsce miejsceCollectionNewMiejsce : miejsceCollectionNew) {
                if (!miejsceCollectionOld.contains(miejsceCollectionNewMiejsce)) {
                    Uzytkownik oldUzytkownikOfMiejsceCollectionNewMiejsce = miejsceCollectionNewMiejsce.gtetUzytkownik();
                    miejsceCollectionNewMiejsce.setUzytkownik(uzytkownik);
                    miejsceCollectionNewMiejsce = em.merge(miejsceCollectionNewMiejsce);
                    if (oldUzytkownikOfMiejsceCollectionNewMiejsce != null && !oldUzytkownikOfMiejsceCollectionNewMiejsce.equals(uzytkownik)) {
                        oldUzytkownikOfMiejsceCollectionNewMiejsce.getMiejsca().remove(miejsceCollectionNewMiejsce);
                        oldUzytkownikOfMiejsceCollectionNewMiejsce = em.merge(oldUzytkownikOfMiejsceCollectionNewMiejsce);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = uzytkownik.getIdUzytkownik();
                if (findUzytkownik(id) == null) {
                    throw new NonexistentEntityException("The uzytkownik with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Uzytkownik uzytkownik;
            try {
                uzytkownik = em.getReference(Uzytkownik.class, id);
                uzytkownik.getIdUzytkownik();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The uzytkownik with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Miejsce> miejsceCollectionOrphanCheck = uzytkownik.getMiejsca();
            for (Miejsce miejsceCollectionOrphanCheckMiejsce : miejsceCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Uzytkownik (" + uzytkownik + ") cannot be destroyed since the Miejsce " + miejsceCollectionOrphanCheckMiejsce + " in its miejsceCollection field has a non-nullable uzytkownik field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(uzytkownik);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Uzytkownik> findUzytkownikEntities() {
        return findUzytkownikEntities(true, -1, -1);
    }

    public List<Uzytkownik> findUzytkownikEntities(int maxResults, int firstResult) {
        return findUzytkownikEntities(false, maxResults, firstResult);
    }

    private List<Uzytkownik> findUzytkownikEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Uzytkownik.class));
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

    public Uzytkownik findUzytkownik(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Uzytkownik.class, id);
        } finally {
            em.close();
        }
    }

    public int getUzytkownikCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Uzytkownik> rt = cq.from(Uzytkownik.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
