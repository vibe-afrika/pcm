package dev.vibeafrika.pcm.profile.application.usecase;

import dev.vibeafrika.pcm.common.domain.TenantId;
import dev.vibeafrika.pcm.profile.application.dto.CreateProfileCommand;
import dev.vibeafrika.pcm.profile.application.dto.ProfileResponse;
import dev.vibeafrika.pcm.profile.application.service.PIIProtectionService;
import dev.vibeafrika.pcm.profile.domain.event.ProfileCreatedEvent;
import dev.vibeafrika.pcm.profile.domain.event.ProfileEventPublisher;
import dev.vibeafrika.pcm.profile.domain.model.Handle;
import dev.vibeafrika.pcm.profile.domain.model.Profile;
import dev.vibeafrika.pcm.profile.domain.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProfileUseCaseTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileEventPublisher eventPublisher;

    @Mock
    private PIIProtectionService piiProtectionService;

    private CreateProfileUseCase createProfileUseCase;

    @BeforeEach
    void setUp() {
        createProfileUseCase = new CreateProfileUseCase(profileRepository, eventPublisher, piiProtectionService);
    }

    @Test
    void shouldCreateProfileWithProvidedId() {
        // Arrange
        UUID providedId = UUID.randomUUID();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", "John");

        CreateProfileCommand command = CreateProfileCommand.builder()
                .id(providedId.toString())
                .handle("john_doe")
                .attributes(attributes)
                .build();

        CreateProfileUseCase.Input input = new CreateProfileUseCase.Input(TenantId.of("test-tenant"), command);

        when(profileRepository.existsByHandle(any())).thenReturn(false);
        when(piiProtectionService.protect(any())).thenReturn(attributes);
        when(piiProtectionService.unprotect(any())).thenReturn(attributes);
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProfileResponse response = createProfileUseCase.execute(input);

        // Assert
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());

        Profile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getId()).isEqualTo(providedId);
        assertThat(savedProfile.getHandle().getValue()).isEqualTo("john_doe");
        verify(eventPublisher).publish(any(ProfileCreatedEvent.class));
        assertThat(response.getId()).isEqualTo(providedId);
    }

    @Test
    void shouldCreateProfileWithRandomIdWhenNotProvided() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        CreateProfileCommand command = CreateProfileCommand.builder()
                .handle("jane_doe")
                .attributes(attributes)
                .build();

        CreateProfileUseCase.Input input = new CreateProfileUseCase.Input(TenantId.of("test-tenant"), command);

        when(profileRepository.existsByHandle(any())).thenReturn(false);
        when(piiProtectionService.protect(any())).thenReturn(attributes);
        when(piiProtectionService.unprotect(any())).thenReturn(attributes);
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProfileResponse response = createProfileUseCase.execute(input);

        // Assert
        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());

        Profile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getId()).isNotNull();
        assertThat(savedProfile.getHandle().getValue()).isEqualTo("jane_doe");
        assertThat(response.getId()).isEqualTo(savedProfile.getId());
    }

    @Test
    void shouldThrowExceptionWhenHandleAlreadyExists() {
        // Arrange
        CreateProfileCommand command = CreateProfileCommand.builder()
                .handle("existing_handle")
                .attributes(new HashMap<>())
                .build();

        CreateProfileUseCase.Input input = new CreateProfileUseCase.Input(TenantId.of("test-tenant"), command);

        when(profileRepository.existsByHandle(any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> createProfileUseCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Handle already exists");

        verify(profileRepository, never()).save(any());
    }
}
