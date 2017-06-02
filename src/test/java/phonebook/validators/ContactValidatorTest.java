package phonebook.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.Errors;
import phonebook.entities.Contact;
import phonebook.services.interfaces.ContactService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext_ForTests.xml", "classpath:dispatcher-servlet.xml"})
public class ContactValidatorTest extends AbstractJUnit4SpringContextTests {

    @Mock ContactService service;
    @Mock Contact contact;
    @Mock Contact duplicateNameContact;
    @Mock Contact duplicatePhoneContact;
    @Mock Errors errors;

    @InjectMocks
    @Resource(name = "ContactValidator")
    ContactValidator contactValidator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(contact.getName()).thenReturn("TestName");
        when(contact.getPhone()).thenReturn("123");
        when(contact.getId()).thenReturn(1L);

        when(duplicateNameContact.getName()).thenReturn("TestName");
        when(duplicateNameContact.getPhone()).thenReturn("222");
        when(duplicatePhoneContact.getName()).thenReturn("SomeName");
        when(duplicatePhoneContact.getPhone()).thenReturn("123");
    }


    @Test
    public void validate_NoDuplicateFieldsInDatabase_ShouldNotCauseErrors() {
        when(service.searchByNameOrPhone(anyString(), anyString())).thenReturn(new ArrayList<>());

        contactValidator.validate(contact, errors);

        verify(service, times(1)).searchByNameOrPhone("TestName", "123");
        verifyZeroInteractions(errors);
    }

    @Test
    public void validate_FindDuplicatesByNameAndPhone_DifferentId_ShouldCauseErrors() {
        when(service.searchByNameOrPhone(anyString(), anyString())).thenReturn(Arrays.asList(duplicateNameContact, duplicatePhoneContact));
        when(duplicateNameContact.getId()).thenReturn(2L);
        when(duplicatePhoneContact.getId()).thenReturn(3L);

        contactValidator.validate(contact, errors);

        verify(errors, times(1)).rejectValue("name", "contact.name.notUnique");
        verify(errors, times(1)).rejectValue("phone", "contact.phone.notUnique");
    }

    @Test
    public void validate_FindDuplicatesByNameAndPhone_SameId_ShouldNotCauseErrors() {
        when(service.searchByNameOrPhone(anyString(), anyString())).thenReturn(Arrays.asList(duplicateNameContact, duplicatePhoneContact));
        when(duplicateNameContact.getId()).thenReturn(1L);
        when(duplicatePhoneContact.getId()).thenReturn(1L);

        contactValidator.validate(contact, errors);

        verifyZeroInteractions(errors);
    }

    @Test
    public void supports_UnsupportedClass_ShouldReturnFalse() throws Exception {
        assertFalse(contactValidator.supports(Long.class));
    }

    @Test
    public void supports_null_ShouldReturnFalse() throws Exception {
        assertFalse(contactValidator.supports(null));
    }

    @Test
    public void supports_SupportedClass_ShouldReturnTrue() throws Exception {
        assertTrue(contactValidator.supports(Contact.class));
    }

}