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
package fr.mcc.ginco.extjs.view.pojo;

import java.util.List;

public class AlignmentView {

	private String identifier;
	private String created;
	private String modified;
	private Boolean andRelation;
	private Integer alignmentType;
	private String internalThesaurusId;
	private List<ExternalThesaurusView> externalThesaurus;
    private List<AlignmentConceptView> targetConcepts;
	private List<AlignmentResourceView> targetResources;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public Boolean getAndRelation() {
		return andRelation;
	}

	public void setAndRelation(Boolean andRelation) {
		this.andRelation = andRelation;
	}

	public Integer getAlignmentType() {
		return alignmentType;
	}

	public void setAlignmentType(Integer alignmentType) {
		this.alignmentType = alignmentType;
	}

	public String getInternalThesaurusId() {
		return internalThesaurusId;
	}

	public void setInternalThesaurusId(String internalThesaurusId) {
		this.internalThesaurusId = internalThesaurusId;
	}	   

	public List<ExternalThesaurusView> getExternalThesaurus() {
		return externalThesaurus;
	}

	public void setExternalThesaurus(List<ExternalThesaurusView> externalThesaurus) {
		this.externalThesaurus = externalThesaurus;
	}

	public List<AlignmentConceptView> getTargetConcepts() {
        return targetConcepts;
    }

    public void setTargetConcepts(List<AlignmentConceptView> targetConcepts) {
        this.targetConcepts = targetConcepts;
    }

	public List<AlignmentResourceView> getTargetResources() {
		return targetResources;
	}

	public void setTargetResources(List<AlignmentResourceView> targetResources) {
		this.targetResources = targetResources;
	}
}
