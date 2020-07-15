package kr.ac.green;

import java.io.Closeable;
import java.io.Serializable;

public class MyUtils implements Serializable{
	public static void closeAll(Closeable... c) {
		for( Closeable temp : c ) {
			try {
				temp.close();
			} catch(Exception e) {}
		}
	}
}
