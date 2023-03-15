package rsocket.server.mapper;

import org.mapstruct.Mapper;
import rsocket.client.model.SystemParamDTO;
import rsocket.server.entity.mongo.SystemParam;

@Mapper(componentModel = "spring")
public interface SystemParamMapper {

  SystemParam toSystemParam(SystemParamDTO systemParamDTO);
}
