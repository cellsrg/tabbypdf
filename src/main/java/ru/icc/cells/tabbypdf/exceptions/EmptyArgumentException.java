package ru.icc.cells.tabbypdf.exceptions;

/**
 * @author aaltaev
 */
public class EmptyArgumentException extends IllegalArgumentException {
	/**
	 *
	 */
	private static final long serialVersionUID = -1772176241681819558L;

	public EmptyArgumentException() {
        super();
    }

    public EmptyArgumentException(String message) {
        super(message);
    }

    public EmptyArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyArgumentException(Throwable cause) {
        super(cause);
    }
}
