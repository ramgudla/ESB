package com.technotes.esb.server;

public interface ApplicationTransaction
{
	public boolean process ( BodyBlock requestBlock, BodyBlock responseBlock );
}