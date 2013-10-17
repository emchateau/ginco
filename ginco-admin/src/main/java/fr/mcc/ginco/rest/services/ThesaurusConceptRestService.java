/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.rest.services;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.Nullable;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import fr.mcc.ginco.beans.Alignment;
import fr.mcc.ginco.beans.AssociativeRelationship;
import fr.mcc.ginco.beans.ConceptHierarchicalRelationship;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.beans.ThesaurusTerm;
import fr.mcc.ginco.exceptions.BusinessException;
import fr.mcc.ginco.exceptions.TechnicalException;
import fr.mcc.ginco.extjs.view.ExtJsonFormLoadData;
import fr.mcc.ginco.extjs.view.pojo.AlignmentView;
import fr.mcc.ginco.extjs.view.pojo.AssociativeRelationshipView;
import fr.mcc.ginco.extjs.view.pojo.GenericRoleView;
import fr.mcc.ginco.extjs.view.pojo.GenericStatusView;
import fr.mcc.ginco.extjs.view.pojo.HierarchicalRelationshipView;
import fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptReducedView;
import fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView;
import fr.mcc.ginco.extjs.view.utils.AlignmentViewConverter;
import fr.mcc.ginco.extjs.view.utils.AssociativeRelationshipViewConverter;
import fr.mcc.ginco.extjs.view.utils.ChildrenGenerator;
import fr.mcc.ginco.extjs.view.utils.HierarchicalRelationshipViewConverter;
import fr.mcc.ginco.extjs.view.utils.TermViewConverter;
import fr.mcc.ginco.extjs.view.utils.ThesaurusConceptViewConverter;
import fr.mcc.ginco.log.Log;
import fr.mcc.ginco.services.IIndexerService;
import fr.mcc.ginco.services.IThesaurusConceptService;
import fr.mcc.ginco.services.IThesaurusTermService;
import fr.mcc.ginco.utils.DateUtil;
import fr.mcc.ginco.utils.LabelUtil;


/**
 * Thesaurus Concept REST service for all operation on a thesaurus' concepts
 *
 */
@Service
@Path("/thesaurusconceptservice")
@Produces({ MediaType.APPLICATION_JSON })
@PreAuthorize("isAuthenticated()")
public class ThesaurusConceptRestService {

	@Inject
	@Named("thesaurusTermService")
	private IThesaurusTermService thesaurusTermService;

	@Inject
	@Named("thesaurusConceptService")
	private IThesaurusConceptService thesaurusConceptService;

	@Inject
	@Named("termViewConverter")
	private TermViewConverter termViewConverter;

	@Inject
	@Named("thesaurusConceptViewConverter")
	private ThesaurusConceptViewConverter thesaurusConceptViewConverter;

    @Inject
    @Named("associativeRelationshipViewConverter")
    private AssociativeRelationshipViewConverter associativeRelationshipViewConverter;

    @Inject
    @Named("hierarchicalRelationshipViewConverter")
    private HierarchicalRelationshipViewConverter hierarchicalRelationshipViewConverter;

    @Inject
    @Named("alignmentViewConverter")
    private AlignmentViewConverter alignmentViewConverter;

	@Inject
    @Named("indexerService")
    private IIndexerService indexerService;

	@Log
	private Logger logger;

	/**
	 * Public method used to get
	 * {@link fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView} object by
	 * providing its id.
	 *
	 * @param conceptId
	 *            {@link String} identifier to try with
	 *
	 * @return {@link fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView} object
	 *         in JSON format or {@code null} if not found
	 * @throws BusinessException
	 */
	@GET
	@Path("/getConcept")
	@Produces({ MediaType.APPLICATION_JSON })
	public ThesaurusConceptView getConceptById(
			@QueryParam("id") String conceptId) throws BusinessException {

        String resultId = "";

        if(conceptId.startsWith(ChildrenGenerator.ID_PREFIX)) {
            resultId = conceptId.substring(
                    conceptId.indexOf(ChildrenGenerator.PARENT_SEPARATOR)
                            + ChildrenGenerator.PARENT_SEPARATOR.length());
        } else {
        	resultId = conceptId;
        }

        List<ThesaurusTerm> terms = new ArrayList<ThesaurusTerm>();
        logger.debug("Trying to load concept with id = " + resultId);

		terms = thesaurusTermService.getTermsByConceptId(resultId);
		return thesaurusConceptViewConverter.convert(
				thesaurusConceptService.getThesaurusConceptById(resultId),
				terms);
	}

	/**
	 * Public method used to create or update a concept
	 *
	 * @throws BusinessException
	 */
	@POST
	@Path("/updateConcept")
	@Consumes({ MediaType.APPLICATION_JSON })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ThesaurusConceptView updateConcept(
			ThesaurusConceptView conceptView)
			throws BusinessException, TechnicalException {

		ThesaurusConcept convertedConcept = thesaurusConceptViewConverter
				.convert(conceptView);

		//This method gets from the concept view a list of concepts we have to detach (because they are not still children of the concept we update)
		List<ThesaurusConcept> childrenToDetach = thesaurusConceptViewConverter
				.convertRemovedChildren(conceptView);

		List<ThesaurusTerm> terms = termViewConverter
				.convertTermViewsInTerms(conceptView
                        .getTerms(), true);
		logger.info("Number of converted terms : " + terms.size());


		//We get associative relationships from the view
        List<AssociativeRelationship> associations = new ArrayList<AssociativeRelationship>();
        for(AssociativeRelationshipView view : conceptView.getAssociatedConcepts()) {
            associations.add(associativeRelationshipViewConverter.convert(view, convertedConcept));
        }

        //We get hierarchical relationships (from child to parents) from the view
        List<ConceptHierarchicalRelationship> hierarchicalRelationships = new ArrayList<ConceptHierarchicalRelationship>();
        if (conceptView.getParentConcepts() != null) {
        	for (HierarchicalRelationshipView hierarchicalRelationView : conceptView.getParentConcepts()) {
        		hierarchicalRelationships.add(hierarchicalRelationshipViewConverter.convertRelationFromChildToParent(hierarchicalRelationView, convertedConcept));
        	}
        }


        //We get alignments from the view
        List<Alignment> alignments = new ArrayList<Alignment>();
        if (conceptView.getAlignments() != null) {
        	for (AlignmentView alignmentView : conceptView.getAlignments()) {
        		alignments.add(alignmentViewConverter.convertAlignmentView(alignmentView, convertedConcept));
        	}
        }

        //We save or update the concept
        logger.info("Saving concept in DB");
		ThesaurusConcept returnConcept = thesaurusConceptService
				.updateThesaurusConcept(convertedConcept, terms, associations, hierarchicalRelationships, childrenToDetach, alignments);

		for (ThesaurusTerm term : terms) {
			indexerService.addTerm(term);
		}
		indexerService.addConcept(convertedConcept);
		// Return ThesaurusConceptView created/updated
		return thesaurusConceptViewConverter.convert(returnConcept, terms);
	}

    @GET
    @Path("/getAssociations")
    @Produces({MediaType.APPLICATION_JSON})
    public List<AssociativeRelationship> getAssociativeRelationshipsByConceptId(
            @QueryParam("conceptId")String conceptId) {

        ThesaurusConcept concept = thesaurusConceptService.getThesaurusConceptById(conceptId);
        return (List<AssociativeRelationship>) concept.getAssociativeRelationshipRight();
    }

    @GET
    @Path("/getConcepts")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<ThesaurusConceptReducedView> getConceptsByThesaurusId(
            @QueryParam("id") String conceptId,
            @QueryParam("thesaurusId") String thesaurusId,
            @QueryParam("searchOrphans") @Nullable String searchOrphans,
            @QueryParam("onlyValidatedConcepts") @Nullable Boolean onlyValidatedConcepts)
            throws BusinessException {

        Boolean searchOrphanParam;

        if(searchOrphans == null) {
            searchOrphanParam = null;
        } else if (searchOrphans.isEmpty()) {
            searchOrphanParam = null;
        } else {
            searchOrphanParam = Boolean.parseBoolean(searchOrphans);
        }

        boolean onlyValidated = onlyValidatedConcepts;
        if (onlyValidatedConcepts == null) {
        	//By default, if the value is not set, we return all concepts
        	onlyValidated = false;
        }

        return thesaurusConceptViewConverter
                .convert(thesaurusConceptService.getConceptsByThesaurusId(conceptId, thesaurusId,
                        searchOrphanParam, onlyValidated));
    }

    @GET
    @Path("/getSimpleConcepts")
    @Produces({ MediaType.APPLICATION_JSON })
    public  ExtJsonFormLoadData<List<ThesaurusConceptReducedView> > getSimpleConcepts(@QueryParam("conceptIds") List<String> associatedConcepts)
            throws BusinessException {
    	List<ThesaurusConceptReducedView> reducedConcepts = new ArrayList<ThesaurusConceptReducedView>();
    	for (String conceptId: associatedConcepts) {
    		ThesaurusConcept concept = thesaurusConceptService.getThesaurusConceptById(conceptId);
    		reducedConcepts.add(thesaurusConceptViewConverter.convert(concept));
    	}
    	 return new ExtJsonFormLoadData<List<ThesaurusConceptReducedView>>(reducedConcepts);
    }

    @GET
    @Path("/getSimpleChildrenConcepts")
    @Produces({ MediaType.APPLICATION_JSON })
    public  ExtJsonFormLoadData<List<ThesaurusConceptReducedView> > getSimpleChildrenConcepts(@QueryParam("conceptId") String conceptId)
            throws BusinessException {
        List<ThesaurusConceptReducedView> reducedConcepts = new ArrayList<ThesaurusConceptReducedView>();
        List<ThesaurusConcept> children = thesaurusConceptService.getChildrenByConceptId(conceptId);
        for (ThesaurusConcept child : children) {
            reducedConcepts.add(thesaurusConceptViewConverter.convert(child));
        }
        return new ExtJsonFormLoadData<List<ThesaurusConceptReducedView>>(reducedConcepts);
    }

    @GET
    @Path("/getAvailableConceptsOfArray")
    @Produces({ MediaType.APPLICATION_JSON })
    public  ExtJsonFormLoadData<List<ThesaurusConceptReducedView> > getAvailableConceptsOfArray(
    		@QueryParam("arrayId") String arrayId,
    		@QueryParam("thesaurusId") String thesaurusId
    		)
            throws BusinessException {

        List<ThesaurusConceptReducedView> reducedConcepts = new ArrayList<ThesaurusConceptReducedView>();
        List<ThesaurusConcept> children = thesaurusConceptService.getAvailableConceptsOfArray(arrayId, thesaurusId);
        for (ThesaurusConcept child : children) {
            reducedConcepts.add(thesaurusConceptViewConverter.convert(child));
        }
        return new ExtJsonFormLoadData<List<ThesaurusConceptReducedView>>(reducedConcepts);
    }

	@GET
	@Path("/getAvailableConceptsOfGroup")
	@Produces({ MediaType.APPLICATION_JSON })
	public ExtJsonFormLoadData<List<ThesaurusConceptReducedView>> getAvailableConceptsOfGroup(
			@QueryParam("groupId") String groupId,
			@QueryParam("thesaurusId") String thesaurusId)
			throws BusinessException {

		List<ThesaurusConceptReducedView> reducedConcepts = new ArrayList<ThesaurusConceptReducedView>();
		List<ThesaurusConcept> availableGroupConcepts = thesaurusConceptService
				.getAvailableConceptsOfGroup(groupId, thesaurusId);
		for (ThesaurusConcept concept : availableGroupConcepts) {
			reducedConcepts.add(thesaurusConceptViewConverter.convert(concept));
		}
		return new ExtJsonFormLoadData<List<ThesaurusConceptReducedView>>(
				reducedConcepts);
	}

    /**
	 * Public method used to delete
	 * {@link fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView} -
     * thesaurus term JSON object send by extjssearchOrphanParam
     *
	 * @return {@link fr.mcc.ginco.extjs.view.pojo.ThesaurusConceptView} deleted object
	 *         in JSON format or {@code null} if not found
	 * @throws BusinessException
	 */
	@POST
	@Path("/destroyConcept")
	@Consumes({ MediaType.APPLICATION_JSON })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void destroyConcept(ThesaurusConceptView thesaurusViewJAXBElement) throws BusinessException {
		ThesaurusConcept object = thesaurusConceptService
				.getThesaurusConceptById(thesaurusViewJAXBElement.getIdentifier());
		object.setModified(DateUtil.nowDate());
		if (object != null) {
			thesaurusConceptService.destroyThesaurusConcept(object);
			indexerService.removeConcept(object);
		}
	}

	/**
	 * Public method to get all status for concept (id + label)
	 * The types are read from a properties file
	 * @throws BusinessException
	 */
	@GET
	@Path("/getAllConceptStatus")
	@Produces({MediaType.APPLICATION_JSON})
	public ExtJsonFormLoadData<List<GenericStatusView>> getAllConceptStatus() throws BusinessException {
		List<GenericStatusView> listOfStatus = new ArrayList<GenericStatusView>();

		try {
			String availableStatusIds[] = LabelUtil.getResourceLabel("concept-status").split(",");

			if ("".equals(availableStatusIds[0])) {
				//Ids of status for concepts are not set correctly
				throw new BusinessException("Error with property file - check values of identifier concept status", "check-values-of-concept-status");
			}

            GenericStatusView firstEmpty = new GenericStatusView();
            firstEmpty.setStatus(-1);
            firstEmpty.setStatusLabel("-");
            listOfStatus.add(firstEmpty);

	        for (String id : availableStatusIds) {
	        	GenericStatusView conceptStatusView = new GenericStatusView();
	        	conceptStatusView.setStatus(Integer.valueOf(id));

	        	String label = LabelUtil.getResourceLabel("concept-status["+ id +"]");
	        	if (label.isEmpty()) {
	        		//Labels of status are not set correctly
	        		throw new BusinessException("Error with property file - check values of identifier concept status", "check-values-of-concept-status");
				} else {
					conceptStatusView.setStatusLabel(label);
				}
	        	listOfStatus.add(conceptStatusView);
			}
		} catch (MissingResourceException e) {
			throw new BusinessException("Error with property file - check values of concept status", "check-values-of-concept-status", e);
		}
		ExtJsonFormLoadData<List<GenericStatusView>> result = new ExtJsonFormLoadData<List<GenericStatusView>>(listOfStatus);
        result.setTotal((long) listOfStatus.size());
		return result;
	}

	/**
	 * Public method to get all roles for hierarchical relationships between concepts
	 * The types are read from a properties file
	 * @throws BusinessException
	 */
	@GET
	@Path("/getAllHierarchicalRelationRoles")
	@Produces({MediaType.APPLICATION_JSON})
	public ExtJsonFormLoadData<List<GenericRoleView>> getAllHierarchicalRelationRoles() throws BusinessException {
		List<GenericRoleView> listOfRoles = new ArrayList<GenericRoleView>();

		try {
			String availableRoleIds[] = LabelUtil.getResourceLabel("hierarchical-role").split(",");

			if ("".equals(availableRoleIds[0])) {
				//Ids of roles for hierarchical relationships are not set correctly
				throw new BusinessException("Error with property file - check values of identifiers for hierarchical relationships", "check-values-of-hierarchical-relationships");
			}

	        for (String id : availableRoleIds) {
	        	GenericRoleView roleView = new GenericRoleView();
	        	roleView.setRole(Integer.valueOf(id));

	        	String label = LabelUtil.getResourceLabel("hierarchical-role["+ id +"]");
	        	if (label.isEmpty()) {
	        		//Labels of status are not set correctly
	        		throw new BusinessException("Error with property file - check values of identifiers for hierarchical relationships", "check-values-of-hierarchical-relationships");
				} else {
					roleView.setRoleLabel(label);
				}
	        	listOfRoles.add(roleView);
			}
		} catch (MissingResourceException e) {
			throw new BusinessException("Error with property file - check values of identifiers for hierarchical relationships", "check-values-of-hierarchical-relationships", e);
		}
		ExtJsonFormLoadData<List<GenericRoleView>> result = new ExtJsonFormLoadData<List<GenericRoleView>>(listOfRoles);
        result.setTotal((long) listOfRoles.size());
		return result;
	}
	
	@GET
	@Path("/getConceptThesaurusId")
	@Produces({MediaType.APPLICATION_JSON})
	public ExtJsonFormLoadData<String> getConceptThesaurusId (@QueryParam("id") String conceptId) {
		return new ExtJsonFormLoadData<String>(thesaurusConceptService.getThesaurusConceptById(conceptId).getThesaurusId());
	}
}
