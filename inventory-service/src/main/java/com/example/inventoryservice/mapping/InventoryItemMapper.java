package com.example.inventoryservice.mapping;

import com.example.inventoryservice.domain.InventoryItem;
import com.example.inventoryservice.dto.InventoryItemCreateRequest;
import com.example.inventoryservice.dto.InventoryItemUpdateRequest;
import com.example.inventoryservice.dto.InventoryItemResponse;
import org.mapstruct.*;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "spring",
        imports = Instant.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryItemMapper {

	Logger log = LoggerFactory.getLogger(InventoryItemMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    InventoryItem toEntity(InventoryItemCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(InventoryItemUpdateRequest request, @MappingTarget InventoryItem entity);

    InventoryItemResponse toResponse(InventoryItem entity);
}