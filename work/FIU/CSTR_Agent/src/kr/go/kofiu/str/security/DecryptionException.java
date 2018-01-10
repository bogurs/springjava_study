package kr.go.kofiu.str.security;


public class DecryptionException extends RuntimeException {
	
    public DecryptionException(String message) {
    	super(message);
    }
    
    public DecryptionException(Throwable cause) {
        super(cause);
    }
}