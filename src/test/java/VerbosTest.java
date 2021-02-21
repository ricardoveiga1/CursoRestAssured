//import statico serve para tirar notações docódigo, deixar mais limpo
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import io.restassured.http.ContentType;

import org.junit.Test;


public class VerbosTest {
	
	@Test
	public void deveSalvarUsuario(){
		given()
			 .log().all()
			 .contentType("application/json")
			 .body(" {\n" + 
			 		"        \"name\": \"Rik\",\n" + 
			 		"        \"age\": 30,\n" + 
			 		"        \"salary\": 12000.00\n" + 
			 		"    }")
		.when()
			 .post("http://restapi.wcaquino.me/users")
		.then()
			 .log().all()
			 .statusCode(201)
			 .body("id", is(notNullValue()))
			 .body("name", is("Rik"))
			 .body("age", is(30))			 
		;
		
	}
	
	@Test
	public void deveSalvarUsuarioUsandoMap() {
		
		Map<String, Object> params = new HashMap<String, Object>();  //mapeamento para serializar, precisa da lib gson 
		params.put("name", "Usuario via map");
		params.put("age", 25);
		params.put("salary", 1500.5678);//adicionado para testar
		
		given()
			.log().all()
			.contentType("application/json")
			.body(params) //definido acima, para evitar de mandar string como o método acima
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via map"))
			.body("age", is(25))
		;
	}
	
	
	
	//MELHOR PRÁTICA ESTÁ NESTE MÉTODO
	@Test
	public void deveSalvarUsuarioUsandoObjeto() {
		
		User user = new User("Usuario via objeto", 35, 500.00);  //Valores que vem do construtor, se quiser add valores, devo incluir no construtor
		
		given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via objeto"))
			.body("age", is(35))
			.body("salary", is(500))
		;
	}
	
	
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		
		User user = new User("Usuario deserializado", 35, 500.00);
		
		User usuarioInserido = given()  // usuário inserido é o retorno dessa expressão toda
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class) //SE ATENTAR ESTE PONTO, A VALIDAÇÃO É FEITA NA CLASSE
		;
		
		System.out.println(usuarioInserido);
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(35));
		Assert.assertThat(usuarioInserido.getSalary(), is(500.00));
	}
	
	
	
	
	@Test
	public void deveSalvarUsuarioViaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40, 500.00);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
		;
	}

	@Test
	public void deveDeserializarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 40, 6500.00);
		
		User usuarioInserido = given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		System.out.println(usuarioInserido); //não deve ter sysout, mas pode por durante a construção do teste ou debug
		Assert.assertThat(usuarioInserido.getId(), is(notNullValue()));
		Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
		//Assert.assertThat(usuarioInserido.getAge(), is(40));
		Assert.assertThat(usuarioInserido.getAge(), equalTo(new Integer(40)));//adicionado por mim
		//Assert.assertThat(usuarioInserido.getSalary(), is(notNullValue()));//poderia colocar o valor que foi enviado no body
		Assert.assertThat(usuarioInserido.getSalary(), is(new Double(6500.00))); //adicionada por mim
	}
	
	
	
	
	
	
	
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"age\": 50 }")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user>"
					+ "<name>Rik</name>"
					+ "<age>30</age>"
				 +"</user>")  // Esse mais aparece automaticamente quando quebro a linha, o entendimento ficar mais fácil
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue())) //ponho como not null value quando não sei o valor que irá retornar
			.body("user.name", is("Rik"))
			.body("user.age", is("30"))
		;
	}
	
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\","
					+ " \"age\": 80 }")
		.when()
			.put("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void devoCustomizarURL() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\", \"age\": 80 }")
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")  //definindo do modo mais simples
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void devoCustomizarURLParte2() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\", \"age\": 80 }")
			.pathParam("entidade", "users")
			.pathParam("userId", 1)
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}") //foram definidos no pathParam
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}

	@Test
	public void naoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	
	
	
	
		
		


		

}
