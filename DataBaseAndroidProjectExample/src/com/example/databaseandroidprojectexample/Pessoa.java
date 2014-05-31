package com.example.databaseandroidprojectexample;

import java.util.Date;
import java.util.HashMap;

import com.lvc.database.EntitiePersistable;
import com.lvc.database.annotation.PrimaryKey;
import com.lvc.database.annotation.SaveAsBytes;

public class Pessoa implements EntitiePersistable {

	private static final long serialVersionUID = -5142412216578484099L;

	@PrimaryKey(autoIncrement = true)
	private Long id;
	private String nome;
	
	@SaveAsBytes
	private HashMap<String, String> hashMapUm;
	
	@SaveAsBytes
	private HashMap<String, String> hashMapDois;
	private byte[] photo;
	private byte idade;
	private boolean heterosexual;  
	private Date dataNascimento;

	public Pessoa() {
	}

	

	public Pessoa(String nome, HashMap<String, String> hashMapUm, HashMap<String, String> hashMapDois, byte[] photo, byte idade, boolean heterosexual) {
		super();
		this.nome = nome;
		this.hashMapUm = hashMapUm;
		this.hashMapDois = hashMapDois;
		this.photo = photo;
		this.idade = idade;
		this.heterosexual = heterosexual;
	}



	public Pessoa(String nome, HashMap<String, String> hashMapUm) {
		super();
		this.nome = nome;
		this.hashMapUm = hashMapUm;
	}
	
	
	public Date getDataNascimento() {
		return dataNascimento;
	}
	
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
		Pessoa other = (Pessoa) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	
	
  
}