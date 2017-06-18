package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the miejsce database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQueries({
@NamedQuery(name="Miejsce.findAll", query="SELECT m FROM Miejsce m"),
@NamedQuery(name="Miejsce.noweMiejsca", query="SELECT m FROM Miejsce m WHERE m.dataDodania > :data")})
public class Miejsce implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idMiejsce;

	private String adres;
	
	private float cord1;

	private float cord2;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDodania;

	private String mail;

	private String nazwa;

	private String telefon;

	private String www;

	private byte[] zdjecie;

	//bi-directional many-to-one association to Uzytkownik
	@ManyToOne
	@JoinColumn(name="uzytkownik")
	@JsonIgnore
	private Uzytkownik uzytkownik;

	//bi-directional many-to-one association to Wydarzenie
	@OneToMany(mappedBy="miejsce")
	private transient List<Wydarzenie> wydarzenia;

	public Miejsce() {
	}

	public int getIdMiejsce() {
		return this.idMiejsce;
	}

	public void setIdMiejsce(int idMiejsce) {
		this.idMiejsce = idMiejsce;
	}

	public String getAdres() {
		return this.adres;
	}

	public void setAdres(String adres) {
		this.adres = adres;
	}

	public Date gtetDataDodania() {
		return this.dataDodania;
	}

	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}

	public String gtetMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public float getCord1() {
		return this.cord1;
	}

	public void setCord1(float cord1) {
		this.cord1 = cord1;
	}

	public float getCord2() {
		return this.cord2;
	}

	public void setCord2(float cord2) {
		this.cord2 = cord2;
	}

	public String getNazwa() {
		return this.nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public String getTelefon() {
		return this.telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getWww() {
		return this.www;
	}

	public void setWww(String www) {
		this.www = www;
	}

	public String getZdjecie() {
		if(this.zdjecie == null)
			return null;
		return Base64.getEncoder().encodeToString(this.zdjecie);
	}

	public void setZdjecie(byte[] zdjecie) {
		this.zdjecie = zdjecie;
	}


	public Uzytkownik gtetUzytkownik() {
		return this.uzytkownik;
	}

	public void setUzytkownik(Uzytkownik uzytkownik) {
		this.uzytkownik = uzytkownik;
	}

	public List<Wydarzenie> gtetWydarzenia() {
		return this.wydarzenia;
	}

	public void setWydarzenia(List<Wydarzenie> wydarzenia) {
		this.wydarzenia = wydarzenia;
	}

	public Wydarzenie addWydarzenia(Wydarzenie wydarzenia) {
		gtetWydarzenia().add(wydarzenia);
		wydarzenia.setMiejsce(this);

		return wydarzenia;
	}

	public Wydarzenie removeWydarzenia(Wydarzenie wydarzenia) {
		gtetWydarzenia().remove(wydarzenia);
		wydarzenia.setMiejsce(null);

		return wydarzenia;
	}

	@Override
	public String toString() {
		return "Miejsce [idMiejsce=" + idMiejsce + ", adres=" + adres
				+ ", mail=" + mail + ", nazwa=" + nazwa + ", telefon="
				+ telefon + ", www=" + www + ", zdjecie="
				+ Arrays.toString(zdjecie) + "]\n";
	}

}