package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.TicketCreateWebModel;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface TicketWebModelMapper {

  TicketWebModelMapper INSTANCE = Mappers.getMapper(TicketWebModelMapper.class);

  TicketCreate toDomain(TicketCreateWebModel source);

}



