package phonebook.dao;

import com.vladmihalcea.sql.SQLStatementCountValidator;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;
import phonebook.dao.interfaces.ContactDao;
import phonebook.entities.Contact;

import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.junit.Assert.*;


@RunWith(UnitilsBlockJUnit4ClassRunner.class)
@SpringApplicationContext("applicationContext_ForTests.xml")
@Transactional(value=TransactionMode.DISABLED)
@DataSet("db_test/dataset/basic/contacts/ContactDbBasicTest.mainDataSet.xml")
public class ContactDbBasicTest {

    @SpringBeanByType
    ContactDao dao;

    @BeforeClass
    public static void beforeClass(){
        DatabaseUnitils.updateDatabase();
        DatabaseUnitils.generateDatasetDefinition();
    }

    @Before
    public void setUp(){
        SQLStatementCountValidator.reset();
        CacheManager.getInstance().clearAll();
        DatabaseUnitils.updateSequences();
    }


    @Test
    public void getById_ShouldReturnEntity(){
        Contact contact = dao.getById(2L);
        assertSelectCount(1);
        assertNotNull(contact);
        assertEquals(new Long(2), contact.getId());
        assertEquals("Сергей Иванов", contact.getName());
        assertEquals("79789876543", contact.getPhone());
    }

    @Test
    public void getById_InputNonexistentId_ShouldReturnNull(){
        Contact contact = dao.getById(4L);
        assertSelectCount(1);
        assertNull(contact);
    }

    @Test
    public void getById_InputNull_ShouldReturnNull(){
        Contact contact = dao.getById(null);
        assertSelectCount(0);
        assertNull(contact);
    }

    @Test
    public void getAll_ShouldReturnListOfContactsOrderedByIdDesc(){
        List<Contact> contacts = dao.getAll();
        assertSelectCount(1);
        assertNotNull(contacts);
        assertEquals(3, contacts.size());
        Long id = 3L;
        for (Contact contact : contacts) {
            assertNotNull(contact);
            assertEquals(id, contact.getId());
            id--;
        }
    }

    @Test
    @ExpectedDataSet("db_test/dataset/basic/contacts/ContactDbBasicTest.save_ShouldWriteToDb_ExpectedDataSet.xml")
    public void save_ShouldWriteToDb(){
        Contact contact = new Contact("Test contact 1", "79787774411");
        dao.save(contact);
        assertInsertCount(1);
    }

    @Test
    @ExpectedDataSet("db_test/dataset/basic/contacts/ContactDbBasicTest.update_ShouldUpdateDb_ExpectedDataSet.xml")
    public void update_ShouldUpdateDb(){
        Contact contact = dao.getById(2L);
        contact.setName("Updated name");
        contact.setPhone("79780000000");
        dao.update(contact);
        assertUpdateCount(1);
    }

    @Test
    @ExpectedDataSet("db_test/dataset/basic/contacts/ContactDbBasicTest.delete_ShouldDeleteFromDb.xml")
    public void delete_ShouldDeleteFromDb(){
        Contact contact = dao.getById(2L);
        dao.delete(contact);
        assertDeleteCount(1);
    }

    @Test
    public void delete_UnsavedEntity_ShouldNotFireDeleteQuery(){
        Contact contact = new Contact("Test contact 1", "79787774411");
        dao.delete(contact);
        assertDeleteCount(0);
    }

    @Test
    @ExpectedDataSet("db_test/dataset/basic/contacts/ContactDbBasicTest.delete_ShouldDeleteFromDb.xml")
    public void deleteById_ShouldDeleteFromDb(){
        dao.deleteById(2L);
        assertSelectCount(1);
        assertDeleteCount(1);
    }

    @Test
    public void deleteById_InputNonExistentId_ShouldNotDeleteAnything(){
        dao.deleteById(4L);
        assertSelectCount(1);
        assertDeleteCount(0);
    }

    @Test
    public void deleteById_InputNull_ShouldNotDeleteAnything(){
        dao.deleteById(null);
        assertSelectCount(0);
        assertDeleteCount(0);
    }

    @Test
    public void getByName_ShouldReturnClient(){
        String name = "Сергей Иванов";
        Contact contact = dao.getByName(name);
        assertSelectCount(1);
        assertNotNull(contact);
        assertEquals(name, contact.getName());
    }

    @Test
    public void getByName_CaseInsensitiveName_ShouldReturnNull(){
        Contact contact = dao.getByName("cергей иванов");
        assertSelectCount(1);
        assertNull(contact);
    }

    @Test
    public void getByName_NullName_ShouldReturnNull(){
        Contact contact = dao.getByName(null);
        assertSelectCount(0);
        assertNull(contact);
    }

    @Test
    public void getByName_NonexistentName_ShouldReturnNull(){
        String name = "Nonexistent Name";
        Contact contact = dao.getByName(name);
        assertSelectCount(1);
        assertNull(contact);
    }

    @Test
    public void searchByName_ShouldReturnListOfContacts(){
        List<Contact> contacts = dao.searchByName("ван");
        assertSelectCount(1);
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        assertEquals("Сергей Иванов", contacts.get(0).getName());
        assertEquals("Иван Петров", contacts.get(1).getName());
    }

    @Test
    public void searchByName_CaseInsensitiveName_ShouldReturnListOfClientsOrderedByIdDesc(){
        List<Contact> contacts = dao.searchByName("иван");
        assertSelectCount(1);
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        assertEquals("Сергей Иванов", contacts.get(0).getName());
        assertEquals("Иван Петров", contacts.get(1).getName());
    }

    @Test
    public void searchByName_NullName_ShouldReturnNull(){
        List<Contact> contacts = dao.searchByName(null);
        assertSelectCount(0);
        assertNull(contacts);
    }

    @Test
    public void searchByName_NonexistentName_ShouldReturnEmptyListOfClients(){
        List<Contact> contacts = dao.searchByName("Nonexistent Name");
        assertSelectCount(1);
        assertEquals(0, contacts.size());
    }

    @Test
    public void searchByNameOrPhone_ShouldReturnListOfContacts(){
        List<Contact> contacts = dao.searchByNameOrPhone("Иван Петров", "79785554422");
        assertSelectCount(1);
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        assertEquals((Long) 1L, contacts.get(0).getId());
        assertEquals((Long) 3L, contacts.get(1).getId());
    }

    @Test
    public void searchByNameOrPhone_NullName_ShouldReturnListOfContacts(){
        List<Contact> contacts = dao.searchByNameOrPhone(null, "79785554422");
        assertSelectCount(1);
        assertNotNull(contacts);
        assertEquals(1, contacts.size());
        assertEquals((Long) 3L, contacts.get(0).getId());
    }

}
