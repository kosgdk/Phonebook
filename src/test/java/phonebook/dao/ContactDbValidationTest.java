package phonebook.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;
import phonebook.dao.interfaces.ContactDao;
import phonebook.entities.Contact;
import testservices.CauseExceptionMatcher;

import java.sql.SQLDataException;
import java.sql.SQLIntegrityConstraintViolationException;


@RunWith(UnitilsBlockJUnit4ClassRunner.class)
@SpringApplicationContext("applicationContext_ForTests_DisabledBeanValidation.xml")
@Transactional(TransactionMode.DISABLED)
public class ContactDbValidationTest {

    @SpringBeanByType
    ContactDao dao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass(){
        DatabaseUnitils.updateDatabase();
        DatabaseUnitils.generateDatasetDefinition();
    }

    @Before
    public void setUp() {
        DatabaseUnitils.updateSequences();
    }


    @Test
    public void name_ShouldNotBeNull_OrThrowException() {
        Contact contact = new Contact(null, "123");
        expectedException.expect(ConstraintViolationException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLIntegrityConstraintViolationException.class,
                                                                "column: NAME"));
        dao.save(contact);
    }

    @Test
    public void phone_ShouldNotBeNull_OrThrowException() {
        Contact contact = new Contact("TestName", null);
        expectedException.expect(ConstraintViolationException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLIntegrityConstraintViolationException.class,
                "column: PHONE"));
        dao.save(contact);
    }

    @Test
    public void note_ShouldNotBeLongerThan50Characters_OrThrowException() {
        expectedException.expect(DataException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLDataException.class, "CONTACTS column: NAME"));

        Contact contact = new Contact(StringUtils.leftPad("", 51, "a"), "123");

        dao.save(contact);
    }

    @Test
    public void phone_ShouldNotBeLongerThan11Characters_OrThrowException() {
        expectedException.expect(DataException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLDataException.class, "CONTACTS column: PHONE"));

        Contact contact = new Contact("Name", "123456789012");

        dao.save(contact);
    }

    @Test
    @DataSet("db_test/dataset/basic/contacts/ContactDbValidationTest.mainDataSet.xml")
    public void name_ShouldBeUnique_OrThrowException() {
        expectedException.expect(ConstraintViolationException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLIntegrityConstraintViolationException.class,
                                                                "unique constraint or index violation: \"table_name_name_uindex\""));
        Contact contact = new Contact("Иван Петров", "123");
        dao.save(contact);
    }

    @Test
    @DataSet("db_test/dataset/basic/contacts/ContactDbValidationTest.mainDataSet.xml")
    public void phone_ShouldBeUnique_OrThrowException() {
        expectedException.expect(ConstraintViolationException.class);
        expectedException.expectCause(new CauseExceptionMatcher(SQLIntegrityConstraintViolationException.class,
                "unique constraint or index violation: \"table_name_phone_uindex\""));
        Contact contact = new Contact("SomeName", "79781234567");
        dao.save(contact);
    }

}
