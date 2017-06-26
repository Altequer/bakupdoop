package com.br.backup;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Cliente")
@SuppressWarnings("serial")
public class Cliente implements Serializable {
	private String nome, senha, caminhoDir;
	private boolean conectado = false;

	public Cliente(String nome, String senha, String caminho){
		this.setNome(nome);
		this.setSenha(senha);
		this.setCaminhoDir(caminho);
	}
	
	public String getCaminhoDir() {
		return this.caminhoDir;
	}

	public void setCaminhoDir(String caminhoDir) {
		this.caminhoDir = caminhoDir;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void conectar(String nome, String senha){
		if(this.getNome().equalsIgnoreCase(nome) && this.getSenha().equalsIgnoreCase(senha)){
			this.conectado = true;
		}else{
			this.conectado = false;
		}
	}

	public boolean isConnectado(){
		return conectado;
	}
}
