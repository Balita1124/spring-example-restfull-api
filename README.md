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
 
 **6. Creer les models**
 
 **7. Creer les repository**
 
 **8. Creer les services**
 
 **9. Creer les playload**
 
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

Il faut visiter http://localhost:8080/spring-security-rest/api/v2/api-docs pour verifier si ca marche.

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

IL faut visiter http://localhost:8080/your-app-root/swagger-ui.html pour voir si ca marche

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

