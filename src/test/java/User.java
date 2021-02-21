import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="user")//para trabalhar com xml definidno a classe com name user, por conta do método deveSalvarUsuarioViaXMLUsandoObjeto
@XmlAccessorType(XmlAccessType.FIELD)//pega todos atributos da classe, inclusive os get
public class User {
	
	//para gerar os gete e set, clique com botão direito, source, generate getters and setters
	@XmlAttribute  //para resolver problema do id no xml, porq o id é um atributo do user
	private Long id;
	private String name;
	private Integer age;
	private Double salary;


	public User() {

		super();
	}



	//Depois devemos gerar construtor, vou gerar apenas do name e age para ser mais simples
	public User(String name, Integer age, Double salary) {
		super();
		this.name = name;
		this.age = age;
		this.salary = salary;
	}	
	


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	//botão direito source Generate ToString() 
	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", salary=" + salary + ", id=" + id + "]";
	}
	
	

}


