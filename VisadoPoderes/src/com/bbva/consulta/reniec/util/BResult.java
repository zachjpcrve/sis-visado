package com.bbva.consulta.reniec.util;

/**
 * @author dmclemente
 * @version 1.0
 */

public class BResult {

	private Object object;
	private int code;
	private String message;

	/**
	 * Constructor for BResult.
	 */
	public BResult() {
		super();
		code = 0;
	}

	/**
	 * Returns the code.
	 * 
	 * @return byte
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns the message.
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the object.
	 * 
	 * @return Object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Sets the code.
	 * 
	 * @param code The code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the object.
	 * 
	 * @param object The object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

}
