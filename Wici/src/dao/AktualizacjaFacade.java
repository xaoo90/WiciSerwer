package dao;

import dao.exceptions.NonexistentEntityException;
import entity.Aktualizacja;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Xaoo
 */
public class AktualizacjaFacade implements Serializable {

    public AktualizacjaFacade(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Aktualizacja aktualizacja) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(aktualizacja);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Aktualizacja aktualizacja) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            aktualizacja = em.merge(aktualizacja);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = aktualizacja.getIdAktualizacja();
                if (findAktualizacja(id) == null) {
                    throw new NonexistentEntityException("The aktualizacja with id " + id + " no longer exists.");
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
            Aktualizacja aktualizacja;
            try {
                aktualizacja = em.getReference(Aktualizacja.class, id);
                aktualizacja.getIdAktualizacja();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The aktualizacja with id " + id + " no longer exists.", enfe);
            }
            em.remove(aktualizacja);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Aktualizacja> findAktualizacjaEntities() {
        return findAktualizacjaEntities(true, -1, -1);
    }

    public List<Aktualizacja> findAktualizacjaEntities(int maxResults, int firstResult) {
        return findAktualizacjaEntities(false, maxResults, firstResult);
    }

    private List<Aktualizacja> findAktualizacjaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Aktualizacja.class));
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

    public Aktualizacja findAktualizacja(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Aktualizacja.class, id);
        } finally {
            em.close();
        }
    }

    public int getAktualizacjaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Aktualizacja> rt = cq.from(Aktualizacja.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public int maxId(){
    	int id = getEntityManager().createNamedQuery("Aktualizacja.ostatniaAktualizacja", Integer.class).getSingleResult();
    	return id;
    }
    
    public Boolean isOstatniaAktualizacja(int idAktualizacja){
		if(maxId() == idAktualizacja)
			return true;
		return false;
    }
    
    
}
