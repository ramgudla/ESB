package com.technotes.jmx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;

/**
 * This class provides the simple implementation of JMXAuthenticator.
 */
class PasswordAuthenticator implements JMXAuthenticator
{
	private static final String s_userName = "admin";

	private static final String s_password = "r@mky";

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.management.remote.JMXAuthenticator#authenticate(java.lang.Object)
	 * This method provides the simple implementation of the authenticate method
	 * of JMXAuthenticator.
	 */
	@Override
	public Subject authenticate ( Object credentials ) throws SecurityException
	{
		if (!(credentials instanceof String[]))
		{
			throw new SecurityException("Unsupported credentials format");
		}

		String creds[] = (String[]) credentials;

		if (creds.length != 2)
		{
			throw new SecurityException("Unsupported credentials format");
		}

		String userName = creds[0];
		String password = creds[1];

		if ((userName == null) || !userName.equals(s_userName) || (password == null) || !password.equals(s_password))
		{
			throw new SecurityException("Bad user name/password combination");
		}

		Set<JMXPrincipal> principals = new HashSet<JMXPrincipal>();
		principals.add(new JMXPrincipal(s_userName));
		return new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
	}
}
