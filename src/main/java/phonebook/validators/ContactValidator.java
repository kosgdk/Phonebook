package phonebook.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import phonebook.entities.Contact;
import phonebook.services.interfaces.ContactService;

import java.util.List;


@Component("ContactValidator")
public class ContactValidator implements Validator {

    @Autowired
    ContactService contactService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Contact.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Contact contact = (Contact) target;

        String name = contact.getName();
        String phone = contact.getPhone();
        List<Contact> duplicateContacts = contactService.searchByNameOrPhone(name, phone);

        for (Contact duplicateContact : duplicateContacts) {
            if (duplicateContact.getName().equals(name) && !duplicateContact.getId().equals(contact.getId())) {
                errors.rejectValue("name", "contact.name.notUnique");
            }
            if (duplicateContact.getPhone().equals(phone) && !duplicateContact.getId().equals(contact.getId())) {
                errors.rejectValue("phone", "contact.phone.notUnique");
            }
        }

    }
}
