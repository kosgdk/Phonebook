package phonebook.services.interfaces;

import phonebook.entities.Contact;

import java.util.List;

public interface ContactService {

    Contact getById(Long id);

    Contact getByName(String name);

    List<Contact> searchByName(String name);

    List<Contact> searchByNameOrPhone(String name, String phone);

    List<Contact> getAll();

    void save(Contact contact);

    void update(Contact contact);

    void delete(Contact contact);

    void deleteById(Long id);
	
}
