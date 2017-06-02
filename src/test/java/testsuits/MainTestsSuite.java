package testsuits;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.unitils.database.DatabaseUnitils;
import phonebook.dao.ContactDbBasicTest;
import phonebook.dao.ContactDbValidationTest;
import phonebook.validators.ContactValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ContactDbBasicTest.class,
        ContactDbValidationTest.class,
        ContactValidatorTest.class
})
public class MainTestsSuite {

    @BeforeClass
    public static void beforeClass(){
        DatabaseUnitils.updateDatabase();
        DatabaseUnitils.generateDatasetDefinition();
    }
}