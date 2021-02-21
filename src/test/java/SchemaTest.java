import static io.restassured.RestAssured.given;

//import org.junit.Assert;
import org.junit.Test;

import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;

import org.xml.sax.SAXParseException;


//import static  io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath ;
//import static  org.hamcrest.MatcherAssert.assertThat ;



public class SchemaTest {
	
	

		@Test
		public void deveValidarSchemaXML() {
			given()
				.log().all()
			.when()
				.get("https://restapi.wcaquino.me/usersXML")
			.then()
				.log().all()
				.statusCode(200)
				.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
			;
		}
		
		@Test(expected=SAXParseException.class)     // é esperando q uma exceção é esperada mesmo, necessita informar para aceitar 
		public void naoDeveValidarSchemaXMLInvalido() {
			given()
				.log().all()
			.when()
				.get("https://restapi.wcaquino.me/invalidUsersXML")
			.then()
				.log().all()
				.statusCode(200)
				.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
			;
		}
		
		
		
		//ESQUEMA JSON
		@Test
		public void deveValidarSchemaJson() {
			given()
				.log().all()
			.when()
				.get("https://restapi.wcaquino.me/users")
			.then()
				.log().all()
				.statusCode(200)
				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
			;
			
			//Assert.assertThat(json, matchesJsonSchemaInClasspath("users.json"));
		}
}
