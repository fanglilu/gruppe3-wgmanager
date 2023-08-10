# Swagger
To use swagger go to
[Swagger UI](http://localhost:8080/swagger-ui/index.html)

## Authentication
1. Get register as user
2. Login with your user, you will get a token to autheticate yourself in swagger-ui
3. Enter the token on "Auth" (right upper corner)
4. You can start testing
5. If you use Postman make sure you copied the token also in there!

### Development bypass
Go to file : [SecurityConfiguration.kt](src/main/kotlin/lmu/gruppe3/wgmanager/common/configurations/SecurityConfiguration.kt)
Add your path 
```
.antMatchers("/YOUR_PATH_NAME/**").permitAll()
```
Now it is excluded from any authorization. Happy developping and don't forget to remove it after you are done ;)

# Test Daten
1. WG: Yara, Fangli
2. WG: Moritz, Marcello, Niklas
3. WG: Niklas, Lisa (weniger Features)


# Features

- Wg create
- Wg join


## Security

- [Invitation codes expire after a defined time](src/main/kotlin/lmu/gruppe3/wgmanager/wg_root/service/WgRootService.kt)