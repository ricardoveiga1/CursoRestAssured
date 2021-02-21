import org.junit.Test;

import static io.restassured.RestAssured.get;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;

import org.hamcrest.Matchers;
//import org.hamcrest.Matchers;
import org.junit.Assert;
// import estático para testar
//import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;


public class OlaMundoTest {
	
	@Test
	public void test01OlaMundo() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertTrue("O status code deveria ser 200",response.statusCode() == 200);
		Assert.assertTrue("O status code deveria ser 200",response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
	}
	
	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
		//uma linha para mesma coisa que fiz acima
		get("http://restapi.wcaquino.me/ola").then().statusCode(200);
		
		//terceira forma de fazer e mais organizada segundo  o instrutor
		given() //pre-condicoes, header, contentetype
		.when()  //quando
			.get("http://restapi.wcaquino.me/ola")
		.then()  //Assertivas
//			.assertThat()
			.statusCode(200);	
		
	}
	
	@Test
	public void devoConhecerMatcherHamcrest() {
		Assert.assertThat("Ricardo", Matchers.is("Ricardo"));  //compara igualdade, acertiva
		Assert.assertThat(128, Matchers.is(128));
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		Assert.assertThat(128d, Matchers.greaterThan(120d));
		Assert.assertThat(128d, Matchers.lessThan(130d));
		
		//elementos do contais devem ser na mesma ordem da lista e todos itens para nao dar erro
		//para fazer import estatico do assert, clique no botao direito, source, add import(ou crtl+shift+M)
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		Assert.assertThat(impares, Matchers.hasSize(5));
		Assert.assertThat(impares, Matchers.contains(1,3,5,7,9));
		Assert.assertThat(impares, Matchers.containsInAnyOrder(1,3,5,7,9));
		Assert.assertThat(impares, Matchers.hasItem(1));
		
		//para ver que não precisa do Assert e Matchers por causa do import static
		assertThat(impares, hasItems(1,5));
		
		assertThat("Ricardo", is(not("Ricardoo")));
		assertThat("Ricardo", not("Rick"));
		assertThat("Ricardo", anyOf(is("Rick"), is("Ricardo")));
		assertThat("Ricardo", allOf(startsWith("Ric"), endsWith("do"), containsString("car")));		
		
	}
	
	@Test
	public void devoValidaeBody() {
		
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())));	
		
	}
		
}
