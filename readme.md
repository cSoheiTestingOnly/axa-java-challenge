### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Modification done by Chikama Sohei

##### 1. Changed @Autowired from properties to the constructor

Property based Autowired will make it hard to write the tests for bean class.
By using the property based constructor, the beans can be easily mocked without stubbing or 
replacing the beans with the test-only-bean defs.

##### 2. Return structured response instead of entity itself or list of them.

Returning the entity itself or list of the entities are hard to modify in the future.  
The structured response can be modified even if there are some additional requirement such as metadata or tokens in the future.  
It is very likely that getAll API will be required to add pagination feature in the future, but if the response is formed like  

`[{id: "aaa", name: "bbb""}]`

, then we cannot add page token to this json scheme without breaking the backward compatibility.
By forming the response json scheme as  

`{result: [{id: "aaa", name: "bbb""}]}`  

, we can easily add pageToken like

`{result: [{id: "aaa", name: "bbb""}], pageToken: "AAAAA"}`  

##### 3. Replaced return type of service layer with the Either or Optional

There was no error handling implemented in the service layer.  
I replaced all the return value with the Monad functor and let method caller to handle the exception.  
For more detail, I have written an article about the error handling before.  
Please take a look if you wanna know the detail.
[Exceptionをもみ消すなって言われた人のためのエラーハンドリングの話。](https://qiita.com/Munchkin/items/48897c98c8b9749d66b5)


##### 4. Changed content location of create API from query param to request body

Query param can be logged at the proxy server or application server.  
Even with current EmployeeEntity's spec, name or salary can be considered as confidential data and it should not be printed in the logs.  
Moreover, if there will be some requirements to store the address or bank account into this table, those information must'nt be printed in the logs.  
To change it in the future, we cannot modify the API without breaking backward compatibility


##### 5. Change the response status dependent on the error type if any happened.

One of the most important spec of the RESTAPI is to notify the API status with the HTTP Status.  
Thus, I distinguish the error type and changed error status with the correct one instead of 500 INTERNAL SERVER ERROR.  


##### 6. Return original entity if delete or update  

Services which work together with other service through the API, especially in the micro service architecture,
rollback or retry is the duty of the API user unless the service is supporting rollback feature such as SAGA or TCC.  
In that case, it will be quite hard to roll back if the API caller do not know the original state of the deleted/updated entity.  
Thus, it should be better to return the updated/deleted entity if the API call succeeded.


#### What Chikama didn't do this time.

##### 1. Check authority of the request.

In most of the webapi, it is required to check the authorization header of HTTP and make sure that the API caller has the 
permission enough to call the API.  
This feature is expected to be implemented as the **Aspect** by using spring-aop or some other Aspect Oriented Programming framework/library.  
However, in this time, there was no information about the authorization present as the pre-condition.  
Thus, I skipped the implementation regarding this part.  

##### 2. Use different class between the layers.

It is better to use the different model in the each of layer such as Service, Controller and Repository.  
The models, in other word, data structures should be designed suitable for the business of each layer.  
For instance, the model in the service layer should be implemented as immutable so that developers can easily trace the 
state change of instance even in the complex business logic.  
The entity of the repository has several annotation only for OR mapping and it is not required in the Controller or service layer. 
If we use different model for each, we can easily change the data structure without changing the logic in the Service layer 
or Presentation layer even if we need to use the different DB or another OR mapper.  

This time, business logic in the API is quite simple and time is limited, thus I didn't implement such kinda models.  

