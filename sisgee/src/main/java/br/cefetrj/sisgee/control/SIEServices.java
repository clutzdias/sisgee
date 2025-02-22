package br.cefetrj.sisgee.control;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import br.cefetrj.sisgee.model.dao.CampusDAO;
import br.cefetrj.sisgee.model.dao.CursoDAO;
import br.cefetrj.sisgee.model.dao.PessoaDAO;
import br.cefetrj.sisgee.model.entity.Aluno;
import br.cefetrj.sisgee.model.entity.Campus;
import br.cefetrj.sisgee.model.entity.Curso;
import br.cefetrj.sisgee.model.entity.Pessoa;
import br.cefetrj.sisgee.model.entity.ProfessorOrientador;
import br.cefetrj.sisgee.view.utils.AlunoSIE;

public class SIEServices {

	private static final String URLBASE = "http://my-json-server.typicode.com/paducantuaria/sisgeeJson/";

	/**
	 * Método de busca de aluno a partir da base do SIE, usando webservice
	 * 
	 * @param matricula
	 * @return aluno buscado ou null, caso não exista no SIE
	 */
	public static Aluno buscarAlunoFromSIE(String matricula) {

		String urlAluno = URLBASE + "aluno/?matricula=" + matricula;

		JSONObject jsonObject = getJsonAluno(urlAluno);
		AlunoSIE alunoSie = jsonObject != null ? converterJsonEmAlunoSIE(jsonObject) : null;
		return alunoSie != null ? converterAlunoSIE(alunoSie) : null;
	}
	
	/*Método para converter jsonObject para arraylist de professor*/
	public static List<ProfessorOrientador> converteListaProfessorJson(JSONObject jobject){
		List<ProfessorOrientador> listdata = new ArrayList<>();
		ProfessorOrientador professor = new ProfessorOrientador();
		JSONArray jArray = jobject.getJSONArray("siape");
		if (jArray != null) { 
		   for (int i=0;i<jArray.length();i++){
			professor.setNomeProfessorOrientador(jArray.getString(i));   
		    listdata.add(professor);
		   } 
		}
		return listdata;
	}
	
	/**
	 * Método para remover boiler plate do método principal da busca de aluno no SIE
	 * 
	 * @param urlAluno url de busca de aluno, usando GET
	 * @return JSONObject do aluno buscado ou null caso não exista
	 */
	private static JSONObject getJsonAluno(String urlAluno) {
		try {
			String urlParse = IOUtils.toString(new URL(urlAluno), StandardCharsets.UTF_8);
			JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(urlParse);
			return jsonArray.isEmpty() ? null : (JSONObject) jsonArray.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//TODO Ajustar esse método de acordo com a definição do professor
	//O que fazer com essa lista
	public static List<ProfessorOrientador> getListaProfessorFromSIE() {

		String urlProfessor = URLBASE + "professor";
		List<ProfessorOrientador> listaProfessores = new List<ProfessorOrientador>();
		try {

			String urlParse = IOUtils.toString(new URL(urlProfessor), StandardCharsets.UTF_8);
			JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(urlParse);
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);
			System.out.println(jsonObject);
			listaProfessores = converteListaProfessorJson(jsonObject);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listaProfessores;
	}

	/**
	 * Método que converte um aluno vindo do JSON para o aluno do domínio (Classe
	 * AlunoSIE para Classe Aluno)
	 * 
	 * @param alunoSie
	 * @return Objeto Aluno convertido
	 */
	private static Aluno converterAlunoSIE(AlunoSIE alunoSie) {

		String siglacurso = alunoSie.getSiglaCurso(); 
		Aluno aluno = new Aluno();
		Curso curso = new CursoDAO().buscarByCodigo(siglacurso);
		if (curso != null) {
			aluno.setCurso(curso);
		} else {
			curso.setCodigoCurso(siglacurso);
			Campus campus = new CampusDAO().buscarCampusByNome(alunoSie.getCampus());
			curso.setCampus(campus);
			aluno.setCurso(curso);
			
		}

		Pessoa pessoa = new PessoaDAO().buscarByCpf(alunoSie.getCpf());
		if (pessoa == null) {
			pessoa = new Pessoa();
			pessoa.setNome(alunoSie.getNome());
			pessoa.setCpf(alunoSie.getCpf());
			PessoaServices.incluirPessoa(pessoa);
		}
		aluno.setPessoa(pessoa);
		aluno.setMatricula(alunoSie.getMatricula());
		aluno.setTipoAluno(alunoSie.getTipo());
		AlunoServices.incluirAluno(aluno);
		return aluno;
	}

	/**
	 * Método que converte um JSONObject em um AlunoSIE
	 * 
	 * @param jsonObject
	 * @return
	 */
	private static AlunoSIE converterJsonEmAlunoSIE(JSONObject jsonObject) {
		// Recebendo os dados de aluno provenientes do json
		AlunoSIE alunoSie = new AlunoSIE();

		try {
			String matriculaSie = (String) jsonObject.get("matricula");
			String nome = (String) jsonObject.get("nome");
			String cpf = (String) jsonObject.get("cpf");
			String curso = (String) jsonObject.get("curso");
			String siglaCurso = (String) jsonObject.get("siglaCurso");
			String tipo = (String) jsonObject.get("tipo");
			String campus = (String) jsonObject.get("campus");

			// instanciando o alunoSie com os valores provenientes do json

			alunoSie.setMatricula(matriculaSie.trim());
			alunoSie.setNome(nome.trim());
			alunoSie.setCpf(cpf.trim());
			alunoSie.setCurso(curso.trim());
			alunoSie.setSiglaCurso(siglaCurso.trim());
			alunoSie.setTipo(tipo);
			alunoSie.setCampus(campus.trim());
			return alunoSie;

		} catch (Exception e) {
			return null;
		}
	}
}
