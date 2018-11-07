import java.io.OutputStream;


import static spark.Spark.*;

/**
 * 	This Java program uses the Spark web application framework to run a webserver.
 *  See http://sparkjava.com/ for details on Spark.
 */
public class Main {
  public static void main(String... args) throws Exception {

	// Tell Spark to use the Environment variable "PORT" set by Heroku. If no PORT variable is set, default to port 5000.
	int port = System.getenv("PORT")== null ? 5000 : Integer.valueOf(System.getenv("PORT"));
	port(port);

	
	get("/", (req, res) -> "Hello Mobile Developers");
	
	// matches "GET /hello/foo" and "GET /hello/bar"
	// request.params(":name") is 'foo' or 'bar'
	get("/hello/:name", (request, response) -> {
	    return "Hello: " + request.params(":name");
	});
	
	// this route sesponds with the body of the request 
	post("/simple", (request, response) -> {
	  return "Request body: " + request.body();
	});

	// this route uses raw byte output for response
	post("/raw", (request, response) -> {
	  OutputStream out = response.raw().getOutputStream();
	  out.write("Writing to raw!".getBytes());
	  out.close();
	  return response.raw();
	});


	  //For help with the Spark framework this project is using, see http://sparkjava.com/documentation.html

	  //Tasks:
	  // 1. Define a route for handling a HTTP POST request

	  // 2. Get the image from the request, possibly storing it somewhere before proceeding.

	  // 3. Process the image using the JHLabs filtering library (you have to add the dependency)

	  // 4. Write the processed image to the HTTP response ( Tip: response.raw() can be helpful)

  }
}