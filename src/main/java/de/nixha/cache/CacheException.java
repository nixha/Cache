package de.nixha.cache;

/**
 * 
 * @author Hans Nix
 * 
 * @since 1.0.0
 */
public class CacheException extends Exception {

	private static final long serialVersionUID = 5505244064234811322L;

	public CacheException() {
		super();
	}

	public CacheException(String message) {
		super(message);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}

	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

}
