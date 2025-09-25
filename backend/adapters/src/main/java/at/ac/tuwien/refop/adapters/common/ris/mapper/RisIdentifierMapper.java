package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisIdentifier;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RisIdentifierMapper {

    RisIdentifierMapper INSTANCE = Mappers.getMapper(RisIdentifierMapper.class);

    Identifier toDomain(RisIdentifier source);
    List<Identifier> toDomain(List<RisIdentifier> source);

    RisIdentifier fromDomain(Identifier source);
    List<RisIdentifier> fromDomain(List<Identifier> source);
}
