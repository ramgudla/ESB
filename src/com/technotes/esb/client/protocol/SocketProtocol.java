package com.technotes.esb.client.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketProtocol
{

	public static Message read ( final DataInputStream dis ) throws IOException
	{
		int actualPacketSize = dis.readInt();
		// Allocate the bytes.
		byte[] socketBytes = new byte[actualPacketSize];
		// Read the number of bytes expected from the stream.
		dis.readFully(socketBytes, 0, actualPacketSize);
		return SOAPMessage.toMessage(new String(socketBytes));
	}

	public static void write ( final DataOutputStream outputStream, final Message message ) throws IOException
	{
		// Get the length of payload
		int actualPacketSize = message.text.getBytes().length;
		outputStream.writeInt(actualPacketSize);
		outputStream.write(message.text.getBytes());
		outputStream.flush();
	}
}