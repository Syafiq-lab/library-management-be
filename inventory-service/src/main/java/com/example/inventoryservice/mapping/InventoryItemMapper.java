package com.example.inventoryservice.mapping;

import com.example.inventoryservice.domain.InventoryItem;
import com.example.inventoryservice.web.dto.InventoryItemCreateRequest;
import com.example.inventoryservice.web.dto.InventoryItemUpdateRequest;
import com.example.inventoryservice.web.dto.InventoryItemResponse;
import org.mapstruct.*;
import java.time.Instant;

@Mapper(componentModel = "spring",
        imports = Instant.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    InventoryItem toEntity(InventoryItemCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(InventoryItemUpdateRequest request, @MappingTarget InventoryItem entity);

    InventoryItemResponse toResponse(InventoryItem entity);
}
