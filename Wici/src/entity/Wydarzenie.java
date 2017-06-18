package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import entity.Kategoria;
import entity.Miejsce;
import entity.Zdjecie;

import java.util.Base64;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the wydarzenie database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQueries({
@NamedQuery(name="Wydarzenie.findAll", query="SELECT w FROM Wydarzenie w"),
@NamedQuery(name="Wydarzenie.noweWydarzenia", query="SELECT w FROM Wydarzenie w WHERE w.dataDodania > :data AND w.data > :sysDate"),
@NamedQuery(name="Wydarzenie.przyszleWydarzenia", query="SELECT w FROM Wydarzenie w WHERE w.data > :sysDate"),
@NamedQuery(name="Wydarzenie.noweWydarzeniaId", query="SELECT w.idWydarzenie FROM Wydarzenie w WHERE w.dataDodania > :data AND w.data > :sysDate"),
@NamedQuery(name="Wydarzenie.przyszleWydarzeniaId", query="SELECT w.idWydarzenie FROM Wydarzenie w WHERE w.data > :sysDate")})
public class Wydarzenie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idWydarzenie;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDodania;

	private String opis;

	private String podtytul;

	private String tytul;

	@Lob
	private byte[] zdjecie;

	//bi-directional many-to-one association to Miejsce
	@ManyToOne
	@JoinColumn(name="miejsce")
	private Miejsce miejsce;

	//bi-directional many-to-one association to Zdjecie
	@OneToMany(mappedBy="wydarzenie")
	private transient List<Zdjecie> zdjecia;

	//bi-directional many-to-one association to Kategoria
	@ManyToOne
	@JoinColumn(name="kategoria")
	private Kategoria kategoria;

	public Wydarzenie() {
	}

	public int getIdWydarzenie() {
		return this.idWydarzenie;
	}

	public void setIdWydarzenie(int idWydarzenie) {
		this.idWydarzenie = idWydarzenie;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date gtetDataDodania() {
		return this.dataDodania;
	}

	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}

	public String getOpis() {
		return this.opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public String gtetPodtytul() {
		return this.podtytul;
	}

	public void setPodtytul(String podtytul) {
		this.podtytul = podtytul;
	}

	public String getTytul() {
		return this.tytul;
	}

	public void setTytul(String tytul) {
		this.tytul = tytul;
	}

	public String getZdjecie() {
		if(this.zdjecie == null)
			return null;
		return Base64.getEncoder().encodeToString(this.zdjecie);
	}

	public void setZdjecie(byte[] zdjecie) {
		this.zdjecie = zdjecie;
	}

	public Miejsce gtetMiejsce() {
		return this.miejsce;
	}
	public Integer getMiejsce() {
		return this.miejsce.getIdMiejsce();
	}

	public void setMiejsce(Miejsce miejsce) {
		this.miejsce = miejsce;
	}

	public List<Zdjecie> gtetZdjecia() {
		return this.zdjecia;
	}

	public void setZdjecia(List<Zdjecie> zdjecia) {
		this.zdjecia = zdjecia;
	}

	public Zdjecie addZdjecia(Zdjecie zdjecia) {
		gtetZdjecia().add(zdjecia);
		zdjecia.setWydarzenie(this);

		return zdjecia;
	}

	public Zdjecie removeZdjecia(Zdjecie zdjecia) {
		gtetZdjecia().remove(zdjecia);
		zdjecia.setWydarzenie(null);

		return zdjecia;
	}

	public Kategoria gtetKategorie() {
		return this.kategoria;
	}
	
	public Integer getKategorie() {
		return this.kategoria.getIdKategoria();
	}

	public void setKategorie(Kategoria kategoria) {
		this.kategoria = kategoria;
	}

//	@Override
//	public String toString() {
//		return "Wydarzenie [idWydarzenie=" + idWydarzenie + ", data=" + data
//				+ ", opis=" + opis + ", podtytul=" + podtytul + ", tytul="
//				+ tytul + ", zdjecie=" + Arrays.toString(zdjecie)
//				+ ", kategoria=" + kategoria + ", miejsce=" + miejsce.getIdMiejsce() + "]\n";
//	}

}