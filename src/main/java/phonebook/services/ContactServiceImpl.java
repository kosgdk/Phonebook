package phonebook.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phonebook.dao.interfaces.ContactDao;
import phonebook.entities.Contact;
import phonebook.services.interfaces.ContactService;

import java.util.List;

@Service("ClientService")
public class ContactServiceImpl implements ContactService {

    @Autowired
    ContactDao dao;

    @Override
    public Contact getById(Long id) {
        return dao.getById(id);
    }

    @Override
    public Contact getByName(String name) {
        return dao.getByName(name);
    }

    @Override
    public List<Contact> searchByName(String name) {
        return dao.searchByName(name);
    }

    @Override
    public List<Contact> searchByNameOrPhone(String name, String phone){
        return dao.searchByNameOrPhone(name, phone);
    }

    @Override
    public List<Contact> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(Contact contact) {
        dao.save(contact);
    }

    @Override
    public void update(Contact contact) {
        dao.update(contact);
    }

    @Override
    public void delete(Contact contact) {
        dao.delete(contact);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}