# PCM gRPC Contracts

This module contains all gRPC service definitions (Protobuf files) for inter-service communication in PCM.

## Structure

```
src/main/proto/
├── common.proto              # Common types (errors, pagination)
├── profile_service.proto     # Profile Service API
├── consent_service.proto     # Consent Service API
├── segment_service.proto     # Segment Service API
└── preference_service.proto  # Preference Service API
```

## Code Generation

Protobuf and gRPC code is automatically generated during the Maven build:

```bash
mvn clean compile
```

Generated classes will be in:
- `target/generated-sources/protobuf/java/` - Protobuf messages
- `target/generated-sources/protobuf/grpc-java/` - gRPC service stubs

## Usage

### In Spring Boot Services

Add dependency to `pom.xml`:

```xml
<dependency>
    <groupId>dev.vibe-afrika</groupId>
    <artifactId>pcm-grpc-contracts</artifactId>
    <version>${project.version}</version>
</dependency>
```

Implement a gRPC service:

```java
@GrpcService
public class ProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {
    
    @Override
    public void getProfile(GetProfileRequest request, 
                          StreamObserver<GetProfileResponse> responseObserver) {
        // Implementation
    }
}
```

### In Quarkus Services

Add dependency to `pom.xml`:

```xml
<dependency>
    <groupId>dev.vibe-afrika</groupId>
    <artifactId>pcm-grpc-contracts</artifactId>
    <version>${project.version}</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-grpc</artifactId>
</dependency>
```

Implement a gRPC service:

```java
@GrpcService
public class SegmentGrpcService implements SegmentService {
    
    @Override
    public Uni<GetSegmentsResponse> getSegments(GetSegmentsRequest request) {
        // Implementation
    }
}
```

## Service Definitions

### Profile Service

- `GetProfile` - Get a profile by ID
- `GetProfiles` - Batch get profiles
- `GetProfileByHandle` - Get profile by handle
- `HandleExists` - Check if handle exists
- `GetProfileSummary` - Get lightweight profile info

### Consent Service

- `GetConsentStatus` - Get current consent for a purpose
- `GetAllConsents` - Get all consents for a profile
- `GetConsentHistory` - Get consent change history
- `VerifyConsent` - Verify if consent is granted

### Segment Service

- `GetSegments` - Get all segments for a profile
- `BelongsToSegment` - Check segment membership
- `GetProfilesInSegment` - Get profiles in a segment (paginated)

### Preference Service

- `GetPreferences` - Get all preferences
- `GetPreference` - Get a specific preference
- `UpdatePreferences` - Update preferences

## Best Practices

1. **Versioning**: Use package versioning (e.g., `v1`, `v2`) for breaking changes
2. **Backwards Compatibility**: Add new fields with high field numbers
3. **Documentation**: Document all messages and fields
4. **Error Handling**: Use `common.ErrorDetails` for consistent error responses
5. **Pagination**: Use `common.PaginationRequest/Response` for list operations

## Testing

Test gRPC services using `grpcurl`:

```bash
# List services
grpcurl -plaintext localhost:9090 list

# Call a method
grpcurl -plaintext -d '{"profile_id": "123", "tenant_id": "vibe-afrika"}' \
  localhost:9090 dev.vibeafrika.pcm.grpc.profile.ProfileService/GetProfile
```

## References

- [gRPC Java Documentation](https://grpc.io/docs/languages/java/)
- [Protocol Buffers Guide](https://protobuf.dev/programming-guides/proto3/)
- [gRPC Spring Boot Starter](https://github.com/grpc-ecosystem/grpc-spring)
- [Quarkus gRPC Guide](https://quarkus.io/guides/grpc)
