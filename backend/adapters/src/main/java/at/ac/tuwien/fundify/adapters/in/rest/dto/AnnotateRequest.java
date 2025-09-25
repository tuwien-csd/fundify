package at.ac.tuwien.fundify.adapters.in.rest.dto;

public record AnnotateRequest(
    String callId,
    String universityId,
    CallAnnotationWebModel annotation
) implements WebModel {

    @Override
    public String id() {
        return callId;
    }

    @Override
    public String publisherId() {
        return universityId;
    }
}