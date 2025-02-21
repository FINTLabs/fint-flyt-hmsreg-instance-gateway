package no.fintlabs.instance.gateway.codelist.links;

import no.fint.model.resource.FintLinks;

public class NoSuchLinkException extends RuntimeException {

    public static NoSuchLinkException noLink(FintLinks resource, String linkedResourceName) {
        return new NoSuchLinkException("No link for resource");
    }

    public NoSuchLinkException(String message) {
        super(message);
    }

}
