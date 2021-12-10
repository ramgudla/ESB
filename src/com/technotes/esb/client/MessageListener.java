package com.technotes.esb.client;

import com.technotes.esb.client.protocol.Message;

public interface MessageListener
{
	void onReceive ( Message msg );
}