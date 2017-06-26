package com.br.backup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.conf.Configuration;

@XmlRootElement(name="Backup")
public class Backup{
	private ArrayList<Cliente> listaUsuario;
	
	public Backup(){
		this.listaUsuario = new ArrayList<>();
	}
	

	public boolean conectar(String nome, String senha){
		if(this.buscaCliente(nome, senha) != null){
			return true;
		}
		return false;
	}

	public boolean registrarUsuario(String nome, String senha){
		if(nome != "" && senha != ""){
			this.carregaLista();
			this.listaUsuario.add(new Cliente(nome, senha, System.getProperty("java.io.tmpdir")+"/backups/"+nome));
			this.salvarLista();
			return true;
		}
		return false;
	}

	public void removeCliente(Cliente cliente){
		this.carregaLista();
		this.listaUsuario.remove(cliente);
		this.salvarLista();
	}

	public Cliente buscaCliente(String nome, String senha){

		if(this.carregaLista()){
			for (int i = 0; i < this.listaUsuario.size(); i++) {
				this.listaUsuario.get(i).conectar(nome, senha);

				if(this.listaUsuario.get(i).isConnectado()){
					return this.listaUsuario.get(i);
				}
			}
		}
		return null;
	}

	public boolean salvarLista(){
		FileOutputStream fout;
		try {

			fout = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath());
			ObjectOutputStream object = new ObjectOutputStream(fout);
			object.writeObject(this.listaUsuario);
			fout.close();
			object.close();

			return true;

		} catch (IOException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean carregaLista(){
		try {
			if(new File(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath()).exists()){
				FileInputStream fin = new FileInputStream(new File(System.getProperty("java.io.tmpdir")+"/usuarios.data").getAbsolutePath());
				ObjectInputStream ois = new ObjectInputStream(fin);
				this.listaUsuario =  (ArrayList<Cliente>) ois.readObject();
				fin.close();
				ois.close();
				return true;
			}else{
				return false;
			}
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}

	public boolean isConnectado(String nome, String senha){
		return buscaCliente(nome, senha).isConnectado();
	}

	public void acao(String acao, String caminhoArq, String nome, String senha){

		Cliente clienteAtual = this.buscaCliente(nome, senha);

		if(clienteAtual != null && this.isConnectado(nome, senha)){
			HDFS acoes = new HDFS();

			Configuration conf = new Configuration();
			conf.set("fs.default.name", "hdfs://" + acao + ":" + caminhoArq);


			try {
				if (acao.equals("add")) {
					acoes.addFile(caminhoArq, clienteAtual.getCaminhoDir(), conf);

				} else if (acao.equals("read")) {
					acoes.readFile(caminhoArq, conf);

				} else if (acao.equals("delete")) {
					acoes.deleteFile(caminhoArq, conf);

				} else if (acao.equals("mkdir")) {
					acoes.mkdir(caminhoArq, conf);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
