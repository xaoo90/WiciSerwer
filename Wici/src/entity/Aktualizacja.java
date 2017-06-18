package entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Date;


/**
 * The persistent class for the aktualizacja database table.
 * 
 */
@Entity
@XmlRootElement
@NamedQueries({
@NamedQuery(name="Aktualizacja.findAll", query="SELECT a FROM Aktualizacja a"),
@NamedQuery(name="Aktualizacja.ostatniaAktualizacja", query="SELECT max(a.idAktualizacja) FROM Aktualizacja a")})
public class Aktualizacja implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idAktualizacja;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dataDodania;

	public Aktualizacja() {
	}

	public int getIdAktualizacja() {
		return this.idAktualizacja;
	}

	public void setIdAktualizacja(int idAktualizacja) {
		this.idAktualizacja = idAktualizacja;
	}

	public Date gtetDataDodania() {
		return this.dataDodania;
	}

	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}

	@Override
	public String toString() {
		return "Aktualizacja [idAktualizacja=" + idAktualizacja
				+ ", dataDodania=" + dataDodania + "]";
	}

}