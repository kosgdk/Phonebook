package phonebook.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import phonebook.dao.interfaces.ContactDao;
import phonebook.entities.Contact;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository("ClientDaoHibernateImpl")
@Transactional(propagation=Propagation.REQUIRED)
public class ContactDaoHibernateImpl implements ContactDao {

    @Autowired
    protected SessionFactory sessionFactory;

    private Session currentSession(){
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Contact getById(Long id){
        if(id == null) return null;
        return currentSession().get(Contact.class, id);
    }

    @Override
    public Contact getByName(String name) {
        if (name == null) return null;

        String hql = "from Contact where name=:name";

        Query<Contact> query = currentSession().createQuery(hql, Contact.class)
                .setParameter("name", name)
                .setCacheable(true);

        try{
            return query.getSingleResult();
        }catch (PersistenceException e){
            return null;
        }
    }

    @Override
    public List<Contact> searchByName(String name) {
        if (name == null) return null;
        String[] words = name.toLowerCase().split(" ");

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = cb.createQuery(Contact.class);
        Root<Contact> root = criteriaQuery.from(Contact.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();

        for (String word : words) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%"+word+"%"));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        criteriaQuery.orderBy(cb.desc(root.get("id")));

        TypedQuery<Contact> typedQuery = currentSession().createQuery(criteriaQuery).setMaxResults(20);
        List<Contact> contacts = typedQuery.getResultList();
        return contacts;
    }

    public List<Contact> searchByNameOrPhone(String name, String phone){
        if (name == null && phone == null) return new ArrayList<>();

        String hql = "from Contact where name=:name or phone=:phone";

        Query<Contact> query = currentSession().createQuery(hql, Contact.class)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .setCacheable(true);

        return query.getResultList();
    }

    @Override
    public List<Contact> getAll() {
        
        String hql = "from Contact order by id desc";

        Query<Contact> query = currentSession().createQuery(hql, Contact.class)
                .setCacheable(true);

        return query.getResultList();
    }

    @Override
    public void save(Contact contact) {
        if(contact != null) currentSession().save(contact);
    }

    @Override
    public void update(Contact contact) {
        currentSession().merge(contact);
    }

    @Override
    public void delete(Contact contact) {
        if(contact != null) currentSession().delete(contact);
    }

    @Override
    public void deleteById(Long id){
        delete(getById(id));
    }
    
}