package index;

import java.util.List;
import javax.jdo.annotations.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class PostDao {

	// Injected database connection:
	@PersistenceUnit
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("post.odb");
    public static PostDao instance;
    
    public static PostDao getInstance() {
    	if (instance == null) {
    		instance = new PostDao();
    	}
    	
    	return instance;
    }
 
    @Transactional
    public void persist(Post post) {
    	EntityManager em = emf.createEntityManager();
    	EntityTransaction tx = em.getTransaction();
    	tx.begin();
    	
        em.persist(post);
        
        tx.commit();
        em.close();
    }
    
    @Transactional
    public void update(Post post) {
    	EntityManager em = emf.createEntityManager();
    	EntityTransaction tx = em.getTransaction();
    	Post updated = em.find(Post.class, post.word);
    	
    	tx.begin();
    	try {
    		updated.documents = post.documents;
    		tx.commit();
    	} catch (Exception e) {
    		tx.rollback();
    	}
        
        em.close();
    }
    
    
    public List<Post> getAllPosts() {
    	EntityManager em = emf.createEntityManager();
        TypedQuery<Post> query = em.createQuery(
            "SELECT g FROM Post g", Post.class);
        List<Post> result = query.getResultList();
        em.close();
        
        return result;
    }
    
    public Post getPost(String word) {
    	EntityManager em = emf.createEntityManager();
    	
    	String queryString = String.format("SELECT g FROM Post g WHERE g.word = '%s'", word);
        TypedQuery<Post> query = em.createQuery(queryString, Post.class);
    	Post p = (Post) JpaResultHelper.getSingleResultOrNull(query);
    	
    	em.close();
    	return p;
    }
    
    @Transactional
    public void deleteAll() {
    	EntityManager em = emf.createEntityManager();
    	EntityTransaction tx = em.getTransaction();
    	tx.begin();
    	try {
    		em.createQuery(
    	            "DELETE FROM Post",
    	            Post.class).executeUpdate();
    		tx.commit();
    	} catch (Exception e){
    		e.printStackTrace();
    		tx.rollback();
    	}
    	
    	em.close();
    }
    
    
    public static class JpaResultHelper {
        public static Object getSingleResultOrNull(Query query){
            List results = query.getResultList();
            if (results.size() >= 2)
            	System.out.println(String.format("Got %d -> %s", results.size(), results.toString()));
            
            if (results.isEmpty()) return null;
            else if (results.size() == 1) return results.get(0);
            throw new NonUniqueResultException();
        }
    }
}
