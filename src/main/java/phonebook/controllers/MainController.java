package phonebook.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import phonebook.entities.Contact;
import phonebook.services.interfaces.ContactService;
import phonebook.validators.ContactValidator;

import javax.validation.Valid;
import java.util.List;


@Controller
public class MainController {

	Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired Validator validator;
	@Autowired ContactValidator contactValidator;
	@Autowired ContactService contactService;

	private final String editErrors = "org.springframework.validation.BindingResult.contactToEdit";

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ExceptionHandler(RuntimeException.class)
	public String exceptionHandler(Model model) {
		model.addAttribute("message", "Что-то пошло не так :(");
		return "error";
	}

	@RequestMapping(value = "error")
	public String errorHandler(Model model) {
		model.addAttribute("message", "Что-то пошло не так :(");
		return "error";
	}

	// Переход на основную страницу
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String pageIndex(@RequestParam(name = "search", required = false) String searchName,
							Model model){
		logger.debug("inside pageIndex()");

		List<Contact> contacts = contactService.getAll();
		if (!model.containsAttribute("newContact")) {
			model.addAttribute("newContact", new Contact());
		}

		if (searchName == null){
			model.addAttribute("contacts", contacts);
		} else {
			model.addAttribute("contacts", contactService.searchByName(searchName));
			model.addAttribute("search", searchName);
		}

		return "index";
	}

	// Добавление контакта
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addContact(@Valid @ModelAttribute Contact contact, Errors errors,
							 @RequestParam(name = "search", required = false) String searchName,
							 RedirectAttributes redirectAttributes){
		logger.debug("inside addContact()");

		processSearch(searchName, redirectAttributes);

		if (processNewContactErrors(contact, errors, redirectAttributes)) return "redirect:/";
		contactValidator.validate(contact, errors);
		if (processNewContactErrors(contact, errors, redirectAttributes)) return "redirect:/";

		contactService.save(contact);

		return "redirect:/";
	}

	// Удаление контакта
	@RequestMapping(value = "/delete", method = RequestMethod.GET, params = {"id"})
	public String removeContact(@RequestParam("id") Long id,
								@RequestParam(name = "search", required = false) String searchName,
								RedirectAttributes redirectAttributes){
		logger.debug("inside removeContact()");
		processSearch(searchName, redirectAttributes);
		contactService.deleteById(id);
		return "redirect:/";
	}


	// Переход на страницу редактирования контакта
	@RequestMapping(value = "/edit", method = RequestMethod.GET, params = {"id"})
	public String pageEdit(@RequestParam("id") Long id,
						   @RequestParam(name = "search", required = false) String searchName,
						   RedirectAttributes redirectAttributes, Model model){
		logger.debug("inside pageEdit()");
		processSearch(searchName, redirectAttributes);

		if (!model.containsAttribute("contactToEdit")) {
			redirectAttributes.addFlashAttribute("contactToEdit", contactService.getById(id));
		}else {
			redirectAttributes.addFlashAttribute("contactToEdit", model.asMap().get("contactToEdit"));
		}

		if (model.containsAttribute(editErrors)){
			redirectAttributes.addFlashAttribute(editErrors, model.asMap().get(editErrors));
		}

		redirectAttributes.addFlashAttribute("action", "edit");
		return "redirect:/";
	}


	// Обновление контакта
	@RequestMapping(value = "/update", method = RequestMethod.POST, params = {"id"})
	public String updateContact(@RequestParam("id") Long id,
								@RequestParam(name = "search", required = false) String searchName,
								@Valid @ModelAttribute Contact contact, Errors errors,
							 	RedirectAttributes redirectAttributes){
		logger.debug("inside updateContact()");
		processSearch(searchName, redirectAttributes);

		if (processEditContactErrors(contact, errors, redirectAttributes)) return "redirect:/edit?id=" + id;
		contactValidator.validate(contact, errors);
		if (processEditContactErrors(contact, errors, redirectAttributes)) return "redirect:/edit?id=" + id;

		contactService.update(contact);

		return "redirect:/";
	}

	// Поиск контактов
	@RequestMapping(value = "/", method = RequestMethod.POST, params = {"search"})
	public String searchContacts(@RequestParam("search") String searchName,
								 RedirectAttributes redirectAttributes){
		logger.debug("inside searchContacts()");

		processSearch(searchName, redirectAttributes);

		return "redirect:/";
	}


	private void processSearch(String searchName, RedirectAttributes redirectAttributes){
		if (searchName != null){
			redirectAttributes.addAttribute("search", searchName);
		}
	}

	private boolean processNewContactErrors(Contact contact, Errors errors, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()){
			redirectAttributes.addFlashAttribute("newContact", contact);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newContact", errors);
			return true;
		}
		return false;
	}

	private boolean processEditContactErrors(Contact contact, Errors errors, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()){
			redirectAttributes.addFlashAttribute("contactToEdit", contact);
			redirectAttributes.addFlashAttribute(editErrors, errors);
			return true;
		}
		return false;
	}
}