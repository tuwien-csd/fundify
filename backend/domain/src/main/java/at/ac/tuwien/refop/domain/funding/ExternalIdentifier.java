package at.ac.tuwien.refop.domain.funding;

import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.domain.funding.vo.enums.EIdentifierType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ExternalIdentifier {

    EnumMap<EIdentifierType, Identifier> identifiers = new EnumMap<>(EIdentifierType.class);

    public ExternalIdentifier() {
    }

    public ExternalIdentifier(List<Identifier> identifierList, RisId risId) {
        handleAssignIdentifierList(identifierList);
        handleAssignRisIdentifier(risId);
    }

    public Identifier getIdentifier(EIdentifierType type) {
        return identifiers.get(type);
    }

    public void setIdentifier(EIdentifierType type, String value) {
        identifiers.put(type, new Identifier(type, value));
    }

    public void setIdentifiers(List<Identifier> identifierList) {
        handleAssignIdentifierList(identifierList);
    }

    public List<Identifier> getIdentifiers() {
        return new ArrayList<>(identifiers.values());
    }

    public void setRisId(RisId risId) {
        handleAssignRisIdentifier(risId);
    }

    public RisId getRisId() {
        Identifier risIdentifier = getIdentifier(EIdentifierType.RIS_SYNERGY);
        if (risIdentifier == null) {
            return null;
        }
        return RisId.fromString(risIdentifier.value());
    }

    private void handleAssignIdentifierList(List<Identifier> identifierList) {
        if (identifierList != null) {
            for (Identifier identifier : identifierList) {
                identifiers.put(identifier.type(), identifier);
            }
        }
    }

    private void handleAssignRisIdentifier(RisId risId) {
        if (identifiers.get(EIdentifierType.RIS_SYNERGY) == null && risId != null) {
            identifiers.put(EIdentifierType.RIS_SYNERGY, new Identifier(EIdentifierType.RIS_SYNERGY, risId.toString()));
        }
    }
}
