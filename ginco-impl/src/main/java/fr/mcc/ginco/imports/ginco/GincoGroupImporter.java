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
package fr.mcc.ginco.imports.ginco;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import fr.mcc.ginco.beans.ThesaurusConceptGroup;
import fr.mcc.ginco.beans.ThesaurusConceptGroupLabel;
import fr.mcc.ginco.dao.INodeLabelDAO;
import fr.mcc.ginco.dao.IThesaurusConceptGroupDAO;
import fr.mcc.ginco.dao.IThesaurusConceptGroupLabelDAO;
import fr.mcc.ginco.exports.result.bean.GincoExportedThesaurus;
import fr.mcc.ginco.exports.result.bean.JaxbList;
import fr.mcc.ginco.log.Log;

/**
 * This class gives methods to import groups and groups labels
 *
 */
@Component("gincoGroupImporter")
public class GincoGroupImporter {
	
	@Inject
	@Named("nodeLabelDAO")
	private INodeLabelDAO nodeLabelDAO;
	
	@Inject
	@Named("thesaurusConceptGroupDAO")
	private IThesaurusConceptGroupDAO thesaurusConceptGroupDAO;
	
	@Inject
	@Named("thesaurusConceptGroupLabelDAO")
	private IThesaurusConceptGroupLabelDAO thesaurusConceptGroupLabelDAO;
	
	@Log
	private Logger logger;
	
	/**
	 * This method stores all the groups of the thesaurus included in the {@link GincoExportedThesaurus} object given in parameter
	 * @param exportedThesaurus
	 * @return The list of stored groups
	 */
	public List<ThesaurusConceptGroup> storeGroups(GincoExportedThesaurus exportedThesaurus) {
		List<ThesaurusConceptGroup> updatedGroups = new ArrayList<ThesaurusConceptGroup>();
		for (ThesaurusConceptGroup group : exportedThesaurus.getConceptGroups()) {
			group.setThesaurus(exportedThesaurus.getThesaurus());
			updatedGroups.add(thesaurusConceptGroupDAO.update(group));
		}
		return updatedGroups;
	}
	
	/**
	 * This method stores all the group labels of the thesaurus included in the {@link GincoExportedThesaurus} object given in parameter
	 * @param exportedThesaurus
	 * @return The list of stored group labels
	 */
	public List<ThesaurusConceptGroupLabel> storeGroupLabels(GincoExportedThesaurus exportedThesaurus) {
		Map<String, JaxbList<ThesaurusConceptGroupLabel>> labels = exportedThesaurus.getConceptGroupLabels();
		List<ThesaurusConceptGroupLabel> updatedLabels = new ArrayList<ThesaurusConceptGroupLabel>();
		
		if (labels != null && !labels.isEmpty()) {
			Iterator<Map.Entry<String,  JaxbList<ThesaurusConceptGroupLabel>>> entries = labels.entrySet().iterator();
			String groupId = null;
			List<ThesaurusConceptGroupLabel> groupLabels = null;
			while(entries.hasNext()){
				Map.Entry<String,  JaxbList<ThesaurusConceptGroupLabel>> entry = entries.next();
				//Getting the id of the group
				groupId = entry.getKey();
				
				//Getting the label for this group
				if (entry.getValue() != null && !entry.getValue().isEmpty()) {
					groupLabels = entry.getValue().getList();
				}
				
				for (ThesaurusConceptGroupLabel label : groupLabels) {
					label.setConceptGroup(thesaurusConceptGroupDAO.getById(groupId));
					updatedLabels.add(thesaurusConceptGroupLabelDAO.update(label));
				}
			}
		}
		return updatedLabels;
	}
}
