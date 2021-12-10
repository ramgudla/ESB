package com.technotes.esb.client.protocol;

import java.util.UUID;

public class Message
{

	public UUID id;

	public String text;

	public URI target;

	public Message ( UUID id, String text, URI target )
	{
		this.id = id;
		this.text = text;
		this.target = target;
	}
}