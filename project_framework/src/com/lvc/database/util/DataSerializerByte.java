package com.lvc.database.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class DataSerializerByte {

	public static byte[] toByte(Object target) throws RuntimeException {

		try {


			byte[] yourBytes =  null;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
				out = new ObjectOutputStream(bos);   
				out.writeObject(target);
				yourBytes = bos.toByteArray();
			} finally { 
				if (out != null) {
					out.close();
				}
				bos.close();
			} 
			return yourBytes;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object toObject(byte[] yourBytes) throws RuntimeException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
			ObjectInput in = null;
			Object o = null;
			try {
				in = new ObjectInputStream(bis);
				o = in.readObject(); 

			} finally {
				bis.close(); 
				if (in != null) {
					in.close();
				} 
			}

			return o;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
