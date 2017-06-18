package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import entity.Wydarzenie;

import java.util.Base64;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the kategoria database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQueries({
@NamedQuery(name="Kategoria.findAll", query="SELECT k FROM Kategoria k"),
@NamedQuery(name="Kategoria.noweKategorie", query="SELECT k FROM Kategoria k WHERE k.dataDodania > :data")})
public class Kategoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idKategoria;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDodania;

	private String nazwa;

	@Lob
	private byte[] zdjecie;

	//bi-directional many-to-one association to Wydarzenie
	@OneToMany(mappedBy="kategoria")
	private transient List<Wydarzenie> wydarzenia;

	public Kategoria() {
	}

	public int getIdKategoria() {
		return this.idKategoria;
	}

	public void setIdKategoria(int idKategoria) {
		this.idKategoria = idKategoria;
	}

	public Date gtetDataDodania() {
		return this.dataDodania;
	}

	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}

	public String getNazwa() {
		return this.nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public byte[] gtetZdjecie() {
        
		if(this.zdjecie == null)
			return null;
		return Base64.getEncoder().encode(this.zdjecie);
	}
	
	public String getZdjecie() {
		if(this.zdjecie == null)
			return null;
		return Base64.getEncoder().encodeToString(this.zdjecie);
	}

	public void setZdjecie(byte[] zdjecie) {
		this.zdjecie = zdjecie;
	}

	public List<Wydarzenie> gtetWydarzenia() {
		return this.wydarzenia;
	}

	public void setWydarzenia(List<Wydarzenie> wydarzenia) {
		this.wydarzenia = wydarzenia;
	}

	public Wydarzenie addWydarzenia(Wydarzenie wydarzenia) {
		gtetWydarzenia().add(wydarzenia);
		wydarzenia.setKategorie(this);

		return wydarzenia;
	}

	public Wydarzenie removeWydarzenia(Wydarzenie wydarzenia) {
		gtetWydarzenia().remove(wydarzenia);
		wydarzenia.setKategorie(null);

		return wydarzenia;
	}

	@Override
	public String toString() {
		return "Kategoria [idKategoria=" + idKategoria + ", nazwa=" + nazwa
				+ "]\n";
	}
	
	

}