# Arquitectura del Proyecto Vivia

## Principios Generales

1. **Clean Architecture** - Separación clara de responsabilidades
2. **Nomenclatura en Inglés** - Todo el código en inglés (excepto mensajes de validación al usuario)
3. **UUIDs como VARCHAR(50)** - Para todas las entidades principales
4. **Builder Pattern** - Para entidades de dominio
5. **Validación en Dominio** - Las reglas de negocio viven en el dominio

---

## Estructura de Capas por Feature

Cada feature sigue esta estructura:

```
features/
  └── {feature}/
      ├── domain/          # Capa de Dominio (independiente de frameworks)
      │   ├── entities/    # Entidades de negocio (POJO con Builder)
      │   ├── repositories/# Interfaces de repositorios
      │   ├── exceptions/  # Excepciones de negocio
      │   └── valueobjects/# Value Objects (opcional)
      │
      ├── data/            # Capa de Datos (infraestructura)
      │   ├── entities/    # Entidades JPA (@Entity)
      │   ├── repositories/# JpaRepository + Adapter
      │   ├── dtos/        # DTOs de request/response
      │   │   ├── request/
      │   │   └── response/
      │   └── mappers/     # Mappers (Entity <-> Domain)
      │
      ├── services/        # Capa de Aplicación
      │   ├── impl/        # Implementaciones de servicios
      │   ├── mappers/     # Mappers (Entity <-> DTO)
      │   └── I{Name}Service.java  # Interfaces de servicios
      │
      └── controllers/     # Capa de Presentación
          └── {Name}Controller.java
```

---

## CAPA 1: DOMAIN (Dominio)

### 1.1 Domain Entities

**Características:**
- POJO puro (sin anotaciones de frameworks)
- Inmutable (campos `final`)
- Builder Pattern
- Validación en constructor
- Getters únicamente

**Ejemplo:**
```java
package {feature}.domain.entities;

import {feature}.domain.exceptions.Invalid{Name}Exception;
import java.util.UUID;

public class Lessor {
    private final UUID id;
    private final String phoneNumber;

    private Lessor(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.phoneNumber = builder.phoneNumber;
    }

    private void validate(Builder builder) {
        if (builder.id == null)
            throw new Invalid{Name}Exception("ID is required");
        // Más validaciones de negocio...
    }

    public static Builder builder() { return new Builder(); }

    // Getters
    public UUID getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }

    public static class Builder {
        private UUID id;
        private String phoneNumber;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Lessor build() { return new Lessor(this); }
    }
}
```

### 1.2 Domain Repository Interfaces

**Características:**
- Solo interfaces
- Métodos de alto nivel
- Tipos de dominio (no JPA)

**Ejemplo:**
```java
package {feature}.domain.repositories;

import {feature}.domain.entities.{Entity};
import java.util.Optional;
import java.util.UUID;

public interface I{Entity}Repository {
    {Entity} save({Entity} entity);
    Optional<{Entity}> getById(UUID id);
    void deleteById(UUID id);
    // Métodos de consulta específicos del dominio
}
```

### 1.3 Domain Exceptions

**Características:**
- Extender `RuntimeException` o `DomainException`
- Mensajes descriptivos en inglés

**Ejemplo:**
```java
package {feature}.domain.exceptions;

public class Invalid{Entity}Exception extends RuntimeException {
    public Invalid{Entity}Exception(String message) {
        super(message);
    }
}
```

---

## CAPA 2: DATA (Infraestructura)

### 2.1 Data Entities (JPA)

**Características:**
- Anotaciones JPA
- Lombok para reducir boilerplate
- Mapeo a base de datos
- UUID como VARCHAR(50)

**Ejemplo:**
```java
package {feature}.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "{table_name}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {Entity}Entity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", length = 50)
    private UUID id;

    @Column(name = "column_name", nullable = false, length = X)
    private String fieldName;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "foreign_key_id")
    private RelatedEntity relatedEntity;
}
```

**Nota:** Para UUIDs siempre usar:
```java
@Id
@JdbcTypeCode(Types.VARCHAR)
@Column(name = "id", length = 50)
private UUID id;
```

### 2.2 Data Repository (JPA)

**Características:**
- Interface que extiende `JpaRepository`
- Queries personalizadas si es necesario

**Ejemplo:**
```java
package {feature}.data.repositories;

import {feature}.data.entities.{Entity}Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface {Entity}Repository extends JpaRepository<{Entity}Entity, UUID> {
    Optional<{Entity}Entity> findByFieldName(String fieldName);
    // Queries personalizadas...
}
```

### 2.3 Repository Adapter

**Características:**
- Implementa la interfaz del dominio
- Usa JpaRepository + Mapper
- Convierte entre Entity y Domain

**Ejemplo:**
```java
package {feature}.data.repositories;

import {feature}.data.entities.{Entity}Entity;
import {feature}.data.mappers.{Entity}Mapper;
import {feature}.domain.entities.{Entity};
import {feature}.domain.repositories.I{Entity}Repository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class {Entity}RepositoryAdapter implements I{Entity}Repository {

    private final {Entity}Repository repository;
    private final {Entity}Mapper mapper;

    public {Entity}RepositoryAdapter(
            {Entity}Repository repository,
            @Qualifier("{entity}DataMapper") {Entity}Mapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public {Entity} save({Entity} entity) {
        {Entity}Entity jpaEntity = mapper.toEntity(entity);
        {Entity}Entity saved = repository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<{Entity}> getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new {Entity}NotFoundException("Not found");
        }
        repository.deleteById(id);
    }
}
```

### 2.4 Data Mappers

**Características:**
- Convierte entre Entity (JPA) y Domain
- Nombrado: `{Entity}Mapper`
- Qualifier: `"{entity}DataMapper"`

**Ejemplo:**
```java
package {feature}.data.mappers;

import {feature}.data.entities.{Entity}Entity;
import {feature}.domain.entities.{Entity};
import org.springframework.stereotype.Component;

@Component("{entity}DataMapper")
public class {Entity}Mapper {

    public {Entity} toDomain({Entity}Entity entity) {
        if (entity == null) return null;

        return {Entity}.builder()
                .id(entity.getId())
                .fieldName(entity.getFieldName())
                .build();
    }

    public {Entity}Entity toEntity({Entity} domain) {
        if (domain == null) return null;

        return {Entity}Entity.builder()
                .id(domain.getId())
                .fieldName(domain.getFieldName())
                .build();
    }
}
```

### 2.5 DTOs (Request/Response)

**Request DTOs:**
```java
package {feature}.data.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {Operation}{Entity}Dto {

    @NotBlank(message = "El campo es obligatorio")
    private String fieldName;

    @Email(message = "El formato del correo es inválido")
    private String email;

    @Size(min = 8, message = "Mínimo 8 caracteres")
    private String password;
}
```

**Response DTOs:**
```java
package {feature}.data.dtos.response;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {Entity}ResponseDto {
    private UUID id;
    private String fieldName;
    // Solo campos necesarios para el cliente
}
```

---

## CAPA 3: SERVICES (Aplicación)

### 3.1 Service Interface

**Ejemplo:**
```java
package {feature}.services;

import {feature}.data.dtos.request.*;
import {feature}.data.dtos.response.*;
import java.util.UUID;

public interface I{Entity}Service {
    {Entity}ResponseDto create({Operation}Dto request);
    {Entity}ResponseDto getById(UUID id);
    void deleteById(UUID id);
}
```

### 3.2 Service Implementation

**Características:**
- Orquesta dominio + infraestructura
- Transacciones
- Manejo de excepciones

**Ejemplo:**
```java
package {feature}.services.impl;

import {feature}.domain.entities.{Entity};
import {feature}.domain.repositories.I{Entity}Repository;
import {feature}.services.I{Entity}Service;
import {feature}.services.mappers.{Entity}Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class {Entity}ServiceImpl implements I{Entity}Service {

    private final I{Entity}Repository repository;
    private final {Entity}Mapper mapper;

    public {Entity}ServiceImpl(
            I{Entity}Repository repository,
            @Qualifier("{entity}ServiceMapper") {Entity}Mapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public {Entity}ResponseDto create({Operation}Dto request) {
        // 1. Validar (si es necesario)

        // 2. Construir entidad de dominio
        {Entity} entity = {Entity}.builder()
                .id(UUID.randomUUID())
                .fieldName(request.getFieldName())
                .build();

        // 3. Guardar
        {Entity} saved = repository.save(entity);

        // 4. Retornar DTO
        return mapper.toResponseDto(saved);
    }

    // ... otros métodos
}
```

### 3.3 Service Mappers

**Características:**
- Convierte entre Entity (JPA) y DTOs
- Nombrado: `{Entity}Mapper`
- Qualifier: `"{entity}ServiceMapper"`

**Ejemplo:**
```java
package {feature}.services.mappers;

import {feature}.data.dtos.response.{Entity}ResponseDto;
import {feature}.data.entities.{Entity}Entity;
import org.springframework.stereotype.Component;

@Component("{entity}ServiceMapper")
public class {Entity}Mapper {

    public {Entity}ResponseDto toResponseDto({Entity}Entity entity) {
        if (entity == null) return null;

        return {Entity}ResponseDto.builder()
                .id(entity.getId())
                .fieldName(entity.getFieldName())
                .build();
    }
}
```

---

## CAPA 4: CONTROLLERS (Presentación)

**Características:**
- REST API
- Validación con `@Valid`
- Swagger/OpenAPI annotations
- BaseResponse wrapper

**Ejemplo:**
```java
package {feature}.controllers;

import {feature}.data.dtos.request.*;
import {feature}.data.dtos.response.*;
import {feature}.services.I{Entity}Service;
import aleosh.online.vivia.core.dtos.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/{entities}")
@Tag(name = "Gestión de {entities}", description = "Endpoints para CRUD de {entities}")
public class {Entity}Controller {

    private final I{Entity}Service service;

    public {Entity}Controller(I{Entity}Service service) {
        this.service = service;
    }

    @Operation(summary = "Crear {entity}",
               description = "Crea un nuevo registro de {entity}")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse<{Entity}ResponseDto>> create(
            @Valid @RequestBody {Operation}Dto request
    ) {
        {Entity}ResponseDto response = service.create(request);

        return new BaseResponse<>(
                true,
                response,
                "{Entity} creado exitosamente",
                HttpStatus.CREATED
        ).buildResponseEntity();
    }

    @Operation(summary = "Obtener {entity} por ID")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<{Entity}ResponseDto>> getById(
            @PathVariable UUID id
    ) {
        {Entity}ResponseDto response = service.getById(id);

        return new BaseResponse<>(
                true,
                response,
                "{Entity} encontrado",
                HttpStatus.OK
        ).buildResponseEntity();
    }

    @Operation(summary = "Eliminar {entity}")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.deleteById(id);

        return new BaseResponse<>(
                true,
                null,
                "{Entity} eliminado exitosamente",
                HttpStatus.OK
        ).buildResponseEntity();
    }
}
```

---

## Convenciones de Nombres

### Paquetes
- `domain.entities` - Entidades de dominio
- `domain.repositories` - Interfaces de repositorio
- `domain.exceptions` - Excepciones de negocio
- `data.entities` - Entidades JPA
- `data.repositories` - JpaRepository + Adapters
- `data.dtos.request` - DTOs de entrada
- `data.dtos.response` - DTOs de salida
- `data.mappers` - Mappers Data <-> Domain
- `services` - Interfaces de servicios
- `services.impl` - Implementaciones
- `services.mappers` - Mappers Entity <-> DTO
- `controllers` - REST Controllers

### Clases
- Domain Entity: `{Entity}`
- JPA Entity: `{Entity}Entity`
- Repository Interface (Domain): `I{Entity}Repository`
- JPA Repository: `{Entity}Repository`
- Repository Adapter: `{Entity}RepositoryAdapter`
- Service Interface: `I{Entity}Service`
- Service Impl: `{Entity}ServiceImpl`
- Controller: `{Entity}Controller`
- Request DTO: `{Operation}{Entity}Dto`
- Response DTO: `{Entity}ResponseDto`
- Data Mapper: `{Entity}Mapper` con `@Component("{entity}DataMapper")`
- Service Mapper: `{Entity}Mapper` con `@Component("{entity}ServiceMapper")`

---

## Ejemplo Completo: Property Feature

```
features/property/
├── domain/
│   ├── entities/
│   │   └── Property.java
│   ├── repositories/
│   │   └── IPropertyRepository.java
│   └── exceptions/
│       └── InvalidPropertyException.java
│
├── data/
│   ├── entities/
│   │   └── PropertyEntity.java
│   ├── repositories/
│   │   ├── PropertyRepository.java (JPA)
│   │   └── PropertyRepositoryAdapter.java
│   ├── dtos/
│   │   ├── request/
│   │   │   └── CreatePropertyDto.java
│   │   └── response/
│   │       └── PropertyResponseDto.java
│   └── mappers/
│       └── PropertyMapper.java (@Component("propertyDataMapper"))
│
├── services/
│   ├── IPropertyService.java
│   ├── impl/
│   │   └── PropertyServiceImpl.java
│   └── mappers/
│       └── PropertyMapper.java (@Component("propertyServiceMapper"))
│
└── controllers/
    └── PropertyController.java
```

---

## Checklist para Crear un Feature

- [ ] Domain Entity con Builder y validaciones
- [ ] Domain Repository Interface
- [ ] Domain Exceptions
- [ ] JPA Entity con anotaciones correctas
- [ ] JpaRepository interface
- [ ] Repository Adapter
- [ ] Data Mapper (Entity <-> Domain)
- [ ] Request DTOs con validaciones
- [ ] Response DTOs
- [ ] Service Interface
- [ ] Service Implementation
- [ ] Service Mapper (Entity <-> DTO)
- [ ] Controller con Swagger annotations
- [ ] Migraciones de base de datos (Flyway)

---

## Notas Importantes

1. **Nunca** exponer entidades JPA en controllers
2. **Siempre** validar en el dominio (entidades)
3. **Siempre** usar DTOs para request/response
4. **Usar** `@Transactional` en servicios que modifican datos
5. **Nombres** en inglés para código, español para mensajes de usuario
6. **UUIDs** siempre como VARCHAR(50) en base de datos
7. **Mappers** separados para data y services (diferentes responsabilidades)
8. **BaseResponse** para todas las respuestas de API
9. **Swagger** annotations para documentación automática
10. **Inmutabilidad** en entidades de dominio (campos final)
