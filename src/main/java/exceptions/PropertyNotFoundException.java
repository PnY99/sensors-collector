package exceptions;

public class PropertyNotFoundException extends Exception{
    public PropertyNotFoundException(String msg) {
        super(msg);
    }

    public PropertyNotFoundException() {
        super();
    }
}
