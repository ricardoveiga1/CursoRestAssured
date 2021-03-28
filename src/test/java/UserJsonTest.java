import static org.hamcrest.Matchers.containsString;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class UserJsonTest {
	
	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18)) //maior que
			.body("age", is(30))
		
		;
		
	}
	
	//esta forma é usando junit puro, sem passar pelo hamcrast do restAssured dentro do body
	// o json path é do restassured
	
	@Test
	public void deveVerificarPrimeiroNivelutrasFormas() {
		
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		System.out.println(response.path("id"));
		
		//path
		Assert.assertEquals(new Integer(1), response.path("id"));
		Assert.assertEquals(new Integer(1), response.path("%s", "id"));
		
		//jsonpath
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));
		
		//from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);		
		
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("id", is(2))
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos")) //verificando o segundo nivel user 2 tem endereco e rua
		
		;
	}
	
	@Test
	public void deveVerificarUmaListal() {
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("id", is(3))
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
		
		;
	}
	
	@Test
	public void deveRetornarUsuarioInexistente() {
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		.then()
		.statusCode(404)
		.body("error", is("Usuário inexistente"))
		;
	}
	
	@Test
	public void deveVerificarListaRaiz() { // o retorno é uma lista, basta olhar no browser, considerando que é um array
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3)) // o $ é apenas uma convencao
			.body("", hasSize(3))
			.body("name", hasItems("João da Silva","Maria Joaquina","Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
		  //  .body("salary", contains(1234.5678f, 2500, null))  //se atentar quando o valor for float poe o f apos o numero
		;
	}
	
	@Test	
	public void devoFazerVerificacoesAvancadas() {
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2)) // todos itens que tem idae menos que 25, representado pelo it, sabemos que deve ser igual a 2
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina")) //preciso usar has item porque o findAll retorna uma lista
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) //primeiro elemento da lista
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))  //ultimo elementp-1
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))  //find tras apenas um elemento
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			.body("age.collect{it * 2}", hasItems(60, 50, 40)) // multiplica idade por 2
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f)) //menor salario
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
			
		;
	}
	
	@Test
	public void devoUnirJsonPathComJAVA() {
		ArrayList<String> names =    //lista de string
			given()  //serve para por a autorizacao, basic auth
			.when()
				.get("https://restapi.wcaquino.me/users")
			.then()
				.statusCode(200)
				.extract().path("name.findAll{it.startsWith('Maria')}") //fazendo uma query que inicia com maria
			;
		Assert.assertEquals(1, names.size()); //size desta lista
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
		Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}
	
	

}
