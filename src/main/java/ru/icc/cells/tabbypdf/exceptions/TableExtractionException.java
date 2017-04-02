package ru.icc.cells.tabbypdf.exceptions;

/**
 * @author aaltaev
 */
public class TableExtractionException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -3362896806940945907L;

	public TableExtractionException() {
        super();
    }

    public TableExtractionException(String message) {
        super(message);
    }

    public TableExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableExtractionException(Throwable cause) {
        super(cause);
    }

    protected TableExtractionException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
