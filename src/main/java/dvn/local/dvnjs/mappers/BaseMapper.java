package dvn.local.dvnjs.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import dvn.local.dvnjs.annotations.BaseMapperAnnotation;

public interface BaseMapper <E, R, C, U> {
    R tResource(E entity);

    List<R> toList(List<E> entities);

    default Page<R> toResourcePage(Page<E> page) {
        return page.map(this::tResource);
    }

    @BaseMapperAnnotation
    @BeanMapping(nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE)
    E toEntity(C createRequest);

    @BaseMapperAnnotation
    @BeanMapping(nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromResource(U updateRequest,@MappingTarget E entity);

}
