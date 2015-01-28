package nl.rooftopenergy.bionic.transfer;

import java.util.Map;


public class UserTransfer
{

	private final String username;

	private final Map<String, Boolean> roles;


	public UserTransfer(String userName, Map<String, Boolean> roles)
	{
		this.username = userName;
		this.roles = roles;
	}


	public String getName()
	{
		return this.username;
	}


	public Map<String, Boolean> getRoles()
	{
		return this.roles;
	}

}