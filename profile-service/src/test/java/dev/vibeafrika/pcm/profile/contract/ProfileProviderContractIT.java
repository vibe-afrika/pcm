package dev.vibeafrika.pcm.profile.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import dev.vibeafrika.pcm.profile.application.usecase.GetProfileUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Provider("profile-service")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PactFolder("../api-gateway/target/pacts") // Point to consumer's generated pact
@ExtendWith(SpringExtension.class)
public class ProfileProviderContractIT {

    @LocalServerPort
    int port;

    @MockBean
    private GetProfileUseCase getProfileUseCase;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("A profile exists with ID 12345")
    void toDefaultState() {
        dev.vibeafrika.pcm.profile.application.dto.ProfileResponse response = dev.vibeafrika.pcm.profile.application.dto.ProfileResponse
                .builder()
                .id(java.util.UUID.fromString("00000000-0000-0000-0000-000000012345"))
                .tenantId("default")
                .handle("test_user")
                .attributes(new java.util.HashMap<>())
                .build();

        org.mockito.Mockito.when(getProfileUseCase.execute(org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);
    }
}
