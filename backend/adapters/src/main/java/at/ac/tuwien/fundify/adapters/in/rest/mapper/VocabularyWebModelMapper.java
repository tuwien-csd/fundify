package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.VocabularyWebModel;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
                CommonIdMapper.class,
                PublisherRefWebModelMapper.class
        })
public interface VocabularyWebModelMapper {
    VocabularyWebModelMapper INSTANCE = Mappers.getMapper(VocabularyWebModelMapper.class);

    VocabularyWebModel fromDTO(Vocabulary source);
    List<VocabularyWebModel> fromDTO(List<Vocabulary> source);
}
