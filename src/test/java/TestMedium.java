import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

//import io.restassured.matcher.RestAssuredMatchers;
//import io.restassured.module.jsv.JsonSchemaValidator;

import org.junit.Test;

public class TestMedium {
	
	String url = "https://reqres.in/api/users";
	
	 @Test
	    public void getPageOne(){

	        given()
	        		.log().all()
	                .param("page", "1").
	        when()
	        		.log().all()
	                .get(url).
	        then()
	        		.log().all()
	                .statusCode(200)
	                .body("page", equalTo(1));
	    }

	    @Test  //sem log e com uma linha, mas a forma de cima é mais clara a leitura
	    public void getUser() {
	    	
	        get(url + "/2").then().body("data.id", equalTo(2));
	    }

}
