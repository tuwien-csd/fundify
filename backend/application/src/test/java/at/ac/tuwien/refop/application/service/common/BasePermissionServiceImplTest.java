package at.ac.tuwien.refop.application.service.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.domain.common.ERisIdType;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.funding.Funder;
import at.ac.tuwien.refop.domain.funding.FunderReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasePermissionServiceImplTest {

    @Mock
    private UserService userService;

    private BasePermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        permissionService = new BasePermissionServiceImpl(userService);
    }

    @Test
    void currentUserIsAffiliatedWith_String_match_isCaseInsensitive() {
        // arrange
        when(userService.getCurrentUserAffiliationId()).thenReturn("ffg");

        // act
        boolean result = permissionService.currentUserIsAffiliatedWith("FFG");

        // assert
        assertTrue(result);
    }

    @Test
    void currentUserIsAffiliatedWith_String_mismatch_returnsFalse() {
        // arrange
        when(userService.getCurrentUserAffiliationId()).thenReturn("FWF");
        when(userService.getCurrentUserIdAndName()).thenReturn("123 John Doe");

        // act
        boolean result = permissionService.currentUserIsAffiliatedWith("FFG");

        // assert
        assertFalse(result);
    }

    @Test
    void currentUserIsAffiliatedWith_String_nullAffiliation_returnsFalse_andNoLog() {
        // arrange
        when(userService.getCurrentUserAffiliationId()).thenReturn(null);

        // act
        boolean result = permissionService.currentUserIsAffiliatedWith("FFG");

        // assert
        assertFalse(result);
    }

    @Test
    void currentUserIsAffiliatedWith_FunderReference_delegatesToString_andMatches() {
        // arrange
        when(userService.getCurrentUserAffiliationId()).thenReturn("ffg");
        FunderReference funderRef = new FunderReference(
                new FunderId("ID-1"),
                "funder@example.org",
                new RisId("RIS", ERisIdType.ORGUNIT, "RID"),
                List.of(),
                "https://funder.example.org",
                "FFG",
                List.of()
        );

        // act
        boolean result = permissionService.currentUserIsAffiliatedWith(funderRef);

        // assert
        assertTrue(result);
    }

    @Test
    void currentUserIsAffiliatedWith_Funder_delegatesToString_andMatches() {
        // arrange
        when(userService.getCurrentUserAffiliationId()).thenReturn("ffg");
        Funder funder = Mockito.mock(Funder.class);
        when(funder.getAcronym()).thenReturn("FFG");

        // act
        boolean result = permissionService.currentUserIsAffiliatedWith(funder);

        // assert
        assertTrue(result);
    }
}
