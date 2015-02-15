package restservice.rest_ticket;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author AMore
 */
@Path("ticket")
public class TicketResource {

    @Context
    private UriInfo context;

    public TicketResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{username}/{password}")
    public Response getTicket(
            @PathParam("username") String username,
            @PathParam("password") String password) {
        JsonObject value;

        if (authorizeUser(username, password)) {
            String ticketIDUUID = UUID.randomUUID().toString();

            value = Json.createObjectBuilder()
                    .add("tickettransaction", ticketIDUUID)
                    .build();
            return Response.status(200).entity(value).build();
        } else {
            value = Json.createObjectBuilder()
                    .add("tickettransaction", "false")
                    .add("reason", "User not authorized")
                    .build();

            return Response.status(200).entity(value.toString()).build();
        }
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public boolean authorizeUser(String username, String password) {
        try {
            Client client = Client.create();

            WebResource webResource = client.resource("http://localhost:8080/REST_Authorization/webresources/auth/login/" + username + "/" + password);

            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }

            JSONObject jsonObj = new JSONObject(response.getEntity(String.class));

            return Boolean.valueOf((String) jsonObj.get("authorized"));

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

    }

}
