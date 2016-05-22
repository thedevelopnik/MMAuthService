import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Created by benhernandez on 5/21/16.
 */
public class AuthServiceTest {
    @BeforeClass
    public static void beforeAll() {
        String[] args = {};
        AuthService.main(args);
    }

    @Before
    public void beforeEach() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/Users");
        Statement statement = connection.createStatement();
        String query = "delete from users";
        statement.execute(query);
        statement.close();
        connection.close();
    }

    @Test
    public void test404Get() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .get("http://localhost:8080/doesnotexist");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = new JSONObject(response.getErrorBody().toString());
        JSONObject expected = new JSONObject();
        expected.put("message", "error");
        expected.put("status", 404);
        expected.put("requested resource", "/doesnotexist");
        expected.put("requested method", "GET");
        JSONAssert.assertEquals(expected, result, true);
    }

    @Test
    public void test404Post() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .post("http://localhost:8080/doesnotexist");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = new JSONObject(response.getErrorBody().toString());
        JSONObject expected = new JSONObject();
        expected.put("message", "error");
        expected.put("status", 404);
        expected.put("requested resource", "/doesnotexist");
        expected.put("requested method", "POST");
        JSONAssert.assertEquals(expected, result, true);
    }

    @Test
    public void test404Put() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .put("http://localhost:8080/doesnotexist");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = new JSONObject(response.getErrorBody().toString());
        JSONObject expected = new JSONObject();
        expected.put("message", "error");
        expected.put("status", 404);
        expected.put("requested resource", "/doesnotexist");
        expected.put("requested method", "PUT");
        JSONAssert.assertEquals(expected, result, true);
    }

    @Test
    public void test404Delete() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .delete("http://localhost:8080/doesnotexist");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = new JSONObject(response.getErrorBody().toString());
        JSONObject expected = new JSONObject();
        expected.put("message", "error");
        expected.put("status", 404);
        expected.put("requested resource", "/doesnotexist");
        expected.put("requested method", "DELETE");
        JSONAssert.assertEquals(expected, result, true);
    }

    @Test
    public void testCreateValidUser()  throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .post("http://localhost:8080/create")
                .param("email", "test@test.com")
                .param("password", "password");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = response.getBody();
        if (result == null) {
            result = new JSONObject(response.getErrorBody().toString());
        }
        JSONObject expected = new JSONObject();
        expected.put("message", "User created");
        expected.put("status", 201);
        expected.put("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0.egbaJ7yWUvC4mU_C7LNJi24cPNpfx3rlr7woWn9pqsGX6LrGCK2Rf2LaD2cFiJ4AWC93QDMChuCmUM4YtDjzAw");
        JSONAssert.assertEquals(expected, result, true);
        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void testCreatedAlreadyExistingUser() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .post("http://localhost:8080/create")
                .param("email", "test@test.com")
                .param("password", "password");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = response.getBody();
        if (result != null) {
            request = webb
                    .post("http://localhost:8080/create")
                    .param("email", "test@test.com")
                    .param("password", "password");
            response = request
                    .asJsonObject();
            result = new JSONObject(response.getErrorBody().toString());
            JSONObject expected = new JSONObject();
            expected.put("message", "User already exists");
            expected.put("status", 409);
            JSONAssert.assertEquals(expected, result, true);
            assertEquals(409, response.getStatusCode());
        }
    }

    @Test
    public void testRead() throws Exception {
        Webb webb = Webb.create();
        Request request = webb
                .post("http://localhost:8080/create")
                .param("email", "test@test.com")
                .param("password", "password");
        Response<JSONObject> response = request
                .asJsonObject();
        JSONObject result = response.getBody();
        if (result != null) {
            request = webb
                    .post("http://localhost:8080/read")
                    .param("email", "test@test.com")
                    .param("password", "password");
            response = request
                    .asJsonObject();
            result = response.getBody();
            if (result == null) {
                result = new JSONObject(response.getErrorBody().toString());
            }
            JSONObject expected = new JSONObject();
            expected.put("message", "Logged in");
            expected.put("status", 200);
            expected.put("token", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0.egbaJ7yWUvC4mU_C7LNJi24cPNpfx3rlr7woWn9pqsGX6LrGCK2Rf2LaD2cFiJ4AWC93QDMChuCmUM4YtDjzAw");
            JSONAssert.assertEquals(expected, result, true);
            assertEquals(200, response.getStatusCode());
        } else {
            fail("Unable to even create the user");
        }

    }
}