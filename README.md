## Installation

* `$ git clone https://github.com/Balita1124/spring-example-restfull-api.git`
* `$ cd spring-example-restfull-api`
* `$ mvn install`
* `$ mvn spring-boot:run`   _(ou utiliser l'IDE)_

## Architecture
#### _I. Base_

**1. Aller a https://start.spring.io/**

**2. Creer un projet avec les dependences suivantes:**

 * Spring web Starter
 * Mysql Driver
 * Spring Data Jpa
 * Spring Boot DevTools
 * Lombok
  
 **3. Ajouter l'entity DateAudit**
 ```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
public abstract class DateAudit implements Serializable {

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false , columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant updatedAt;
}
```
 
 **4. Ajouter le config pour CORS et l'audit pour le JPA**
 * CORS
 ```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    public final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }
}
```
 * Audit JPA
 
 ```java
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}
```
 **5. Creer les errors Custom**
 * Field Error personnalisé
 
 ```java
@Data
public class CustomFieldError {
    private String fieldname;
    private String errorMessage;

    public CustomFieldError(String fieldname, String errorMessage) {
        this.fieldname = fieldname;
        this.errorMessage = errorMessage;
    }
}
```
 * Section erreur dans la reponse
 
 ```java
@Data
public class ErrorSection {
    Object request;
    List<?> errors;

    public ErrorSection(Object request, List<ObjectError> errors) {
        List<CustomFieldError> listErrors = new ArrayList<>();
        if (errors != null)
            errors.forEach(objectError -> listErrors.add(new CustomFieldError(((FieldError) objectError).getField(), objectError.getDefaultMessage())));
        this.request = request;
        this.errors = listErrors;
    }

    public ErrorSection(List<String> errors, Object request) {
        this.request = request;
        this.errors = errors;
    }
}
```
 **6. Creer les models**
 
 * Enitity Person
 
 ```java
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PERSONS")
@Data
public class Person extends DateAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String firstname;

    @NotBlank
    private String lastname;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private Date birth;

    public Person() {
    }

    public Person(String firstname, String lastname, Date birth) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birth = birth;
    }
}
```
 **7. Creer les repository**
 
 ```java

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
}


```
 
 **8. Creer les services**
 
 ```java
@Service
public class PersonService {

    private PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAll() {
        List<Person> personList = new ArrayList<>();
        personRepository.findAll().forEach(personList::add);
        return personList;
    }

    public Person create(PersonRequest personRequest) {
        Person person = new Person(personRequest.getFirstname(), personRequest.getLastname(), personRequest.getBirth());
        return personRepository.save(person);

    }

    public Person update(Person currentPerson, PersonRequest personRequest) {
        currentPerson.setFirstname(personRequest.getFirstname());
        currentPerson.setLastname(personRequest.getLastname());
        currentPerson.setBirth(personRequest.getBirth());
        return personRepository.save(currentPerson);

    }

    public Person findPersonById(Integer personId) {
        return personRepository.findById(personId).orElse(null);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }
}
```
 
 **9. Creer les playload**
 
 * ApiResponse
 ```java
@Data
public class ApiResponse {
    private Boolean success;
    private HttpStatus status;
    private String message;
    private Object data;

    public ApiResponse(Boolean success, HttpStatus status, String message, Object data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
```
 * PersonRequest
 
 ```java

@Data
public class PersonRequest {

    private String firstname;

    @NotBlank(message = "Last name can't blank")
    private String lastname;

    @JsonFormat(pattern="dd-MM-yyyy")
    @NotNull(message = "Date of birth")
    private Date birth;
}
```
 **10. Creer les controllers**

#### _II. Swagger_

Swagger est une APi permettant de documenter un Rest Api

**1.  Ajouter la dependance maven**

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```



**2.  Integrer Swagger dans le Project**

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {                                    
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }
}
```

Il faut visiter http://localhost:8080/v2/api-docs pour verifier si ca marche.

#### _III. Swagger_

Une interface utilisateur qui permet d'interagir avec swagger

**1.  Ajouter la dependence**

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

IL faut visiter http://localhost:8080/swagger-ui.html pour voir si ca marche

**2. Filtrer l'api à exposer**

```java

@Bean
public Docket api() {                
    return new Docket(DocumentationType.SWAGGER_2)          
      .select()                                       
      .apis(RequestHandlerSelectors.basePackage("com.balita.springexamplecrud.controller"))
      .paths(PathSelectors.ant("/persons/*"))                     
      .build();
}

```

**3. Api Info**

```java
private ApiInfo apiInfo() {
    return new ApiInfo(
      "My REST API", 
      "Some custom description of API.", 
      "API TOS", 
      "Terms of service", 
      new Contact("John Doe", "www.example.com", "myeaddress@company.com"), 
      "License of API", "API license URL", Collections.emptyList());
}
```

