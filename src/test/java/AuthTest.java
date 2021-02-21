import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;


import io.restassured.module.jsv.JsonSchemaValidator;



public class AuthTest {
	
	@Test
	public void deveAcessarSWAPI() {
		given()
			.log().all()
		.when()
			.get("https://swapi.co/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
			//.body("name", is("Luke Skywalker"))
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("auth.json"))  //validando json atributes, mas não funcionou hahaha
		;
	}
	
	
	//  http://api.openweathermap.org/data/2.5/weather?q=Fortaleza,BR&appid=29178e691ea4287d83568b08778215c8&units=metric
	
	@Test
	public void deveObterClima() {
		given()
			.log().all()
			.queryParam("q", "Fortaleza,BR")                         // já converte para não ficar com lixo na rota
			.queryParam("appid", "5c891dcd37e872d7e2bd9e7d4dc22966") // meu id no portal, olhar documentação
			.queryParam("units", "metric")
		.when()
			.get("http://api.openweathermap.org/data/2.5/weather")
			
			
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Fortaleza"))
			.body("coord.lon", is(-38.52f))
			.body("main.temp", greaterThan(25f))
		;
	}
	
	
	@Test
	public void naoDeveAcessarSemSenha() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica() {
		given()
			.log().all()
		.when()
			.get("https://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica2() {
		given()
			.log().all()
			.auth().basic("admin", "senha")
		.when()
		  //.get("https://admin:senha@restapi.wcaquino.me/basicauth2")
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoBasicaChallenge() {
		given()
			.log().all()
			.auth().preemptive().basic("admin", "senha") //se atentar
		.when()
		.get("https://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoComTokenJWT() {
		
		Map<String, String> login = new HashMap<String, String>();        //map do java util criado para não ter que criar uam string gigante com os dados
		login.put("email", "ricardoveiga.ti@gmail.com");                  // maping é usado toda vez precisar armazenar chave e valor
		login.put("senha", "123456");
		
		//Login na api
		//Receber o token
		String token = given()   //recebo token na requisição e incluo na variável
			.log().all()
			.body(login)  // criado via mapping para jogar email e senha como um objeto
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token");
		;
		
		//Obter as contas
		given()
			.log().all()
			.header("Authorization", "JWT " + token)  //poderia ser via Bearer token
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", hasItem("conta de teste"))  //has item foi usado porque é uma colecao e ao apenas um item
			.body("", hasSize(2))
		;
	}
	
	
	
	
	
	
	//não cheguei a ver essa solução
	@Test
	public void deveAcessarAplicacaoWeb() {
		//login
		String cookie = given()
			.log().all()
			.formParam("email", "ricardoveiga.ti@gmail.com")
			.formParam("senha", "123456")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")//se atentar a url
		.then()
			.log().all()
			.statusCode(200)
			.extract().header("set-cookie");
		;
		
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.println(cookie);
		
		//obter conta
		
		String body = given()
			.log().all()
			.cookie("connect.sid", cookie)
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("conta de teste"))
			.extract().body().asString();
		;
		
		System.out.println("--------------");
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
	}

}
