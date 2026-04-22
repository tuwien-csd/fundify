package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ECareerStageWebModel {

    EXPERTS("Experts"),
    STUDENTS("Students"),
    DOCTORAL_STUDENTS("Doctoral students"),
    EARLY_STAGE_RESEARCHERS("Early stage researchers"),
    MID_CAREER_RESEARCHERS("Mid-career researchers"),
    ESTABLISHED_RESEARCHERS("Established researchers"),
    UNDERGRADUATE_STUDENTS("Undergraduate students"),
    GRADUATE_STUDENTS("Graduate students"),
    POSTGRADUATE_STUDENTS("Postgraduate students"),
    POSTDOCTORAL_RESEARCHERS("Postdoctoral researchers"),
    SCIENTISTS("Scientists"),
    SCIENTIFIC_INSTITUTION("Scientific institution");

    private final String value;

    public static ECareerStageWebModel fromString(String value) {
        for (ECareerStageWebModel e : ECareerStageWebModel.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
