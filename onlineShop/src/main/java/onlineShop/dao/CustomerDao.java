package onlineShop.dao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;


import onlineShop.model.Authorities;
import onlineShop.model.Cart;
import onlineShop.model.Customer;
import onlineShop.model.User;

@Repository    //Repository is a interface, which annotated by component, spring will automatically start an instance of component
public class CustomerDao {
  
	@Autowired    // inject sessionFactory if there is an instance
	private SessionFactory sessionFactory;
	
	public void addCustomer(Customer customer) {
		customer.getUser().setEnabled(true);
		
		Authorities authorities = new Authorities();
		authorities.setAuthorities("ROLE_USER");
		authorities.setEmailId(customer.getUser().getEmailId());
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		customer.setCart(cart);
		
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			session.save(authorities);
			session.save(customer);
			session.getTransaction().commit();
		} catch (Exception e){
			e.printStackTrace();
			session.getTransaction().rollback(); // if there is an exception then go back one step
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	public Customer getCustomerByUserName(String userName) {
		User user = null;
		try(Session session = sessionFactory.openSession()) {  // will close automatically
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class); // get methods from that class
			Root<User> root = criteriaQuery.from(User.class); //表的根节点, sql是一个树状结构
			criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("emailId"),userName));
			user = session.createQuery(criteriaQuery).getSingleResult();

		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(user != null) {
			return user.getCustomer();
		}
		
		return null;
	}
	
}
