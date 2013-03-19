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
package fr.mcc.ginco.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusArray;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.beans.ThesaurusTerm;
import fr.mcc.ginco.dao.IThesaurusArrayDAO;
import fr.mcc.ginco.dao.IThesaurusConceptDAO;
import fr.mcc.ginco.dao.IThesaurusDAO;
import fr.mcc.ginco.dao.IThesaurusTermDAO;
import fr.mcc.ginco.enums.ConceptStatusEnum;
import fr.mcc.ginco.enums.TermStatusEnum;
import fr.mcc.ginco.exceptions.BusinessException;
import fr.mcc.ginco.log.Log;
import fr.mcc.ginco.utils.LabelUtil;

/**
 * Implementation of the thesaurus concept service. Contains methods relatives
 * to the ThesaurusConcept object
 */
@Transactional
@Service("thesaurusConceptService")
public class ThesaurusConceptServiceImpl implements IThesaurusConceptService {

	@Log
	private Logger logger;

	@Inject
	@Named("thesaurusConceptDAO")
	private IThesaurusConceptDAO thesaurusConceptDAO;

	@Inject
	@Named("thesaurusDAO")
	private IThesaurusDAO thesaurusDAO;

	@Inject
	@Named("thesaurusTermDAO")
	private IThesaurusTermDAO thesaurusTermDAO;

	@Inject
	@Named("thesaurusArrayDAO")
	private IThesaurusArrayDAO thesaurusArrayDAO;	

	@Value("${ginco.default.language}")
	private String defaultLang;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.mcc.ginco.IThesaurusConceptService#getThesaurusConceptList()
	 */
	@Override
	public List<ThesaurusConcept> getThesaurusConceptList() {
		return thesaurusConceptDAO.findAll();
	}

	@Override
	public Set<ThesaurusConcept> getThesaurusConceptList(List<String> list)
			throws BusinessException {
		Set<ThesaurusConcept> result = new HashSet<ThesaurusConcept>();
		for (String id : list) {
			ThesaurusConcept concept = thesaurusConceptDAO.getById(id);
			if (concept == null) {
				throw new BusinessException("The concept " + id
						+ " does not exist!", "concept-does-not-exist");
			} else {
				if (!result.contains(concept)) {
					result.add(concept);
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.mcc.ginco.IThesaurusConceptService#getThesaurusConceptById(java.lang
	 * .String)
	 */
	@Override
	public ThesaurusConcept getThesaurusConceptById(String id) {
		return thesaurusConceptDAO.getById(id);
	}

	@Override
	public List<ThesaurusConcept> getOrphanThesaurusConcepts(String thesaurusId)
			throws BusinessException {
		Thesaurus thesaurus = checkThesaurusId(thesaurusId);
		return thesaurusConceptDAO.getOrphansThesaurusConcept(thesaurus);
	}

	@Override
	public long getOrphanThesaurusConceptsCount(String thesaurusId)
			throws BusinessException {
		Thesaurus thesaurus = checkThesaurusId(thesaurusId);
		return thesaurusConceptDAO.getOrphansThesaurusConceptCount(thesaurus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.mcc.ginco.IThesaurusConceptService#getTopTermThesaurusConcept(java
	 * .lang.String)
	 */
	@Override
	public List<ThesaurusConcept> getTopTermThesaurusConcepts(String thesaurusId)
			throws BusinessException {
		Thesaurus thesaurus = checkThesaurusId(thesaurusId);
		return thesaurusConceptDAO.getTopTermThesaurusConcept(thesaurus);
	}

	@Override
	public long getTopTermThesaurusConceptsCount(String thesaurusId)
			throws BusinessException {
		Thesaurus thesaurus = checkThesaurusId(thesaurusId);
		return thesaurusConceptDAO.getTopTermThesaurusConceptCount(thesaurus);
	}

	@Override
	public List<ThesaurusConcept> getChildrenByConceptId(String conceptId) {
		return thesaurusConceptDAO.getChildrenConcepts(conceptId);
	}

	@Override
	public List<ThesaurusConcept> getConceptsByThesaurusId(
			String excludeConceptId, String thesaurusId, Boolean searchOrphans) {
		return thesaurusConceptDAO.getAllConceptsByThesaurusId(
				excludeConceptId, thesaurusId, searchOrphans);
	}

	@Override
	public boolean hasChildren(String conceptId) {
		return (thesaurusConceptDAO.getChildrenConcepts(conceptId).size() > 0);
	}

	@Override
	public List<ThesaurusConcept> getRootConcepts(ThesaurusConcept concept) {
		ThesaurusConcept start;
		HashMap<String, Integer> path = new HashMap<String, Integer>();
		Set<ThesaurusConcept> roots = new HashSet<ThesaurusConcept>();
		path.clear();
		roots.clear();
		path.put(concept.getIdentifier(), 0);
		start = concept;
		getRoot(concept, 0, start, path, roots);
		return new ArrayList<ThesaurusConcept>(roots);
	}

	@Override
	public void removeParents(ThesaurusConcept concept,
			List<String> parentsToRemove) throws BusinessException {
		Set<ThesaurusConcept> parents = getThesaurusConceptList(parentsToRemove);

		boolean isDefaultTopConcept = concept.getThesaurus()
				.isDefaultTopConcept();

		if (concept.getParentConcepts().size() == 1) {
			concept.getParentConcepts().clear();
			concept.setTopConcept(isDefaultTopConcept);
		} else {
			for (ThesaurusConcept parent : parents) {
				concept.getParentConcepts().remove(parent);
			}
		}
	}

	private void getRoot(ThesaurusConcept concept, Integer iteration,
			ThesaurusConcept start, Map<String, Integer> path,
			Set<ThesaurusConcept> roots) {
		iteration++;
		Set<ThesaurusConcept> directParents = concept.getParentConcepts();
		if (directParents.isEmpty()) {
			if (iteration != 1) {
				roots.add(concept);
			}
			return;
		}
		boolean flag = false;
		Set<ThesaurusConcept> stack = new HashSet<ThesaurusConcept>();
		for (ThesaurusConcept directParent : directParents) {
			if (path.containsKey(directParent.getIdentifier())) {
				continue;
			} else {
				path.put(directParent.getIdentifier(), iteration);
				stack.add(directParent);
				flag = true;
			}
		}

		// HACK to deal with cyclic dependencies. Should be reThink in some
		// time...
		if (!flag && directParents.size() == 1 && directParents.contains(start)) {
			roots.add(concept);
		}

		if (!stack.isEmpty()) {
			for (ThesaurusConcept toVisit : stack) {
				getRoot(toVisit, iteration, start, path, roots);
			}
			stack.clear();
		}
	}

	@Override
	public ThesaurusTerm getConceptPreferredTerm(String conceptId)
			throws BusinessException {

		logger.debug("ConceptId : " + conceptId);

		ThesaurusTerm preferredTerm = thesaurusTermDAO
				.getConceptPreferredTerm(conceptId);
		if (preferredTerm == null) {
			throw new BusinessException("The concept " + conceptId
					+ "has no preferred term",
					"concept-does-not-have-a-preferred-term");
		}
		return preferredTerm;
	}

	@Override
	public String getConceptLabel(String conceptId) throws BusinessException {
		ThesaurusTerm term = getConceptPreferredTerm(conceptId);
		return LabelUtil.getConceptLabel(term, defaultLang);
	}

	@Override
	public ThesaurusConcept updateThesaurusConcept(ThesaurusConcept object,
			List<ThesaurusTerm> terms) throws BusinessException {
		if (object.getStatus() == ConceptStatusEnum.CANDIDATE.getStatus()) {
			if (!object.getAssociativeRelationshipLeft().isEmpty() || !object.getAssociativeRelationshipRight().isEmpty() || !object.getParentConcepts().isEmpty() || hasChildren(object.getIdentifier())) {
				throw new BusinessException("A concept must not have a status candidate when it still have relations",
						"concept-status-candidate-relation");
			}
			
		}
		ThesaurusConcept concept = thesaurusConceptDAO.update(object);
		updateConceptTerms(concept, terms);
		return concept;
	}

	@Override
	public List<ThesaurusConcept> getAssociatedConcepts(String conceptId) {
		ThesaurusConcept concept = thesaurusConceptDAO.getById(conceptId);
		return thesaurusConceptDAO.getAssociatedConcepts(concept);
	}

	@Override
	public ThesaurusConcept destroyThesaurusConcept(ThesaurusConcept object)
			throws BusinessException {
		List<ThesaurusTerm> terms = thesaurusTermDAO
				.findTermsByConceptId(object.getIdentifier());
		for (ThesaurusTerm term : terms) {
			term.setConcept(null);
			thesaurusTermDAO.update(term);
		}

		List<ThesaurusConcept> childrenConcepts = getChildrenByConceptId(object
				.getIdentifier());
		for (ThesaurusConcept childConcept : childrenConcepts) {
			childConcept.getParentConcepts().remove(object);
			thesaurusConceptDAO.update(childConcept);
		}

		List<ThesaurusConcept> rootChildrenConcepts = thesaurusConceptDAO
				.getAllRootChildren(object);
		for (ThesaurusConcept rootChild : rootChildrenConcepts) {
			rootChild.getRootConcepts().remove(object);
			thesaurusConceptDAO.update(rootChild);
		}

		List<ThesaurusArray> arrays = thesaurusArrayDAO
				.getConceptSuperOrdinateArrays(object.getIdentifier());
		for (ThesaurusArray array : arrays) {
			thesaurusArrayDAO.delete(array);
		}

		return thesaurusConceptDAO.delete(object);
	}

	private void updateConceptTerms(ThesaurusConcept concept,
			List<ThesaurusTerm> terms) throws BusinessException {
		List<ThesaurusTerm> returnTerms = new ArrayList<ThesaurusTerm>();
		for (ThesaurusTerm thesaurusTerm : terms) {
			
			//We always set validated status to the terms that are joined to a concept
			thesaurusTerm.setStatus(TermStatusEnum.VALIDATED.getStatus());				
			thesaurusTerm.setConcept(concept);
			returnTerms.add(thesaurusTermDAO.update(thesaurusTerm));

		}
	}

	private Thesaurus checkThesaurusId(String thesaurusId)
			throws BusinessException {
		Thesaurus thesaurus = thesaurusDAO.getById(thesaurusId);
		if (thesaurus == null) {
			throw new BusinessException("Invalid thesaurusId : " + thesaurusId,
					"invalid-thesaurus-id");
		} else {
			logger.debug("thesaurus with id =  " + thesaurusId + " found");

		}
		return thesaurus;
	}

	@Async
	public void calculateChildrenRoot(String parentId) {
		logger.info("root concept calculation launched for parent concept id = "
				+ parentId);
		calculateChildrenRoots(parentId, parentId);
		logger.info("root concept calculation ended for parent concept id = "
				+ parentId);
	}

	private void calculateChildrenRoots(String parentId, String originalParentId) {
		List<ThesaurusConcept> childrenConcepts = getChildrenByConceptId(parentId);
		for (ThesaurusConcept concept : childrenConcepts) {
			if (concept.getIdentifier() != originalParentId) {
				logger.info("calculating root concept for chiled with concept Id : "
						+ concept.getIdentifier());
				List<ThesaurusConcept> thisChildRoots = getRootConcepts(concept);
				concept.setRootConcepts(new HashSet<ThesaurusConcept>(
						thisChildRoots));
				calculateChildrenRoots(concept.getIdentifier(),
						originalParentId);
			}
		}
	}

}
