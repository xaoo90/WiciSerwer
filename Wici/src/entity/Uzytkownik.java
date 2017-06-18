package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;


/**
 * The persistent class for the uzytkownik database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQuery(name="Uzytkownik.findAll", query="SELECT u FROM Uzytkownik u")
public class Uzytkownik implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy=GenerationType.AUTO)
	private int idUzytkownik;

	private String haslo;

	private String login;

	private String mail;

	private String uprawnienie;

	//bi-directional many-to-one association to Miejsce
	@OneToMany(mappedBy="uzytkownik")
	//@XmlTransient 
	private transient List<Miejsce> miejsca;

	public Uzytkownik() {
	}

	public int getIdUzytkownik() {
		return this.idUzytkownik;
	}

	public void setIdUzytkownik(int idUzytkownik) {
		this.idUzytkownik = idUzytkownik;
	}

	public String getHaslo() {
		return this.haslo;
	}

	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUprawnienie() {
		return this.uprawnienie;
	}

	public void setUprawnienie(String uprawnienie) {
		this.uprawnienie = uprawnienie;
	}

	public List<Miejsce> getMiejsca() {
		return this.miejsca;
	}

	public void setMiejsca(List<Miejsce> miejsca) {
		this.miejsca = miejsca;
	}

	public Miejsce addMiejsca(Miejsce miejsca) {
		getMiejsca().add(miejsca);
		miejsca.setUzytkownik(this);

		return miejsca;
	}

	public Miejsce removeMiejsca(Miejsce miejsca) {
		getMiejsca().remove(miejsca);
		miejsca.setUzytkownik(null);

		return miejsca;
	}

	@Override
	public String toString() {
		return idUzytkownik +"";
	}

}