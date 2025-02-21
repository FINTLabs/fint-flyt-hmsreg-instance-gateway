package no.fintlabs.instance.gateway.codelist.links;

public class NoSuchLinkException extends RuntimeException {

    public static NoSuchLinkException noLink() {
        return new NoSuchLinkException("No link for resource");
    }

    public NoSuchLinkException(String message) {
        super(message);
    }

}
