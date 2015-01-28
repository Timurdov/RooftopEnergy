package nl.rooftopenergy.bionic.rest;

import nl.rooftopenergy.bionic.dao.company.CompanyDao;
import nl.rooftopenergy.bionic.dao.rtfbox.RtfBoxDao;
import nl.rooftopenergy.bionic.dao.user.UserDao;
import nl.rooftopenergy.bionic.entity.Company;
import nl.rooftopenergy.bionic.entity.RtfBox;
import nl.rooftopenergy.bionic.entity.User;
import nl.rooftopenergy.bionic.rest.util.PrincipalInformation;
import nl.rooftopenergy.bionic.transfer.PasswordTransfer;
import nl.rooftopenergy.bionic.transfer.UserDataTransfer;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.PasswordAuthentication;

/**
 * Created by Bess on 12/17/14.
 */
@RestController
@Path("boxData")
public class UserSettingsDataResource {
    private final Logger log = Logger.getLogger(UserSettingsDataResource.class.getName());

    @Inject
    private CompanyDao companyDao;

    @Inject
    private RtfBoxDao rtfBoxDao;

    @Inject
    private UserDao userDao;

    @Inject
    private PrincipalInformation principalInformation;

    /**
     * Saves new userInfo
     * @param paramDescription  is the new description of current user account that is about to be saved.
     * @param paramEmail        is the new email of the user.
     * @param paramPublicStatus is new publicStatus of the company.
     * @param paramPanelType    is the new panelType of RTFBox connected to a company.
     */
    @POST
    @Path("/saveNewUserInfo")
    public Response saveNewDescription(@FormParam("description") String paramDescription,
                                       @FormParam("email") String paramEmail,
                                       @FormParam("panelType") String paramPanelType,
                                       @FormParam("publicStatus") String paramPublicStatus) {
        User user = userDao.findByName(principalInformation.getPrincipalName());
        if (paramEmail != null) {
            user.setEmail(paramEmail);
            userDao.save(user);
        }

        //add logging and try CASCADE update

        Company company = principalInformation.getCompany();
        if (paramDescription == null) {
            paramDescription = "";
        }
        company.setDescription(paramDescription);
        Boolean publicStatus = Boolean.parseBoolean(paramPublicStatus);
        if (publicStatus != null) {
            company.setPublicStatus(publicStatus);
        }
        companyDao.save(company);

        if (paramPanelType == null) {
            paramPanelType = "";
        }
        RtfBox rtfBox = company.getRtfBox();
        rtfBox.setPanelType(paramPanelType);
        rtfBoxDao.save(rtfBox);

        return Response.ok().build();
    }

    /**
     * @return settings information about current user
     */
    @GET
    @Path("/getUserDescription")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDataTransfer getUserDescription() {
        String principalName = principalInformation.getPrincipalName();
        User user = userDao.findByName(principalName);
        Company company = user.getCompany();

        String userName = user.getUsername();
        String country = company.getCountry();
        String province = company.getProvince();
        String street = company.getStreet();
        Integer zipCode = company.getZipcode();
        String city = company.getTown();
        String companyName = company.getCompanyName();
        String panelType = company.getRtfBox().getPanelType();
        String description = company.getDescription();
        String email = user.getEmail();
        Boolean publicStatus = company.getPublicStatus();

        UserDataTransfer userDataTransfer = new UserDataTransfer(userName, description, panelType, companyName,
                city, zipCode, street, province, country, email, publicStatus);

        return userDataTransfer;
    }

    /**
     * Changes the password of current user
     * @param passwordTransfer  instance which contains old and new passwords
     * @return HTTP response 200, when everything is okay and HTTP response 304 when not modified
     *
     */
    @POST
    @Path("changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(PasswordTransfer passwordTransfer) {

        User currentUser = userDao.find(principalInformation.getPrincipalName());
        if (passwordTransfer.getOldPassword().equals(currentUser.getPassword())) {
            currentUser.setPassword(passwordTransfer.getNewPassword());
            userDao.save(currentUser);
        } else {
            return Response.notModified().build();
        }

        return Response.ok().build();
    }
}
