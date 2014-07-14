package com.poc.acteur;

import java.io.Serializable;

/**
 * 
 * @author david
 *
 */
public final class ResultMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultMessage(final int value) {
		this.value = value;
	}

	private final  int value;

	public int getValue() {
		return value;
	}
	
	

}
