import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import io.restassured.http.ContentType;

public class EnvioDadosTest {
	
	@Test
	public void deveEnviarValorViaQuery() {
		given()
			.log().all()
			.accept(ContentType.JSON)//dizendo que so aceito valores no header do tipo json
		.when()
			.get("https://restapi.wcaquino.me/v2/users?format=json") //poderia por xml
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)// ele olha o atributo no head -content-type, e não o formato do arquivo
		;
	}
	
	@Test
	public void deveEnviarValorViaQueryViaParam() {
		given()
			.log().all()
			.queryParam("format", "xml")
			.queryParam("outra", "coisa")
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
			.contentType(containsString("utf-8"))//validando content type
		;
	}
	
	@Test
	public void deveEnviarValorViaHeader() {
		given()
			.log().all()
			.accept(ContentType.XML)//so aceita valores, pra dizer oq espero da resposta
		.when()
			.get("https://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
		;
	}

}
