package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisAnnotatedCallMapper;
import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisCallMapper;
import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisProgramMapper;
import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisQueryParamMapperUtil;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisAnnotatedCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisEligibleApplicantsRegion;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisEligibleApplicantsScope;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisProgramme;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisTargetGroup;
import at.ac.tuwien.fundify.application.port.in.programs.ProgramAccessor;
import at.ac.tuwien.fundify.application.port.in.ris.api.PublishedAnnotatedCallAccessor;
import at.ac.tuwien.fundify.application.port.in.ris.api.PublishedCallAccessor;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.InvalidRisIdException;
import at.ac.tuwien.fundify.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import io.quarkus.security.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@ApplicationScoped
@JBossLog
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/ris-synergy/funding")
@RequiredArgsConstructor
@Authenticated
public class RisFundingResource {

    private final PublishedAnnotatedCallAccessor publishedAnnotatedCallAccessor;
    private final PublishedCallAccessor publishedCallAccessor;
    private final ProgramAccessor programAccessor;

    @GET
    @Path("/v1/fundings")
    @Operation(summary = "List fundings", description = "Returns a list of RIS fundings based on provided filters")
    @APIResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RisFunding.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.EXTERNAL_API_CLIENT})
    public List<RisFunding> list(
            @Parameter(description = "Funding type filter") @QueryParam("fundingType") RisFundingType risFundingType,
            @Parameter(description = "Target group filter") @QueryParam("targetGroup") RisTargetGroup risTargetGroup,
            @Parameter(description = "Filter for running calls") @QueryParam("runningCalls") Boolean runningCalls,
            @Parameter(description = "Region filter") @QueryParam("region") RisEligibleApplicantsRegion risRegion,
            @Parameter(description = "Funder ID filter") @QueryParam("funderId") String funderId,
            @Parameter(description = "Applicants scope filter") @QueryParam("applicantsScope") RisEligibleApplicantsScope risApplicantsScope,
            @QueryParam("page[page]") @DefaultValue("0") @Min(0) int page,
            @QueryParam("page[size]") @DefaultValue("20") @Min(0) int size) {
            List<RisFunding> all = getRisFundings(risFundingType, risTargetGroup, runningCalls, risRegion, funderId, risApplicantsScope);
            int fromIndex = page * size;
            if (fromIndex >= all.size()) {
                return List.of();
            }
            return all.subList(fromIndex, Math.min(fromIndex + size, all.size()));
    }

    @GET
    @Path("/v1/fundings/{fundingRisId}")
    @Operation(summary = "Get funding by its RIS ID", description = "Returns a specific RIS funding by its RIS ID")
    @APIResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RisFunding.class)))
    @APIResponse(responseCode = "400", description = "Invalid RIS ID supplied")
    @APIResponse(responseCode = "404", description = "Funding not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.EXTERNAL_API_CLIENT})
    public RisFunding get(@Parameter(description = "Funding RIS ID", required = true) @PathParam("fundingRisId") String fundingRisId)
        throws EntityNotFoundException, InvalidRisIdException {
            return getCallOrProgramByRisId(RisId.parseString(fundingRisId));
    }

    @GET
    @Path("/v1/annotated-calls/university/{universityRisId}")
    @Operation(summary = "List annotated RIS Calls for a University", description = "Returns a list of annotated RIS Calls for a specific university")
    @APIResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RisAnnotatedCall.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "400", description = "Invalid RisId format")
    @APIResponse(responseCode = "404", description = "University not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.EXTERNAL_API_CLIENT})
    public List<RisAnnotatedCall> listForUniversity (
            @Parameter(description = "University RIS ID", required = true) @PathParam("universityRisId") String universityRisId,
            @Parameter(description = "Funding type filter") @QueryParam("fundingType") RisFundingType risFundingType, // call or ongoing call
            @Parameter(description = "Target group filter") @QueryParam("targetGroup") RisTargetGroup risTargetGroup,
            @Parameter(description = "Filter for running calls") @QueryParam("runningCalls") Boolean runningCalls,
            @Parameter(description = "Region filter") @QueryParam("region") RisEligibleApplicantsRegion risRegion,
            @Parameter(description = "Funder ID filter") @QueryParam("funderId") String funderId,
            @Parameter(description = "Applicants scope filter") @QueryParam("applicantsScope") RisEligibleApplicantsScope risApplicantsScope)
        throws EntityNotFoundException, InvalidRisIdException {
            RisId universityId = RisId.parseString(universityRisId);
            return getAnnotatedRisCallsForUniversity(universityId, risFundingType, risTargetGroup, runningCalls, risRegion, funderId, risApplicantsScope);
    }

    @GET
    @Path("/v1/annotated-calls/university/{universityRisId}/call/{callRisId}")
    @Operation(summary = "Get annotated call for a university and call", description = "Returns a specific annotated call for a university and call combination")
    @APIResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RisAnnotatedCall.class)))
    @APIResponse(responseCode = "400", description = "Invalid RisId format")
    @APIResponse(responseCode = "404", description = "University or Call not found")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.EXTERNAL_API_CLIENT})
    public RisAnnotatedCall getAnnotatedCall(
            @Parameter(description = "University RIS ID", required = true) @PathParam("universityRisId") String universityRisId,
            @Parameter(description = "Call RIS ID", required = true) @PathParam("callRisId") String callRisId)
        throws InvalidRisIdException, EntityNotFoundException {
            AnnotatedCallDTO annotatedCallDTO = publishedAnnotatedCallAccessor.getPublishedByUniversityRisIdAndCallRisId(
                    RisId.parseString(universityRisId),
                    RisId.parseString(callRisId));
            return RisAnnotatedCallMapper.INSTANCE.fromDTO(annotatedCallDTO);
    }

    private List<RisAnnotatedCall> getAnnotatedRisCallsForUniversity(RisId universityId, RisFundingType risFundingType,
                                                                     RisTargetGroup risTargetGroup, Boolean runningCalls, RisEligibleApplicantsRegion risRegion,
                                                                     String risFunderId, RisEligibleApplicantsScope risApplicantsScope)
        throws EntityNotFoundException {
        ETargetGroup targetGroup = RisQueryParamMapperUtil.mapRisTargetGroupToETargetGroup(risTargetGroup);
        EAustrianState region = RisQueryParamMapperUtil.mapRisEligibleApplicantsRegionToEAustrianState(risRegion);
        ERegionalScope applicantsScope = RisQueryParamMapperUtil.mapRisEligibleApplicantsScopeToERegionalScope(risApplicantsScope);
        FunderId funderId = risFunderId != null ? new FunderId(risFunderId) : null;
        ECallType callType = risFundingType != null
                ? RisQueryParamMapperUtil.mapRisFundingTypeToECallType(risFundingType)
                : null;

        return new ArrayList<>(RisAnnotatedCallMapper.INSTANCE.fromDTO(
                publishedAnnotatedCallAccessor.listByUniversityAndCallFilter(
                        universityId,
                        callType,
                        targetGroup,
                        runningCalls,
                        region,
                        funderId,
                        applicantsScope
                )));
    }

    private List<RisFunding> getRisFundings(RisFundingType risFundingType, RisTargetGroup risTargetGroup, Boolean runningCalls, RisEligibleApplicantsRegion risRegion, String risFunderId, RisEligibleApplicantsScope risApplicantsScope) {
        List<RisFunding> fundings = new ArrayList<>();
        ETargetGroup targetGroup = RisQueryParamMapperUtil.mapRisTargetGroupToETargetGroup(risTargetGroup);
        EAustrianState region = RisQueryParamMapperUtil.mapRisEligibleApplicantsRegionToEAustrianState(risRegion);
        ERegionalScope applicantsScope = RisQueryParamMapperUtil.mapRisEligibleApplicantsScopeToERegionalScope(risApplicantsScope);
        FunderId funderId = risFunderId != null ? new FunderId(risFunderId) : null;

        // Fetch Programmes
        if (shouldFetchPrograms(risFundingType, runningCalls)) {
          List<RisProgramme> risPrograms = getRisPrograms(targetGroup, region, funderId,
              applicantsScope);
          fundings.addAll(risPrograms);
        }
        // Fetch Calls or Ongoing Calls
        if (shouldFetchCalls(risFundingType)) {
            fundings.addAll(getRisCalls(risFundingType, targetGroup, runningCalls, region, funderId, applicantsScope));
        }
        return fundings;
    }

    private List<RisProgramme> getRisPrograms(ETargetGroup targetGroup, EAustrianState region, FunderId funderId, ERegionalScope applicantsScope) {
        return RisProgramMapper.INSTANCE.fromDomain(
                programAccessor.listPublishedProgramsFilteredBy(
                        targetGroup,
                        region,
                        funderId,
                        applicantsScope
                ));
    }

    private List<RisCall> getRisCalls(RisFundingType risFundingType, ETargetGroup targetGroup, Boolean runningCalls, EAustrianState region, FunderId funderId, ERegionalScope applicantsScope) {
        ECallType callType = risFundingType != null
                ? RisQueryParamMapperUtil.mapRisFundingTypeToECallType(risFundingType)
                : null;

        return RisCallMapper.INSTANCE.fromDTO(
                publishedCallAccessor.listFilteredBy(
                        callType,
                        targetGroup,
                        runningCalls,
                        region,
                        funderId,
                        applicantsScope
                ));
    }

    private RisFunding getCallOrProgramByRisId(RisId risId) throws EntityNotFoundException {

        try {
            return RisCallMapper.INSTANCE.fromDTO(publishedCallAccessor.getByRisId(risId));
        } catch (EntityNotFoundException e) {
            return RisProgramMapper.INSTANCE.fromDomain(programAccessor.getProgramByRisIdAndStatus(risId, EPublicationStatus.PUBLISHED));
        }
    }

    private boolean shouldFetchPrograms(RisFundingType risFundingType, Boolean runningCalls) {
        return (risFundingType == null || risFundingType.equals(RisFundingType.PROGRAMME))
                && (runningCalls == null || !runningCalls);
    }

    private boolean shouldFetchCalls(RisFundingType risFundingType) {
        return risFundingType == null
                || risFundingType.equals(RisFundingType.CALL)
                || risFundingType.equals(RisFundingType.ONGOING_CALL);
    }

}