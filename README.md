# Spark Streaming

To run this project locally the following steps needs to be executed:

- Go to https://apps.twitter.com
- Select "Create new app". Use a random name and description, and "http://example.org" as url
- Navigate to "Keys and Access Tokens" and copy the `Consumer Key (API Key)`, `Consumer Secret (API Secret)`, `Access Token` and `Access Token Secret`
- Create a file called `twitter.properties` in the folder `src/main/resources`
- The file must contain the following:

```
oauth.consumerKey=<your-key>
oauth.consumerSecret=<your-secret>
oauth.accessToken=<your-token>
oauth.accessTokenSecret<your-secret>
```
- Run the `main`-method in the class `JettyServer`
- Open the application on `http://localhost:8080`
