package phonebook.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Entity
@Table(name="contacts")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Contact {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@NotNull(message = "{contact.name.isEmpty}")
	@Size(min = 1, max = 50, message = "{contact.name.length}")
	@Column(name="name", unique = true)
	private String name;

	@NotNull(message = "{contact.phone.isEmpty}")
	@Pattern(regexp = "\\d+", message = "{contact.phone.invalid}")
	@Size(min = 1, max = 11, message = "{contact.phone.length}")
	@Column(name="phone", unique = true)
	private String phone;


	public Contact() {}

	public Contact(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public String getPhoneFormatted(){
		if (phone.length() == 11){
			return new StringBuilder(phone)
					.insert(9, '-')
					.insert(7, '-')
					.insert(4, ") ")
					.insert(1, " (")
					.insert(0, '+')
					.toString();
		}
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return name + " : " + phone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Contact)) return false;

		Contact contact = (Contact) o;

		if (id != null ? !id.equals(contact.id) : contact.id != null) return false;
		if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
		return phone != null ? phone.equals(contact.phone) : contact.phone == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (phone != null ? phone.hashCode() : 0);
		return result;
	}
}
