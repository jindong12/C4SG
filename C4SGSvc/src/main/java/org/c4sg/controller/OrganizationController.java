package org.c4sg.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.codec.binary.Base64;
import org.c4sg.dto.OrganizationDTO;
import org.c4sg.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import static org.c4sg.service.OrganizationService.UPLOAD_DIRECTORY;

@CrossOrigin
@RestController
@RequestMapping("/api/organization")
@Api(description = "Operations about Organizations", tags = "organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(value = "/{organizationName}/uploadLogo", method = RequestMethod.POST)
    @ApiOperation(value = "Add new upload Logo")
    public String uploadLogo(@ApiParam(value = "Organization Name", required = true)
                                 @PathVariable String organizationName,
                             @ApiParam(value = "Request Body", required = true)
                                 @RequestBody String requestBody) {
        try {
            byte[] imageByte = Base64.decodeBase64(requestBody);
            File directory = new File(UPLOAD_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File f = new File(organizationService.getLogoUploadPath(organizationName));
            new FileOutputStream(f).write(imageByte);
            return "Success";
        } catch (Exception e) {
            return "Error saving logo for organization " + organizationName + " : " + e;
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/all", produces = { "application/json" }, method = RequestMethod.GET)
    @ApiOperation(value = "Find all organizations", notes = "Returns a collection of organizations")
    public List<OrganizationDTO> getOrganizations() {
        return organizationService.findOrganizations();
    }
    
    @CrossOrigin
    @RequestMapping(value = "/search/byId/{id}", produces = { "application/json" }, method = RequestMethod.GET)
    @ApiOperation(value = "Find organization by ID", notes = "Returns a collection of organizations")
    public OrganizationDTO getOrganization(@ApiParam(value = "ID of organization to return", required = true)
                                               @PathVariable("id") int id) {
        return organizationService.findById(id);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/search/byKeyword/{keyWord}", produces = { "application/json" }, method = RequestMethod.GET)
    @ApiOperation(value = "Find organization by keyWord", notes = "Returns a collection of organizations")
    public List<OrganizationDTO> getOrganization(@ApiParam(value = "Keyword of organization to return", required = true)
                                                     @PathVariable("keyWord") String keyWord) {
        return organizationService.findByKeyword(keyWord);
    }
    
    @CrossOrigin
    @RequestMapping(value="/create", method = RequestMethod.POST)
    @ApiOperation(value = "Add a new organization")
    public Map<String, Object> createOrganization(@ApiParam(value = "Organization object to return", required = true)
                                                      @RequestBody @Valid OrganizationDTO organizationDTO){
    	System.out.println("**************Create**************");
    	Map<String, Object> responseData = null;
        organizationDTO.setLogo(organizationService.getLogoUploadPath(organizationDTO.getName()));
        try{
    		OrganizationDTO createdOrganization = organizationService.createOrganization(organizationDTO);
    		responseData = Collections.synchronizedMap(new HashMap<>());
    		responseData.put("organization", createdOrganization);
    	}catch(Exception e){
    		System.err.println(e);
    	}
    	return responseData;
    }
    
    @CrossOrigin
    @RequestMapping(value="/update/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing organization")
    public Map<String, Object> updateOrganization(@ApiParam(value = "Updated organization object", required = true)
                                                      @PathVariable("id") int id,
                                                  @RequestBody @Valid OrganizationDTO organizationDTO){
    	System.out.println("**************Update : id=" + organizationDTO.getId() + "**************");
    	Map<String, Object> responseData = null;
    	try{
    		OrganizationDTO updatedOrganization = organizationService.updateOrganization(id, organizationDTO);
    		responseData = Collections.synchronizedMap(new HashMap<>());
    		responseData.put("organization", updatedOrganization);
    	}catch(Exception e){
    		System.err.println(e);
    	}
    	return responseData;
    }

    @CrossOrigin
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes a organization")
    public void deleteOrganization(@ApiParam(value = "Organization id to delete", required = true)
                                       @PathVariable("id") int id) {
        System.out.println("************** Delete : id=" + id + "**************");

        try {
            organizationService.deleteOrganization(id);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}