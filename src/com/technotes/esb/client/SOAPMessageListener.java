package com.technotes.esb.client;

import com.technotes.esb.client.protocol.Message;

public interface SOAPMessageListener
{
	void onReceive ( Message msg );
}