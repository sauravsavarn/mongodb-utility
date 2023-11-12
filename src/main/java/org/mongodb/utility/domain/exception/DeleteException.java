package org.mongodb.utility.domain.exception;

public class DeleteException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3212376071282127434L;

	public DeleteException(String errMsg) {
		super(errMsg);
	}

	public DeleteException(String exMsg, Throwable e) {
		super(exMsg, e);
	}

}
