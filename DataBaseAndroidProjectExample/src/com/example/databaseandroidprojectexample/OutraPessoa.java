package com.example.databaseandroidprojectexample;

import java.util.HashMap;

import com.lvc.database.EntitiePersistable;
import com.lvc.database.annotation.PrimaryKey;
import com.lvc.database.annotation.SaveAsString;

public class OutraPessoa implements EntitiePersistable {

	private static final long serialVersionUID = -5142412216578484099L;

	@PrimaryKey(autoIncrement = true)
	private Integer id;
	private String nome;
	
	@SaveAsString
	private HashMap<String, String> hashMapUm;
	private byte[] photo;
	private byte idade;
	private boolean heterosexual; 
	@SaveAsString
	private HashMap<String, String> hashMapDois;

	

	public OutraPessoa() {
	}


	public OutraPessoa(String nome, HashMap<String, String> hashMapUm) {
		super();
		this.nome = nome;
		this.hashMapUm = hashMapUm;
	}
	
	public HashMap<String, String> getHashMapDois() {
		return hashMapDois;
	}
	public void setHashMapDois(HashMap<String, String> hashMapDois) {
		this.hashMapDois = hashMapDois;
	}
	
	public byte getIdade() {
		return idade;
	}
	
	public void setIdade(byte idade) {
		this.idade = idade;
	}
	
	public boolean isHeterosexual() {
		return heterosexual;
	}
	
	public void setHeterosexual(boolean heterosexual) {
		this.heterosexual = heterosexual;
	}


	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public HashMap<String, String> getHashMapUm() {
		return hashMapUm;
	}

	public void setHashMapUm(HashMap<String, String> hashMapUm) {
		this.hashMapUm = hashMapUm;
	}
 
	@Override
	public String toString() {
		return nome;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutraPessoa other = (OutraPessoa) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

 
}
