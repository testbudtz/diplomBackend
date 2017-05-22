package rest.login;

import config.Config;
import config.DeployConfig;
import data.JWTHandler;
import data.dbDTO.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

/**
 * Service for Campusnet redirects
 * Created by Christian on 28-04-2017.
 */
@Path(Config.CN_SERVICE_PATH)
public class CampusNetService {


    //STEP 1: Redirecting to campusNet Authentication
    @Path(Config.CN_SERVICE_LOGIN)
    @GET
    public Response getLogin() {
        URI uri = URI.create(Config.CAMPUSNET_LOGIN_URL + "?service=" + DeployConfig.CN_REDIRECT_URL);
        return Response.temporaryRedirect(uri).build();
    }

    //STEP 2: Getting redirectURL from Campusnet:
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getToken(@QueryParam("ticket") String ticket) {
        // STEP 3: Exchange Ticket for Login Details:
        OkHttpClient client = new OkHttpClient();
        String url = Config.CAMPUSNET_VALIDATE_URL + "?service=" + DeployConfig.CN_REDIRECT_URL + "&ticket=" + ticket;
        Request request = new Request.Builder().url(url).build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            String validationReply = response.body().string();
            String[] validationArray = validationReply.split("\n");
            String frontUrl = (DeployConfig.PORTAL_FRONT_URL == null) ? "" : DeployConfig.PORTAL_FRONT_URL;
            String jwtToken = "";
            //STEP 4: Issue Token and redicret to frontpage including token in url:
            if (validationArray != null && validationArray.length == 2 && validationArray[0].toLowerCase().trim().equals("yes")) {
                jwtToken = JWTHandler.generateJwtToken(new User(validationArray[1]));
                return Response.ok().entity(
                        "Login Success, id: " + validationArray[1] + "<a href=\"" + frontUrl + "/?token=" + jwtToken + "\">Goto main page</a>"
                )
                        .header(
                                "Authorization", "Bearer " + jwtToken


                        ).build();
            } else {
                return Response.status(401).entity("Login failed, reply was: "  + validationReply + "<br><a href='" + frontUrl +"'>Return to frontPage</a>").build();
            }
        } catch (IOException e)

        {
            e.printStackTrace();
            return Response.serverError().entity("Could not connect to campusnet-auth").build();
        }

    }
}
