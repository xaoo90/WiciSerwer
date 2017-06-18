package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;


/**
 * The persistent class for the zdjecie database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQueries({
@NamedQuery(name="Zdjecie.findAll", query="SELECT z FROM Zdjecie z"),
@NamedQuery(name="Zdjecie.noweZdjecia", query="SELECT z FROM Zdjecie z WHERE z.wydarzenie = :id")})
public class Zdjecie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idZdjecie;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDodania;

	private byte[] zdjecie;

	//bi-directional many-to-one association to Wydarzenie
	@ManyToOne
	@JoinColumn(name="wydarzenie")
	private Wydarzenie wydarzenie;

	public Zdjecie() {
	}

	public int getIdZdjecie() {
		return this.idZdjecie;
	}

	public void setIdZdjecie(int idZdjecie) {
		this.idZdjecie = idZdjecie;
	}

	public Date gtetDataDodania() {
		return this.dataDodania;
	}

	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}

	public String getZdjecie() {
		if(this.zdjecie == null)
			return null;
		return Base64.getEncoder().encodeToString(this.zdjecie);
	}

	public void setZdjecie(byte[] zdjecie) {
		this.zdjecie = zdjecie;
	}

	public Wydarzenie gtetWydarzenie() {
		return this.wydarzenie;
	}
	public Integer getWydarzenie() {
		return this.wydarzenie.getIdWydarzenie();
	}

	public void setWydarzenie(Wydarzenie wydarzenie) {
		this.wydarzenie = wydarzenie;
	}

	@Override
	public String toString() {
		return "Zdjecie [idZdjecie=" + idZdjecie + ", zdjecie="
				+ Arrays.toString(zdjecie) + ", wydarzenie=" + wydarzenie.getIdWydarzenie() + "]\n";
	}
	
}